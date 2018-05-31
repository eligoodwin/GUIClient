package Controller;

import PeerNetworking.PeerConnection;
import QueryObjects.ChatRequest;
import QueryObjects.FriendData;
import QueryObjects.UserData;
import TestBot.JadenSmithBot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.util.Random;

public class ChatInterface {

    public TextArea messageWindow;
    public Button sendButton;
    public TextField messageToSend;
    private UserData user;
    private FriendData friend;
    private ChatRequest request;
    private int port;
    private PeerConnection peer = null;
    private Thread startupThread = null;

    @FXML
    public void initialize(){
    }

    void startConnection(UserData user, FriendData friend, ChatRequest request, int port){
        this.user = user;
        this.friend = friend;
        this.request  = request;
        this.port = port;
        try {
            peer = new PeerConnection(user, request);

        }
        catch(IOException e){
            e.printStackTrace();
        }
        StartupThread startup = new StartupThread(this);
        startupThread = new Thread(startup);
        startupThread.setDaemon(true);
        startupThread.start();
        //TODO: this needs to be after window is fully loaded to work
    }

    private void beginReceiving(){
        peer.setParentWindow(this);
        peer.setFriend(friend);
        peer.startReceiving();
    }

    class StartupThread implements Runnable{
        private ChatInterface parentWindow;
        @Override
        public void run(){
            //attempt connection
            int status = parentWindow.attemptConnection(request);
            System.out.println("attemptConnection status: " + status);
            //while not loaded, wait
            //handle connection results
            //TODO: handle closing window during thread
            //if connected:
            if (status == 0) {
                //TODO: adjust window
                parentWindow.beginReceiving();
            }
        }

        public StartupThread(ChatInterface window){
            parentWindow = window;
        }
    }

    private int attemptConnection(ChatRequest req){
        try {
            peer = new PeerConnection(user, req);
            int status = peer.connectNatPunch(port);
            //TODO: handle different status
            if (status != 0) peer = null;
        }
        catch(IOException e){
            e.printStackTrace();
            peer = null;
        }
        //Connection success
        if (peer != null){
            return 0;
        }
        return 1;
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

    private String userIsSource(String message){
        return String.format("%s >> %s\n", user.username, message);
    }

    public String userIsNotSource(String message){
        return String.format("%s << %s\n", friend.friend_name, message);
    }
}
