import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println(getClass().getResource("/fxml/createAccount.fxml"));
        System.out.println(getClass().getResource("/fxml/splash.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/splash.fxml"));
        primaryStage.setScene(new Scene(root, 400, 200));
        primaryStage.show();

    }

    public static void main(String[] args){
        launch(args);
    }
}
