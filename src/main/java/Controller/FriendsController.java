package Controller;

import ClientAccountNetworking.OkClient;
import PeerNetworking.ConnectionManager;
import PeerNetworking.PeerConnection;
import QueryObjects.ChatRequest;
import QueryObjects.FriendData;
import QueryObjects.UserData;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//Source for ListView: https://www.youtube.com/watch?v=9uubyM6oHAY
public class FriendsController{
    private static final String FRIEND_ACCEPTED = "2";
    private static final long REQ_LISTEN_REFRESH = 2000;
    private static final long MAX_REQUEST_DIFF = 15000;
    private boolean acceptConnection = false;
    private boolean handlingRequest = false;
    private Lock friendLock = new ReentrantLock();
    private OkClient client = null;
    private UserData user = null;
    private ArrayList<ChatRequest> chatRequests = new ArrayList<>();
    //TODO: fList and friends can be merged?
    private ArrayList<FriendData> friends = new ArrayList<>();
    private Thread requestHandler = null;
    private PeerConnection nextPeer = null;
    private ChatRequest acceptedRequest = null;
    private ConnectionManager manager = null;
    private int nextPort = 9000;

    @FXML
    ScrollPane friendContainer;

    @FXML
    ListView<FriendData> friendsList = null;
    private ObservableList<FriendData> fList = FXCollections.observableArrayList();


    public void setNextPeer(PeerConnection peer){
        nextPeer = peer;
    }

    private synchronized UserData getUser() {
        return user;
    }

    private synchronized  void setHandlingRequest(boolean status){handlingRequest = status;}

    private synchronized  boolean getHandlingRequest(){return handlingRequest;}

    public void setAcceptConnection(boolean acceptConnection) {
        this.acceptConnection = acceptConnection;
    }

    class FriendDataCell extends ListCell<FriendData>{
        @Override
        protected void updateItem(FriendData friend, boolean empty) {
            super.updateItem(friend, empty);
            if (friend != null) {
                ContextMenu blockMenu = new ContextMenu();
                MenuItem block = new MenuItem();
                block.setText("Block User");
                block.setOnAction(event -> blockFriend(this.getItem()));
                blockMenu.getItems().add(block);

                ContextMenu pendingMenu = new ContextMenu();
                MenuItem accept = new MenuItem();
                accept.setText("Accept Request");
                accept.setOnAction(event -> acceptFriend(this.getItem()));
                MenuItem reject = new MenuItem();
                reject.setText("Reject Request");
                reject.setOnAction(event -> rejectFriend(this.getItem()));
                pendingMenu.getItems().addAll(accept, reject);

                if (friend == null) {
                    System.out.println("No friend");
                    this.setContextMenu(null);
                    return;
                }
                int friendStatus = Integer.parseInt(friend.requestStatus);
                if(friendStatus == 0){
                    this.setItem(null);
                    //friendsList.getItems().remove(friend);
                    int index = findFriendIndex(friend);
                    if (index >= 0) fList.remove(index);
                    //friendsList.refresh();
                    this.setContextMenu(null);
                }
                //pending
                else if (friendStatus == 1) {
                    //System.out.println("Found friend with status 1");
                    this.setContextMenu(pendingMenu);
                    this.textProperty().bind(Bindings.format("Request from %s", friend.friend_name));
                    this.setStyle("-fx-font-style: italic");
                }
                //accepted friend
                else if (friendStatus == 2) {
                    //System.out.println("Found friend with status 2");
                    if (friend.friend_name.equals("jadenBot")){
                        this.setContextMenu(null);
                    }
                    else {
                        this.setContextMenu(blockMenu);
                    }
                    this.textProperty().bind(Bindings.format("%s", friend.friend_name));
                    this.setStyle("-fx-font-style: normal");
                }
                //blocked or rejected request
                else {
                    this.setItem(null);
                    int index = findFriendIndex(friend);
                    if (index >= 0) fList.remove(index);
                    //friendsList.refresh();
                    //System.out.println("Request status: " + friend.requestStatus);
                }
            }
        }//end update
    }

    private synchronized FriendData findFriend(String friendName){
        if (fList.size() < 1){
            return null;
        }
        for (int i = 0; i < fList.size(); i++){
            FriendData temp = fList.get(i);
            if (temp.friend_name.equals(friendName)){
                return temp;
            }
        }
        //not found
        return null;
    }

    int findFriendIndex(FriendData friend){
        if (fList.size() < 1){
            return -1;
        }
        for (int i = 0; i < fList.size(); i++){
            FriendData temp = fList.get(i);
            if (temp.friendID == friend.friendID && temp.friend_name.equals(friend.friend_name)){
                return i;
            }
        }
        //not found
        return -1;
    }

    @FXML
    public void initialize(){
        client = new OkClient();
    }

    synchronized int updateFriendsList(){
        try {
            if (!friendLock.tryLock(500, TimeUnit.MILLISECONDS)){
                return -1;
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
            return -1;
        }
        if (getUser() == null) return -1;
        if (fList.size() != 0) fList.clear();
        if (friendsList != null) friendsList.getItems().clear();
        if (friends != null) friends.clear();
        try {
            String res = client.getFriends(getUser(), friends);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        //source: https://stackoverflow.com/questions/20936101/get-listcell-via-listview
        if (friends.size() > 0){
            //TODO: may need to remove blocked friends here
            fList.addAll(friends);
            friendsList.setItems(fList);
            friendsList.setCellFactory( new Callback<ListView<FriendData>, ListCell<FriendData>>() {
                @Override
                public ListCell<FriendData> call(ListView<FriendData> lv) {
                    FriendDataCell cell = new FriendDataCell();
                    return cell;
                }
            });
            friendsList.refresh();
        }
        friendLock.unlock();
        return 0;
    }

    void initData(UserData usr){
        System.out.println("Trace: in initData");
        user = usr;
        try {
            manager = ConnectionManager.getConnectionManager(user);
            nextPort = manager.getNextSocket();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        updateFriendsList();
        startRequestHandler();
    }

    private void startRequestHandler(){
        if(requestHandler != null){
            if (requestHandler.isAlive()){
                requestHandler.interrupt();
                try {
                    requestHandler.join();
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        requestHandler = new Thread(this::listenForRequests);
        requestHandler.setDaemon(true);
        requestHandler.start();
    }

    private void getChatRequests(){
        try {
            client.getChatRequests(getUser(), chatRequests);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void acceptFriend(FriendData friend){
        try {
            int check = client.acceptFriend(getUser(), friend.friend_name);
            if (check == 0) updateFriendsList();
            else{
                //TODO: popup error message
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        //System.out.println("Accepted: " + friend.friend_name);
    }

    private void rejectFriend(FriendData friend){
        try {
            int check = client.rejectFriend(getUser(), friend.friend_name);
            if (check == 0) updateFriendsList();
            else{
                //TODO: popup error message
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        //System.out.println("Rejected: " + friend.friend_name);
    }

    private void blockFriend(FriendData friend){
        try {
            int check = client.blockFriend(getUser(), friend.friend_name);
            if (check == 0) {
                //System.out.println("Blocked: " + friend.friend_name);
                updateFriendsList();
            }
            else{
                //TODO: popup error message
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void requestChat(ActionEvent actionEvent){
        friendLock.lock();
        if(getHandlingRequest()){
            //System.out.println("Handle existing chat requests before making a new one.");
            return;
        }
        setHandlingRequest(true);
        //get selected friend from listview
        FriendData friend = null;
        try {
            friend = friendsList.getSelectionModel().getSelectedItem();
            //check if friend is accepted
            if (!friend.requestStatus.equals(FRIEND_ACCEPTED)) {
                setHandlingRequest(false);
                friendLock.unlock();
                return;
            }
        }
        catch(NullPointerException e){
            setHandlingRequest(false);
            friendLock.unlock();
            return;
        }
        friendLock.unlock();
        //TODO: popup window letting person know you can't connect to people that aren't friends

        ChatRequest req;
        try {
            req = client.makeChatRequest(getUser(), friend.friend_name);
        }
        catch(IOException e){
            e.printStackTrace();
            setHandlingRequest(false);
            return;
            //TODO: popup error message
        }
        //openChatWindow will setHandlingRequest(false)
        openChatWindow(req);
    }

    public void addFriend(){
        //Source: https://stackoverflow.com/questions/15041760/javafx-open-new-window
        Parent root;
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addFriend.fxml"));
            root = loader.<Parent>load();
            AddFriendController controller = loader.<AddFriendController>getController();
            controller.initData(getUser());
            Stage stage = new Stage();
            stage.setTitle("Add Friend");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
        catch(IOException e){
            e.printStackTrace();
            //TODO: handle this better
        }
    }

    public synchronized void setUser(UserData usr){
        //System.out.println("Trace: setUser called");
        user = usr;
    }

    //Should only ever be called from checkRequests
    private synchronized void removeStaleRequests(String currentTime){
        for(ChatRequest req : chatRequests){
            if (getDateDifference(req.date, currentTime) > MAX_REQUEST_DIFF){
                chatRequests.remove(req);
            }
        }
    }

    //Should only be called by the checkRequests function
    // Returns difference in seconds between now and dateFormat if difference is within limit
    // Returns -1 if there is an error or difference is outside of allowable limit
    // Source: https://stackoverflow.com/a/20165708/2487475
    private synchronized int getDateDifference(String dateFormat, String currentTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMDD_HHmmss");
        try {
            Date request = format.parse(dateFormat);
            Date now = format.parse(currentTime);
            long diff = now.getTime() - request.getTime();
            //System.out.println("Difference: " + diff);
            return (int)diff;
        }
        catch(ParseException e){
            e.printStackTrace();
            //TODO: is there a better way to handle this?
            return -1;
        }
    }

    //Attempt connection
    //  returns 0 if no connection attempted
    //  returns 1 if connection is processing
    private int getUserRequestInput(ChatRequest req){
        //TODO: This may be its own thread eventually
        //TODO: set connection status?
        //System.out.println("Trace connect to request");
        Parent root;
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/acceptConnection.fxml"));
            root = loader.<Parent>load();
            AcceptConnectionController controller = loader.<AcceptConnectionController>getController();
            controller.initData(this, req);
            Stage stage = new Stage();
            stage.setTitle("Connection Request");
            stage.setScene(new Scene(root));
            setAcceptConnection(false);
            //Source: https://stackoverflow.com/a/34071134/2487475
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        }
        catch(IOException e){
            e.printStackTrace();
            //TODO: handle this better
        }
        if (acceptConnection){
            System.out.println("Connection accepted");
            acceptConnection = false;
            return 1;}
        else{ System.out.println("Connection refused"); return 0;}
    }

    private synchronized FriendData findFriendFromRequest(ChatRequest req){
        String friendName = "";
        //This user accepted the request
        if (req.targetUser.equals(user.username)){
            friendName = req.requestingUser;
        }
        //This user sent the request
        else{
            friendName = req.targetUser;
        }
        return findFriend(friendName);
    }

    private synchronized boolean requestsContains(ChatRequest req){
        if (chatRequests.size() == 0) return false;
        for (ChatRequest current : chatRequests){
            boolean equal = true;
            if (!current.requestingUser.equals(req.requestingUser)) equal = false;
            if (equal && !current.targetUser.equals(req.targetUser)) equal = false;
            if (equal && !current.date.equals(req.date)) equal = false;
            if (equal) return true;
        }
        return false;
    }

    private synchronized ChatRequest handleRequests(ArrayList<ChatRequest> newRequests, String currentTime){
        //For each request check if it occurred within the desired time frame
        //  and it has not been processed before
        for(ChatRequest req : newRequests) {
            int check = getDateDifference(req.date, currentTime);
            boolean contains = requestsContains(req);
            if (!contains && check > 0 && check < MAX_REQUEST_DIFF) {
                chatRequests.add(req);
                //Source: https://stackoverflow.com/a/13804542/2487475
                final FutureTask query = new FutureTask(new Callable(){
                        @Override
                        public Object call() throws Exception{
                            return getUserRequestInput(req);
                        }
                    });
                    Platform.runLater(query);
                    check = 0;
                    try {
                        check = (int)query.get();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                //Request accepted, return request to attemptConnection
                if (check == 1){
                        return req;
                }// connection accepted, attempt done
            }//within timeframe - accept/reject
        }
        return null;
    }

    synchronized private int openChatWindow(ChatRequest req){
        friendLock.lock();
        //Use current stage to hide friends window
        //Stage theStage = (Stage)friendsList.getScene().getWindow();
        //Use new stage to popup new window
        Stage theStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatInterface.fxml"));
        try {
            Parent root = loader.<Parent>load();
            ChatInterface controller = loader.<ChatInterface>getController();
            FriendData friend = findFriendFromRequest(req);
            //System.out.println("In openChatWindow before startConnection");
            System.out.flush();
            controller.startConnection(user, friend, req, nextPort);
            //System.out.println("In openChatWindow after startConnection");
            System.out.flush();
            Scene chatScene = new Scene(root);
            theStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
                public void handle(WindowEvent we) {
                    controller.endConnection();
                }
            });
            //System.out.println("Before friend name");
            String title = "Chat";
            if (friend.friend_name != null) {
                title = "Chatting with " + friend.friend_name;
            }
            //System.out.println("After friend name");
            System.out.flush();
            theStage.setTitle(title);;
            theStage.setScene(chatScene);
            //System.out.println("Showing chat window");
            theStage.show();
            nextPort = manager.getNextSocket();
            acceptedRequest = null;
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Exception in requestChat");
            setHandlingRequest(false);
            friendLock.unlock();
            return 1;
        }
        friendLock.unlock();
        setHandlingRequest(false);
        return 0;
    }

    //This should only be called as a thread
    void listenForRequests(){
        OkClient threadClient = new OkClient();
        int loopCount = 0;
            UserData threadUser = getUser();
            ArrayList<ChatRequest> newRequests = new ArrayList<>();
            if(threadUser == null) return;
            String currentServerTime = null;
            //while not connected - this should change for multiple connections functionality
            while(true){
                if(!getHandlingRequest()) {
                    setHandlingRequest(true);
                    newRequests.clear();
                    try {
                        currentServerTime = threadClient.getChatRequests(threadUser, newRequests);
                        acceptedRequest = handleRequests(newRequests, currentServerTime);
                        if (acceptedRequest != null) {
                            final FutureTask update = new FutureTask(new Callable() {
                                @Override
                                public Object call() throws Exception {
                                    return openChatWindow(acceptedRequest);
                                }
                            });
                            Platform.runLater(update);
                        }
                        else{
                            setHandlingRequest(false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        //TODO: figure out how to handle this gracefully
                        setHandlingRequest(false);
                        break;
                    }
                    if(loopCount > 5 && currentServerTime != null) removeStaleRequests(currentServerTime);
                    //setHandlingRequest(false) is handled by openChatWindow if a request is accepted
                }//if handling request
                loopCount++;
                if(loopCount > 5){
                    loopCount = 0;
                    final FutureTask update = new FutureTask(new Callable(){
                        @Override
                        public Object call() throws Exception{
                            return updateFriendsList();
                        }
                    });
                    Platform.runLater(update);
                    try {
                        update.get();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                } // if loop > 5
                //always sleep
                try {
                    Thread.sleep(REQ_LISTEN_REFRESH);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                    break;
                }
        } //while running
        //System.out.println("Exitted checking for chat requests");
        //Tell the main JavaFX thread to call the attemptConnection function
    }
}
