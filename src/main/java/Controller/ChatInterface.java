package Controller;

import PeerNetworking.PeerConnection;
import QueryObjects.FriendData;
import QueryObjects.UserData;
import TestBot.JadenSmithBot;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.Random;

public class ChatInterface {

    public TextArea messageWindow;
    public Button sendButton;
    public TextField messageToSend;
    private UserData user;
    private FriendData friend;
    private PeerConnection peer = null;
    @FXML
    public void initialize(){
    }

    void initController(PeerConnection peer, UserData user, FriendData friend){
        this.peer = peer;
        peer.setParentWindow(this);
        peer.startReceiving();
        this.user = user;
        this.friend = friend;
    }

    void setPeerTester(PeerConnection peer){
        this.peer = peer;
        peer.setParentWindow(this);
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = messageToSend.getText();
        messageToSend.clear();
        sendMessageToWindow(userIsSource(message));
        peer.sendMessage(message);
    }


    void endConnection(){
        if (peer != null){
            System.out.println("Terminating connection in endConnection");
            peer.stopConnection();
        }
    }

    public void sendMessageToWindow(String message){
        messageWindow.appendText(message);
    }

    //both need to be fixed on init
    private String userIsSource(String message){
        return String.format("%s >> %s\n", user.username, message);
    }

    public String userIsNotSource(String message){
        return String.format("%s << %s\n", friend.friend_name, message);
    }
}
