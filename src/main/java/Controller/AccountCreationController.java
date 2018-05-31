package Controller;

import ClientAccountNetworking.OkClient;
import PeerNetworking.ConnectionManager;
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
        int check = -1;
        try {
            check = client.addUser(user);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not create user... exception encountered");
        }
        //user created succesfully
        if (check == 0) {
            System.out.printf("User created with id: %s%n", user.id);
            Node source = (Node) actionEvent.getSource();
            Stage theStage = (Stage) source.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/friends.fxml"));
            try {
                Parent root = loader.<Parent>load();
                FriendsController controller = loader.<FriendsController>getController();
                controller.initData(user);
                Scene friendsScene = new Scene(root);
                theStage.setScene(friendsScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//user created
        //TODO: handle user not created
    }
}
