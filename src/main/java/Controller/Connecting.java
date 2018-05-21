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
    private boolean connecting = true;

    @FXML
    public void initialize(){
        System.out.println("Trace: in initialize connecting");
    }

    public synchronized void cancelConnection(){
        connecting = false;
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
