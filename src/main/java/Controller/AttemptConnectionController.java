package Controller;

import QueryObjects.ChatRequest;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AttemptConnectionController {
    @FXML
    Text attemptConnectionText;
    FriendsController parentController = null;

    public void initData(FriendsController parent){
        this.parentController = parent;
    }

    public void connect(){
        //TODO: attempt connection
        System.out.println("In connect in AttemptConnectionController");
        Stage stage = (Stage) attemptConnectionText.getScene().getWindow();
        try {
            Thread.sleep(1000);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
        stage.close();
    }
}
