package Controller;

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
    private JadenSmithBot jadenSmithBot;
    private Thread jadenBotThread;

    @FXML
    public void initialize(){
        //start jaden bot thread
        this.jadenSmithBot = new JadenSmithBot();
        this.jadenBotThread = new Thread( () -> {
            Random random = new Random();
            while(true){
                int randomWaitInterval = random.nextInt(10) + 1;
                try {
                    Thread.sleep(randomWaitInterval * 1000);
                    putRandomGarbageOnScreen();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        jadenBotThread.start();

        messageToSend.setOnKeyPressed( event -> {
            if(!messageToSend.getText().isEmpty() && event.getCode() ==KeyCode.ENTER){
               sendMessageToWindow(userIsSource(messageToSend.getText()));
               messageToSend.clear();
            }
        });
    }

    public void exitProgram(ActionEvent actionEvent) {
        System.out.println("Quit");
        System.exit(0);
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = messageToSend.getText();
        sendMessageToWindow(userIsSource(message));
    }


    private int getMessageWindowHeight(){
        int windowHeight = messageWindow.getText().split("\n").length;
        System.out.printf("Window height is: %s\n", windowHeight);
        return windowHeight;
    }


    private void placeRowCountInMessageWindow(int height){
        messageWindow.appendText(String.format("This chat window has %s rows", String.valueOf(height)));
    }

    private void sendMessageToWindow(String message){
        messageWindow.appendText(message);
    }

    private void putRandomGarbageOnScreen(){
        sendMessageToWindow(userIsNotSource(jadenSmithBot.getRandomGarbage()));
    }


    private String userIsSource(String message){
        return String.format("You said >> \t%s\n", message);
    }

    private String userIsNotSource(String message){
        return String.format("They said << \t%s\n", message);
    }
}
