package Controller;

import ClientAccountNetworking.OkClient;
import PeerNetworking.PeerConnection;
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
    UserData user = null;
    private OkClient client = new OkClient();
    @FXML
    private TextField logonUsername;
    @FXML
    private PasswordField logonPassword;

    public void gotoNextPage(ActionEvent actionEvent) throws Exception {
        user = new UserData();
        user.username = logonUsername.getText();
        user.password = logonPassword.getText();
        try {
            int res = client.logon(user);
            System.out.printf("User token: %s%n", user.token);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: re-try route (pop up message?)
        }
        //for debugging only
        if (user.username.equals("testLocalChat") || user.username.equals("testlocalchat")) {
            testLocalServer(actionEvent);
        }
        //normal route
        else {
            System.out.println("Logon controller clicked");
            Node source = (Node) actionEvent.getSource();
            Stage theStage = (Stage) source.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/friends.fxml"));
            Parent root = loader.<Parent>load();
            FriendsController controller = loader.<FriendsController>getController();
            controller.initData(user);
            Scene friendsScene = new Scene(root, 300, 550);
            theStage.setScene(friendsScene);
        }
    }

    private void testLocalServer(ActionEvent actionEvent) {
        //start local server - default port == 9000
        PeerConnection peer = new PeerConnection(9000);
        //display popup that waiting for peer connection

        //update IP after connection
        UserData temp = peer.getUser();
        user.ipAddress = temp.ipAddress;
        user.peerServerPort = temp.peerServerPort;
        System.out.println("Trace, IP update: " + user.ipAddress + " : " + user.peerServerPort);
        int status = -1;
        try {
            status = client.updateIP(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (status != 0) {
            System.out.println("Status is " + status);
        }
        //upon connection create chatInterface
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatInterface.fxml"));
        try {
            Parent root = loader.<Parent>load();
            ChatInterface controller = loader.<ChatInterface>getController();
            controller.setPeerTester(peer);
            Scene chatScene = new Scene(root, 300, 550);
            theStage.setScene(chatScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exception in peer debug");
        }
    }
}
