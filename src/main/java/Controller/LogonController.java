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
import javafx.stage.Stage;

import java.io.IOException;

public class LogonController {
    private UserData user = null;
    private OkClient client = new OkClient();
    @FXML
    private TextField logonUsername;
    @FXML
    private PasswordField logonPassword;

    public void gotoNextPage(ActionEvent actionEvent) throws Exception {
        System.out.println("Logon controller clicked");
        //logon
        if(attemptLogon()){
            //get ip info into stun server
            ConnectionManager connectionManager = new ConnectionManager(user);
            int port = connectionManager.connectToStun();

            Node source = (Node) actionEvent.getSource();
            Stage theStage = (Stage) source.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/friends.fxml"));
            Parent root = loader.<Parent>load();
            FriendsController controller = loader.<FriendsController>getController();
            controller.initData(user, port);
            Scene friendsScene = new Scene(root);
            theStage.setScene(friendsScene);
        }
        else{
            //display red text warning bad credentials
            System.out.println("bad creds");
        }
    }

    //used to attemptLogon the user
    private boolean attemptLogon(){
        final int VALID_HTTP = 0;
        user = new UserData();
        user.username = logonUsername.getText();
        user.password = logonPassword.getText();
        try {
            int httpStatus = client.logon(user);
            if(VALID_HTTP == httpStatus ){
                return true;
            }
        } catch (IOException e) {
            System.out.println("Could not connect to attemptLogon route");
        }
        return false;
    }
}
