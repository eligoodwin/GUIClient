package Controller;

import ClientAccountNetworking.OkClient;
import PeerNetworking.ConnectionManager;
import QueryObjects.UserData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountCreationController {
    private OkClient client = new OkClient();
    @FXML
    private PasswordField createPassword;
    @FXML
    private TextField createUsername;
    @FXML
    private PasswordField reTypePassword;
    @FXML
    private Text createErrorText;

    public void createAccount(ActionEvent actionEvent) {
        UserData user = new UserData();
        user.username = createUsername.getText();
        String newPass = createPassword.getText();
        String repassword = reTypePassword.getText();
        if (newPass.length() < 5){
            createErrorText.setText("Password must be 5 or more characters");
            return;
        }
        else if (!newPass.equals(repassword)){
            createErrorText.setText("Passwords do not match");
            return;
        }
        user.password = newPass;
        user.email = "default@notimplemented.com";
        int check = -1;
        try {
            check = client.addUser(user);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not create user... exception encountered");
        }
        //user created succesfully
        if (check == 1){
            createErrorText.setText("Username is taken");
            return;
        }
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
                theStage.setTitle("Friends");
                theStage.setScene(friendsScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//user created
        //TODO: handle user not created
    }

    public void loadSplash(ActionEvent actionEvent) throws IOException{
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/splash.fxml"));
        Parent root = loader.<Parent>load();
        SplashController controller = loader.<SplashController>getController();
        Scene splashScene = new Scene(root);
        theStage.setScene(splashScene);
    }
}
