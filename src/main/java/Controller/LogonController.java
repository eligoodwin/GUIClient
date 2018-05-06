package Controller;

import ClientAccountNetworking.OkClient;
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
    private OkClient client = new OkClient("http://localhost:8080");
    @FXML
    private TextField logonUsername;
    @FXML
    private PasswordField logonPassword;
    public void gotoNextPage(ActionEvent actionEvent) throws Exception{
        UserData user = new UserData();
        user.username = logonUsername.getText();
        user.password = logonPassword.getText();
        try {
            String res = client.logon(user);
            System.out.println(res);
            System.out.printf("User token: %s%n", user.token);
        }
        catch(IOException e){
            e.printStackTrace();
            //TODO: re-try route (pop up message?)
        }
        System.out.println("Logon controller clicked");
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage)source.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/friends.fxml"));
        Parent root = loader.<Parent>load();
        if (root == null) System.out.println("Root is null");
        else {
            System.out.println("Parent is not null");
        }
        FriendsController controller = loader.<FriendsController>getController();
        if (controller == null) System.out.println("Controller is null");
        else{
            System.out.println("Controller is not null");
            controller.initData(user);
        }
        Scene friendsScene = new Scene(root, 200, 600);
        theStage.setScene(friendsScene);
    }

        /*
    public class LogonController {
        public void gotoNextPage(ActionEvent actionEvent) throws IOException {
        Node source = (Node) actionEvent.getSource();
        Stage theStage = (Stage)source.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/chatInterface.fxml"));
        Scene chatInterface = new Scene(root);
        theStage.setScene(chatInterface);

        System.out.println("Chat Interface Button Clicked");
        */
}
