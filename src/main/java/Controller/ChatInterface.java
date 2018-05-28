package Controller;

import Cryptography.AssymEncypt;
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

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Random;

public class ChatInterface {

    public TextArea messageWindow;
    public Button sendButton;
    public TextField messageToSend;
    private UserData user;
    private FriendData friend;
    private PeerConnection peer = null;
    private AssymEncypt assymEncypt;
    private PublicKey targetUserPublicKey;

    @FXML
    public void initialize(){
    }

    void initController(PeerConnection peer, UserData user, FriendData friend){
        try {
            AssymEncypt crypto = new AssymEncypt();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
            System.out.println("Error with encryption protocol");
            System.exit(1);
        }
        this.peer = peer;
        peer.setParentWindow(this);
        peer.startReceiving();
        this.user = user;
        this.friend = friend;
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

    private String userIsSource(String message){
        return String.format("%s >> %s\n", user.username, message);
    }

    public String userIsNotSource(String message){
        return String.format("%s << %s\n", friend.friend_name, message);
    }
}
