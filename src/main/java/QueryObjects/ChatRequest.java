package QueryObjects;

import com.google.gson.annotations.SerializedName;

public class ChatRequest {
    public String username;
    public String targetUser;
    public String targetIP;
    public String targetPort;
    public String requestingUser;
    public String requestingIPaddress;
    public String requestingPort;
    @SerializedName("API token")
    public String API_token;
    public String usertoken;
    public String date;

    public ChatRequest(UserData user, FriendData friend){
        username = user.username;
        targetUser = friend.friend_name;
        usertoken = user.token;
    }

    public ChatRequest(UserData user, String friendName){
        username = user.username;
        targetUser = friendName;
        usertoken = user.token;
    }
}
