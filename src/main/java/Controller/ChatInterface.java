package Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;

public class ChatInterface {

    public TextArea messageWindow;

    public void exitProgram(ActionEvent actionEvent) {
        System.out.println("Quit");
        System.exit(0);
    }

    public void sendMessage(ActionEvent actionEvent) {
        placeMessageInChatWindow(getMessageWindowHeight());
    }

    private int getMessageWindowHeight(){
        int windowHeight = messageWindow.getText().split("\n").length;
        System.out.printf("Window height is: %s\n", windowHeight);
        return windowHeight;
    }


    private void placeMessageInChatWindow(int height){
        messageWindow.setText(String.format("This chat window has %s rows", String.valueOf(height)));
    }
}
