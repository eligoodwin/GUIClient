package Controller;

import ClientAccountNetworking.OkClient;
import QueryObjects.UserData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountCreationController {
    private OkClient client = new OkClient();
    @FXML
    private TextField createPassword;
    @FXML
    private TextField createUsername;
    @FXML
    private TextField createEmail;

    public void createAccount(ActionEvent actionEvent) {
        UserData user = new UserData();
        user.username = createUsername.getText();
        user.password = createPassword.getText();
        user.email = createEmail.getText();
        try {
            client.addUser(user);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: needs a route to re-try (pop up message?)
            System.out.println("Could not create user... exception encountered");
        }
        System.out.printf("Text user: %s%n", createUsername.getText());
        System.out.printf("Text pass: %s%n", createPassword.getText());
        System.out.printf("Text email: %s%n", createEmail.getText());
        System.out.printf("Obj user: %s%n", user.username);
        System.out.printf("Obj pass: %s%n", user.password);
        System.out.printf("Obj email: %s%n", user.email);
        System.out.printf("Obj token: %s%n", user.token);
        System.out.printf("Obj id: %s%n", user.id);
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/friends.fxml"));
        try {
            Parent root = loader.<Parent>load();
            FriendsController controller = loader.<FriendsController>getController();
            controller.initData(user);
            Scene friendsScene = new Scene(root, 200, 600);
            theStage.setScene(friendsScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}