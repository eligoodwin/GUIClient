package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LogonController {
    public void gotoNextPage(ActionEvent actionEvent) throws IOException {
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage)source.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/chatInterface.fxml"));
        Scene chatInterface = new Scene(root);
        theStage.setScene(chatInterface);

        System.out.println("Chat Interface Button Clicked");
    }
}
