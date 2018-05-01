package Controller;

import com.sun.tools.hat.internal.model.Root;
import javafx.event.ActionEvent;
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
        Parent root = FXMLLoader.load(getClass().getResource("createAccount.fxml"));
        Scene logonScene = new Scene(root, 400, 200);
        theStage.setScene(logonScene);
        System.out.println("logon was clicked");

    }

    public void logon(ActionEvent actionEvent) throws IOException {
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage)source.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("logon.fxml"));
        Scene logonScene = new Scene(root, 400, 200);
        theStage.setScene(logonScene);
        System.out.println("logon was clicked");
    }
}
