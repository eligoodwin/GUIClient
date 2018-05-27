package Controller;

import ClientAccountNetworking.OkClient;
import Cryptography.LocalAsymmetricCrypto;
import PeerNetworking.ConnectionManager;
import PeerNetworking.PeerConnection;
import QueryObjects.ChatRequest;
import QueryObjects.FriendData;
import QueryObjects.UserData;
import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

//Source for ListView: https://www.youtube.com/watch?v=9uubyM6oHAY
public class FriendsController{
    private static final String FRIEND_ACCEPTED = "2";
    private static final long REQ_LISTEN_REFRESH = 2000;
    private static final long MAX_REQUEST_DIFF = 10000;
    private LocalAsymmetricCrypto crypto = null;
    //0 for open, 1 for attempting connection, 2 for connected
    private int connectionStatus = 0;
    private boolean acceptConnection = false;
    private OkClient client = null;
    private UserData user = null;
    private ArrayList<ChatRequest> chatRequests = new ArrayList<>();
    //TODO: fList and friends can be merged?
    private ArrayList<FriendData> friends = new ArrayList<>();
    private Thread requestHandler = null;
    private PeerConnection nextPeer = null;

    @FXML
    ListView<FriendData> friendsList = null;
    private ObservableList<FriendData> fList = FXCollections.observableArrayList();


    public void setNextPeer(PeerConnection peer){
        nextPeer = peer;
    }

    private synchronized UserData getUser() {
        return user;
    }

    private synchronized int getConnectionStatus() {
        return connectionStatus;
    }

    private synchronized void setConnectionStatus(int connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

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
                    int index = findFriend(friend);
                    if (index >= 0) fList.remove(index);
                    //friendsList.refresh();
                    this.setContextMenu(null);
                }
                //pending
                else if (friendStatus == 1) {
                    System.out.println("Found friend with status 1");
                    this.setContextMenu(pendingMenu);
                    this.textProperty().bind(Bindings.format("Request from %s", friend.friend_name));
                    this.setStyle("-fx-font-style: italic");
                }
                //accepted friend
                else if (friendStatus == 2) {
                    System.out.println("Found friend with status 2");
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
                    int index = findFriend(friend);
                    if (index >= 0) fList.remove(index);
                    //friendsList.refresh();
                    System.out.println("Request status: " + friend.requestStatus);
                }
            }
        }//end update
    }

    int findFriend(FriendData friend){
        if (fList.size() < 1) return -1;
        for (int i = 0; i < fList.size(); i++){
            FriendData temp = fList.get(i);
            if (temp.friendID == friend.friendID && temp.friend_name.equals(friend.friend_name)) return i;
        }
        //not found
        return -1;
    }

    @FXML
    public void initialize(){
        client = new OkClient();
        try {
            crypto = new LocalAsymmetricCrypto();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not init encryption");
            System.exit(1);
        }
    }

    //TODO: does this need to be synchronized?
    void updateFriendsList(){
        if (getUser() == null) return;
        if (fList.size() != 0) fList.clear();
        if (friendsList != null) friendsList.getItems().clear();
        if (friends != null) friends.clear();
        try {
            String res = client.getFriends(getUser(), friends);
            //TODO: handle res
        }
        catch(IOException e){
            e.printStackTrace();
            //TODO: handle this
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
    }

    void initData(UserData usr){
        System.out.println("Trace: in initData");
        user = usr;
        updateFriendsList();
        requestHandler = new Thread(this::listenForRequests);
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
        System.out.println("Accepted: " + friend.friend_name);
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
        System.out.println("Rejected: " + friend.friend_name);
    }

    private void blockFriend(FriendData friend){
        try {
            int check = client.blockFriend(getUser(), friend.friend_name);
            if (check == 0) {
                System.out.println("Blocked: " + friend.friend_name);
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

    public void msgFriend(ActionEvent actionEvent){
        //get selected friend from listview
        FriendData friend = friendsList.getSelectionModel().getSelectedItem();
        //check if friend is accepted
        if (!friend.requestStatus.equals(FRIEND_ACCEPTED)) return;
        //TODO: popup window letting person know you can't connect to people that aren't friends
        //feed friend ip and port to PeerConnection
        int ok = 1;
        PeerConnection peer = null;
        try {
            peer = new PeerConnection(getUser(), friend);
            //attempt connection
            ok = peer.connectNatless();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        //if connection succesful open chatInterface
        if (ok == 0){
            Node source = (Node) actionEvent.getSource();
            Stage theStage = (Stage)source.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatInterface.fxml"));
            try {
                Parent root = loader.<Parent>load();
                ChatInterface controller = loader.<ChatInterface>getController();
                controller.initController(peer, user, friend);
                Scene chatScene = new Scene(root);
                theStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
                    public void handle(WindowEvent we) {
                        controller.endConnection();
                    }
                });
                theStage.setScene(chatScene);
            }
            catch(IOException e){
                e.printStackTrace();
                System.out.println("Exception in msgFriend");
            }
        }
        //else popup
        else System.out.println("Could not open peer connection. Error: " + ok);
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
        System.out.println("Trace: setUser called");
        user = usr;
    }

    //Should only ever be called from checkRequests
    private synchronized void removeStaleRequests(){
        //TODO:
    }

    //Should only be called by the checkRequests function
    // Returns difference in seconds between now and dateFormat if difference is within limit
    // Returns -1 if there is an error or difference is outside of allowable limit
    // Source: https://stackoverflow.com/a/20165708/2487475
    private synchronized int getDateDifference(String dateFormat){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMDD_HHmmss");
        try {
            Date input = format.parse(dateFormat);
            Date current = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles")).getTime();
            long diff = current.getTime() - input.getTime();
            //System.out.println("Difference: " + diff);
            if (diff < MAX_REQUEST_DIFF){
                //System.out.println("Request found less than diff: " + diff);
                return (int)diff;
            }
            return -1;
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
    private int connectToRequest(ChatRequest req){
        //TODO: This may be its own thread eventually
        //TODO: set connection status?
        System.out.println("Trace connect to request");
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
            return 1;}
        else{ System.out.println("Connection refused"); return 0;}
    }

    private int attemptConnection(ChatRequest req){
        Parent root;
        try {
            nextPeer = new PeerConnection(user, req);
            int status = nextPeer.connectNatPunch();
            //TODO: handle different status
            if (status != 0) nextPeer = null;
        }
        catch(IOException e){
            e.printStackTrace();
            nextPeer = null;
        }
        /*
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/attemptConnection.fxml"));
            root = loader.<Parent>load();
            AttemptConnectionController controller = loader.<AttemptConnectionController>getController();
            controller.initData(this);
            Stage stage = new Stage();
            stage.setTitle("Attempting Connection");
            stage.setScene(new Scene(root, 400, 150));
            setAcceptConnection(false);
            // Does not work - old connection popup
            stage.setOnShown(new EventHandler<WindowEvent>(){
                @Override
                public void handle(WindowEvent event){
                    if (event.getEventType() == WindowEvent.WINDOW_SHOWN) {
                        controller.connect();
                    }
                }
            });
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>(){
                @Override
                public void handle(WindowEvent event){
                    controller.connect();
                }
            });

            //Source: https://stackoverflow.com/a/34071134/2487475
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            System.out.println("Trace: end of attemptConnection");
        }
        catch(IOException e){
            e.printStackTrace();
            //TODO: handle this better
            return 0;
        }
        */
        //Connection success
        if (nextPeer != null){ return 0;}
        return 1;
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

    private synchronized int handleRequests(ArrayList<ChatRequest> newRequests){
        for(ChatRequest req : newRequests) {
            int check = getDateDifference(req.date);
            boolean contains = requestsContains(req);
            if (check > 0 && !contains) {
                chatRequests.add(req);
                //Source: https://stackoverflow.com/a/13804542/2487475
                final FutureTask query = new FutureTask(new Callable(){
                    @Override
                    public Object call() throws Exception{
                        return connectToRequest(req);
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
                //Connection accepted, now attempt connection
                if (check == 1){
                    check = attemptConnection(req);
                    //connection accepted and connected
                    if (check == 0){
                        System.out.println("Connection accepted and connected");
                        //TODO: open chat window with PeerConnection
                        return 1;
                    }
                }// connection accepted, attempt done
            }//within timeframe - accept/reject
        }
        return 0;
    }

    private int openChatWindow(){
        //TODO: don't need this if multiple connections?
        try {
            if (requestHandler != null) requestHandler.join(500);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        Stage theStage = (Stage)friendsList.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatInterface.fxml"));
        try {
            Parent root = loader.<Parent>load();
            ChatInterface controller = loader.<ChatInterface>getController();
            controller.initController(nextPeer, user, null);
            Scene chatScene = new Scene(root);
            theStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
                public void handle(WindowEvent we) {
                    controller.endConnection();
                }
            });
            theStage.setScene(chatScene);
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Exception in msgFriend");
            return 1;
        }

        return 0;
    }

    //This should only be called as a thread
    void listenForRequests(){
        OkClient threadClient = new OkClient();
        int loopCount = 0;
        UserData threadUser = getUser();
        ArrayList<ChatRequest> newRequests = new ArrayList<>();
        if(threadUser == null) return;
        //while not connected - this should change for multiple connections functionality
        while(getConnectionStatus() != 2){
            //if not attempting a connection
            if (getConnectionStatus() < 1){
                //
                if(loopCount > 5){ removeStaleRequests(); loopCount = 0; }
                newRequests.clear();
                try {
                    threadClient.getChatRequests(threadUser, newRequests);
                    int connectionAccepted = handleRequests(newRequests);
                    if (connectionAccepted == 1) setConnectionStatus(2);
                }
                catch(IOException e){
                    e.printStackTrace();
                    //TODO: figure out how to handle this gracefully
                    break;
                }
                loopCount++;
            } //if connection status < 1
            //always sleep
            try {
                Thread.sleep(REQ_LISTEN_REFRESH);
            }
            catch(InterruptedException e){
                e.printStackTrace();
                break;
            }
        } //while not connected
        final FutureTask query = new FutureTask(new Callable(){
            @Override
            public Object call() throws Exception{
                return openChatWindow();
            }
        });
        Platform.runLater(query);
        //TODO: open chat window
        System.out.println("Exitted checking for chat requests");
    }

}
