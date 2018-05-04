package Controller;

import ClientAccountNetworking.OkClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LogonController {
    private OkClient client = new OkClient("http://localhost:8080");
    @FXML
    private TextField logonUsername;
    @FXML
    private PasswordField logonPassword;
    public void gotoNextPage(ActionEvent actionEvent) {

        System.out.println("Logon controller clicked");
    }
}
