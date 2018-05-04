package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountCreationController {

    public void createAccount(ActionEvent actionEvent) throws IOException {
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage)source.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/splash.fxml"));
        Scene logonScene = new Scene(root, 400, 200);
        theStage.setScene(logonScene);
        System.out.println("logon was clicked");
    }
}
