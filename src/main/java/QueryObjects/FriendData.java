package QueryObjects;

import com.google.gson.annotations.SerializedName;

public class FriendData {
    @SerializedName("friend name")
    public String friend_name = "";
    public String ipAddress = "127.0.0.1";
    public String peerServerPort = "9000";
    public String requestDate = "";
    public String requestStatus = "";
    public long friendID = 0;
    public FriendData(){};

    @Override
    public String toString(){
        return friend_name;
    }
}
