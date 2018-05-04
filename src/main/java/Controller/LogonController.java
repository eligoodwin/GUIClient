package Controller;

import ClientAccountNetworking.OkClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LogonController {
    private OkClient client = new OkClient("http://localhost:8080");
    @FXML
    private TextField logonUsername;
    @FXML
    private PasswordField logonPassword;
    public void gotoNextPage(ActionEvent actionEvent) {

        System.out.println("Logon controller clicked");
    }

        /*
    public class LogonController {
        public void gotoNextPage(ActionEvent actionEvent) throws IOException {
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage)source.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/chatInterface.fxml"));
        Scene chatInterface = new Scene(root);
        theStage.setScene(chatInterface);

        System.out.println("Chat Interface Button Clicked");
        */
}
