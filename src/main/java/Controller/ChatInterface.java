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

    void initController(PeerConnection peer){
        this.peer = peer;
        peer.setParentWindow(this);
        peer.startReceiving();
    }

    void setPeerTester(PeerConnection peer){
        this.peer = peer;
        peer.setParentWindow(this);
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = messageToSend.getText();
        sendMessageToWindow(userIsSource(message));
        peer.sendMessage(message);
    }


    void endConnection(){
        if (peer != null){
            System.out.println("Terminating connection in endConnection");
            peer.stopConnection();
        }
    }



    private int getMessageWindowHeight(){
        int windowHeight = messageWindow.getText().split("\n").length;
        System.out.printf("Window height is: %s\n", windowHeight);
        return windowHeight;
    }


    private void placeRowCountInMessageWindow(int height){
        messageWindow.appendText(String.format("This chat window has %s rows", String.valueOf(height)));
    }

    public void sendMessageToWindow(String message){
        messageWindow.appendText(message);
    }

    private String userIsSource(String message){
        return String.format("You said >> \t%s\n", message);
    }

    public String userIsNotSource(String message){
        return String.format("They said << \t%s\n", message);
    }
}
