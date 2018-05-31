import QueryObjects.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println(getClass().getResource("/fxml/createAccount.fxml"));
        System.out.println(getClass().getResource("/fxml/splash.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/splash.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        //closes down the program completely when the program exits -- still need to fix for chat client
        primaryStage.setOnCloseRequest(e -> {
                    Platform.exit();
                    System.exit(0);
                }
        );
    }

    public static void main(String[] args){
        launch(args);
    }
}
