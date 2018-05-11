package Controller;

import PeerNetworking.PeerConnection;
import QueryObjects.FriendData;
import QueryObjects.UserData;
import TestBot.JadenSmithBot;
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
    private JadenSmithBot jadenSmithBot;
    private Thread jadenBotThread;
    private PeerConnection peer = null;
    @FXML
    public void initialize(){
    }

    public void initController(PeerConnection peer){
        this.peer = peer;
        peer.setParentWindow(this);
        peer.startReceiving();
    }

    public void exitProgram(ActionEvent actionEvent) {
        if (peer != null) peer.stopConnection();
        System.out.println("Quit");
        System.exit(0);
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = messageToSend.getText();
        sendMessageToWindow(userIsSource(message));
        peer.sendMessage(message);
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

    private void putRandomGarbageOnScreen(){
        sendMessageToWindow(userIsNotSource(jadenSmithBot.getRandomGarbage()));
    }


    private String userIsSource(String message){
        return String.format("You said >> \t%s\n", message);
    }

    public String userIsNotSource(String message){
        return String.format("They said << \t%s\n", message);
    }
}
