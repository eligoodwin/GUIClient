package Controller;

import ClientAccountNetworking.OkClient;
import QueryObjects.FriendData;
import QueryObjects.UserData;
import javafx.fxml.FXML;

import java.io.IOException;

public class Connecting {
    private UserData user;
    private FriendData friend;
    private OkClient client;

    @FXML
    public void initialize(){
        System.out.println("Trace: in initialize connecting");
    }

    public void cancelConnection(){

    }
    public void initData(UserData usr, FriendData frnd){
        System.out.println("Trace: in initData");
        client = new OkClient();
        user = usr;
        friend = frnd;
        //TODO: get friend IP and data - maybe in friends controller?
        //TODO: log request for connection
    }
}
