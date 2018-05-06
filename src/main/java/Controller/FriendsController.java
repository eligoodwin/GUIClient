package Controller;

import ClientAccountNetworking.OkClient;
import QueryObjects.FriendData;
import QueryObjects.UserData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

//Source for ListView: https://www.youtube.com/watch?v=9uubyM6oHAY
public class FriendsController{
    private OkClient client = null;
    private UserData user = null;
    //TODO: fList and friends can be merged?
    private ArrayList<FriendData> friends = new ArrayList<FriendData>();

    @FXML
    ListView<FriendData> friendsList;
    ObservableList<FriendData> fList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        System.out.println("Trace: in initialize");
        if (user != null) System.out.println("User is not null");
        if (friendsList != null) System.out.println("friendsList is not null");
    }

    public void initData(UserData usr){
        System.out.println("Trace: in initData");
        client = new OkClient("http://localhost:8080");
        user = usr;
        try {
            String res = client.getFriends(user, friends);
            //TODO: handle res
        }
        catch(IOException e){
            e.printStackTrace();
            //TODO: handle this
        }
        if (friends.size() > 0){
            for (FriendData friend : friends){
                fList.add(friend);
            }
            friendsList.setItems(fList);
        }
    }

    public  void setUser(UserData usr){
        System.out.println("Trace: setUser called");
        user = usr;
    }

}
