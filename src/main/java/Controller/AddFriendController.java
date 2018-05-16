package Controller;

import ClientAccountNetworking.OkClient;
import QueryObjects.UserData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class AddFriendController {
    private UserData user;
    private OkClient client = new OkClient();
    @FXML
    private TextField friendUsername;
    @FXML
    private Label addFriendLabel;
    @FXML
    private Text addFriendMsg;
    @FXML
    private Button addFriendSubmit;

    public void initData(UserData usr){
        user = usr;
    }

    public void submitRequest(){
        try {
            int check = client.requestFriend(friendUsername.getText(), user);
            if (check == 0) {
                //display message
                System.out.println("Response OK");
                addFriendMsg.setText("Friend request sent");
                addFriendMsg.setVisible(true);
                friendUsername.setVisible(false);
                addFriendLabel.setVisible(false);
                addFriendSubmit.setText("OK");
                addFriendSubmit.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //Source: https://stackoverflow.com/questions/13567019/close-fxml-window-by-code-javafx
                        Stage stage = (Stage) addFriendSubmit.getScene().getWindow();
                        stage.close();
                    }
                });
            } // check == 0
            else{
                addFriendMsg.setText("Error in requesting friend");
                addFriendMsg.setVisible(true);
            }
        }
        catch(IOException e){
            e.printStackTrace();
            //TODO: handle this better
        }
    }
}
