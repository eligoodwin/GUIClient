package Controller;

import ClientAccountNetworking.OkClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class SplashController {
    private Stage stage;
    private Parent root;

    public void gotoAccountCreation(ActionEvent actionEvent) throws Exception {
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage)source.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/createAccount.fxml"));
        Scene logonScene = new Scene(root);
        theStage.setScene(logonScene);
        theStage.setTitle("BLACK(c)HAT");
        System.out.println("account creation was clicked");

    }

    public void logon(ActionEvent actionEvent) throws IOException {
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage)source.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/logon.fxml"));
        Scene logonScene = new Scene(root);
        theStage.setScene(logonScene);
        theStage.setTitle("BLACK(c)HAT");
        System.out.println("logon was clicked");
    }
}
