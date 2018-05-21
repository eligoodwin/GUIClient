package Controller;

import QueryObjects.ChatRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AcceptConnectionController {
    @FXML
    private Text incomingConnectionText;
    @FXML
    private Button ignoreConnection;
    @FXML
    private Button acceptConnection;

    FriendsController parentController = null;

    @FXML
    public void acceptConnection(){
        parentController.setAcceptConnection(true);
        Stage stage = (Stage) acceptConnection.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void ignoreConnection(){
        parentController.setAcceptConnection(false);
        Stage stage = (Stage) ignoreConnection.getScene().getWindow();
        stage.close();
    }

    public void initData(FriendsController parent, ChatRequest request){
        this.parentController = parent;
        incomingConnectionText.setText("Accept connection from " + request.requestingUser);
    }
}
