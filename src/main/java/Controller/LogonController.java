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

public class LogonController {
    private UserData user = null;
    private OkClient client = new OkClient();
    @FXML
    private TextField logonUsername;
    @FXML
    private PasswordField logonPassword;
    @FXML
    private Text errorText;

    public void gotoNextPage(ActionEvent actionEvent) throws Exception {
        System.out.println("Logon controller clicked");
        //logon
        int status = attemptLogon();
        if(status == 0){
            //get ip info into stun server

            Node source = (Node) actionEvent.getSource();
            Stage theStage = (Stage) source.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/friends.fxml"));
            Parent root = loader.<Parent>load();
            FriendsController controller = loader.<FriendsController>getController();
            controller.initData(user);
            Scene friendsScene = new Scene(root);
            theStage.setTitle("Friends");
            theStage.setScene(friendsScene);
        }
        else if (status == 1){
            errorText.setText("Bad credentials");
        }
        else{
            errorText.setText("Error with connection");
        }
    }

    //used to attemptLogon the user
    private int attemptLogon(){
        final int VALID_HTTP = 0;
        user = new UserData();
        user.username = logonUsername.getText();
        user.password = logonPassword.getText();
        try {
            int httpStatus = client.logon(user);
            return httpStatus;
        } catch (IOException e) {
            System.out.println("Could not connect to attemptLogon route");
        }
        return -1;
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
