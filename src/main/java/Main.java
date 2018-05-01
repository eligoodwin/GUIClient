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

public class Main extends Application {
    private Stage window;
    private Scene scene1, scene2;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        window = primaryStage;
        //make display for scene 1
        //make grid for objects
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25 ,25,25));
        scene1 = new Scene(gridPane, 400, 200);

        Text sceneTitle = new Text("Welcome to logon");
        gridPane.add(sceneTitle, 0, 0, 2, 1);

        Label username = new Label("User name:");
        gridPane.add(username, 0,1);
        TextField usernameTextField = new TextField();
        gridPane.add(usernameTextField, 1, 1);

        Label password = new Label("Password:");
        gridPane.add(password, 0,2);
        PasswordField pwBox = new PasswordField();
        gridPane.add(pwBox, 1, 2);

        Button submitButton = new Button("Sign in");
        HBox hbButton = new HBox(10);
        submitButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbButton.getChildren().addAll(submitButton);
        gridPane.add(hbButton, 1, 4);

        final Text actionTarget = new Text();
        gridPane.add(actionTarget, 1, 6);
        submitButton.setOnAction( e -> window.setScene(scene2));

        //make scene 2
        Button goBack = new Button("Go back");
        goBack.setOnAction(e -> window.setScene(scene1));
        Label labelScene2 = new Label("This is scene 2");
        VBox vbox = new VBox(4);
        vbox.getChildren().addAll(labelScene2, goBack);
        scene2 = new Scene(vbox, 400, 200);




        //set the scene and show on stage
        window.setScene(scene1);
        window.show();
    }







    public static void main(String[] args){
        launch(args);
    }
}
