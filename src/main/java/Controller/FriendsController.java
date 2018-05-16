package Controller;

import ClientAccountNetworking.OkClient;
import PeerNetworking.PeerConnection;
import QueryObjects.FriendData;
import QueryObjects.UserData;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

//Source for ListView: https://www.youtube.com/watch?v=9uubyM6oHAY
public class FriendsController{
    private OkClient client = null;
    private UserData user = null;
    //TODO: fList and friends can be merged?
    private ArrayList<FriendData> friends = new ArrayList<>();

    @FXML
    ListView<FriendData> friendsList = null;
    private ObservableList<FriendData> fList = FXCollections.observableArrayList();

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
                    friendsList.getItems().remove(friend);
                    friendsList.refresh();
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
                    friendsList.getItems().remove(friend);
                    friendsList.refresh();
                    System.out.println("Request status: " + friend.requestStatus);
                }
            }
        }
    }

    @FXML
    public void initialize(){
        client = new OkClient();
        System.out.println("Trace: in initialize");
    }

    void updateFriendsList(){
        if (user == null) return;
        if (fList.size() != 0) fList.clear();
        if (friendsList != null) friendsList.getItems().clear();
        if (friends != null) friends.clear();
        try {
            String res = client.getFriends(user, friends);
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
    }

    private void acceptFriend(FriendData friend){
        try {
            int check = client.acceptFriend(user, friend.friend_name);
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
            int check = client.rejectFriend(user, friend.friend_name);
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
            int check = client.blockFriend(user, friend.friend_name);
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

    /* Trying new method to get context menu
    void initData(UserData usr){
        System.out.println("Trace: in initData");
        client = new OkClient();
        user = usr;
        try {
            String res = client.getFriends(user, friends);
            //TODO: handle res
        }
        catch(IOException e){
            e.printStackTrace();
            //TODO: handle this
        }
        if (friends.size() > 0){
            fList.addAll(friends);
            friendsList.setItems(fList);
        }
    }
    */

    public void msgFriend(ActionEvent actionEvent){
        //get selected friend from listview
        FriendData friend = friendsList.getSelectionModel().getSelectedItem();
        //feed friend ip and port to PeerConnection
        PeerConnection peer = new PeerConnection(user, friend);
        //attempt connection
        int ok = peer.connectNatless();
        //if connection succesful open chatInterface
        if (ok == 0){
            Node source = (Node) actionEvent.getSource();
            Stage theStage = (Stage)source.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatInterface.fxml"));
            try {
                Parent root = loader.<Parent>load();
                ChatInterface controller = loader.<ChatInterface>getController();
                controller.initController(peer);
                Scene chatScene = new Scene(root, 300, 550);
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
            controller.initData(user);
            Stage stage = new Stage();
            stage.setTitle("Add Friend");
            stage.setScene(new Scene(root, 400, 150));
            stage.show();
        }
        catch(IOException e){
            e.printStackTrace();
            //TODO: handle this better
        }
    }

    public  void setUser(UserData usr){
        System.out.println("Trace: setUser called");
        user = usr;
    }

}
