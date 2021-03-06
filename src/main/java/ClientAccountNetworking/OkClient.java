
//Source: http://www.vogella.com/tutorials/JavaLibrary-OkHttp/article.html
package ClientAccountNetworking;

import java.io.IOException;
import java.util.ArrayList;

import QueryObjects.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import static java.lang.Integer.parseInt;

public class OkClient {
    private final static String GOOD_RES = "VALID REQUEST";
    private final static String API_TOKEN = "fXtas7yB2HcIVoCyyQ78";
    //Server: http://104.168.134.135:8080
    private final static String SERVER_ADDRESS = "http://104.168.134.135:8080";
    //private final static String SERVER_ADDRESS = "http://localhost:8080";
    private static Gson gson = new Gson();
    //Source: https://stackoverflow.com/questions/4802887/gson-how-to-exclude-specific-fields-from-serialization-without-annotations
    private static Gson exGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();
    private String url = SERVER_ADDRESS;
    public int connectToServer(int token){
        return 0;
    }

    //TODO: don't need UserLogon, can just use UserData
    //RES: {"message" : {"authToken" : "testToken"}, "status" : "VALID REQUEST"}
    public int logon(UserData user) throws IOException {
        if (url.equals("")) return -1;
        user.API_token = API_TOKEN;
        String json = gson.toJson(user);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url + "/logon")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        int status = response.code();
        if (status >= 200 && status < 400) {
            String res = response.body().string();
            System.out.println(res);
            ResponseObj resObj = gson.fromJson(res, ResponseObj.class);
            String ret = resObj.status;
            if (ret.equals(GOOD_RES)) {
                user.token = resObj.message.get("authToken").getAsString();
                user.id = Long.parseLong(resObj.message.get("id").toString());
                return 0;
            }
            String resMsg = resObj.message.get("response").getAsString();
            if (resMsg.equals("bad credentials")){
                return 1;
            }
        }
        return -1;
    }

    public int addUser(UserData user) throws IOException {
        if (url.equals("")) return -1;
        user.API_token = API_TOKEN;
        String json = exGson.toJson(user);
        System.out.println("JSON string:\n" + json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url + "/user")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        json = response.body().string();
        System.out.println(json);
        int resStatus = response.code();
        if (resStatus >= 200 && resStatus < 400) {
            ResponseObj resObj = gson.fromJson(json, ResponseObj.class);
            String ret = resObj.status;
            if(ret.equals(GOOD_RES)) {
                UserData tempUser = gson.fromJson(resObj.message.getAsJsonObject("userDetails"), UserData.class);
                user.token = resObj.message.get("token").getAsString();
                user.id = tempUser.id;
                return 0;
            }
            else {
                String resMsg = resObj.message.get("response").getAsString();
                if (resMsg.equals("username taken")) {
                    return 1;
                }
            }
        }
        System.out.println("Error in addUser");
        return -1;
    }

    public String checkToken(UserData user) throws IOException{
        if (url.equals("")) return "No URL set!";
        String json = gson.toJson(user);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url + "/testToken")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String testJWT(String jwt) throws IOException{
        if (url.equals("")) return "No URL set!";
        String json = "{\"jwt\":\"" + jwt + "\"}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url + "/testJWT")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public int requestFriend(String username, UserData current) throws IOException{
        if (url.equals("")) return -1;
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer" + current.token)
                .url(url + "/user/" + current.id + "/friend/" + username)
                .build();
        Response response = client.newCall(request).execute();
        String json = response.body().string();
        System.out.println(json);
        int status = response.code();
        if (status >= 200 && status < 400) {
            ResponseString resObj = gson.fromJson(json, ResponseString.class);
            System.out.println("Status: " + resObj.status);
            if (resObj.status.equals(GOOD_RES)) return 0;
        }
        return 1;
    }

    private int updateRelationship(long id, String userToken, String friendName, int status) throws IOException {
        if (url.equals("")) return -1;
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer" + userToken)
                .url(url + "/user/" + id + "/friend/" + friendName + "/" + status)
                .build();
        Response response = client.newCall(request).execute();
        int resStatus = response.code();
        if (resStatus >= 200 && resStatus < 400) {
            String res = response.body().string();
            ResponseString resObj = gson.fromJson(res, ResponseString.class);
            System.out.println(resObj.status);
            if (resObj.status.equals(GOOD_RES)) return 0;
        }
        return 1;
    }

    public int acceptFriend(UserData current, String friendName) throws IOException{
        int ok = updateRelationship(current.id, current.token, friendName, 2);
        return ok;
    }

    public int rejectFriend(UserData current, String friendName) throws IOException{
        int ok = updateRelationship(current.id, current.token, friendName, 3);
        return ok;
    }

    public int blockFriend(UserData current, String friendName) throws IOException{
        int ok = updateRelationship(current.id, current.token, friendName, 4);
        return ok;
    }

    //TODO: update return type when determined how to parse code
    public String getFriends(UserData current, ArrayList<FriendData> friends) throws IOException {
        if (url.equals("")) return "No URL";
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer" + current.token)
                .url(url + "/user/" + current.id + "/friend")
                .build();
        Response response = client.newCall(request).execute();
        int status = response.code();
        String res = response.body().string();
//        System.out.println(res); //TODO: trace
        if (status >= 200 && status < 400) {
            ResponseArray resObj = gson.fromJson(res, ResponseArray.class);
            res = resObj.status;
            FriendData[] tempFrnds;
            tempFrnds = gson.fromJson(resObj.message, FriendData[].class);
            if (tempFrnds != null && tempFrnds.length > 0){
                for (FriendData fr : tempFrnds) friends.add(fr);
            }
            return res;
        }
        else{
            ResponseArray resObj = gson.fromJson(res, ResponseArray.class);
            res = resObj.status;
            return res;
        }
    }

    public int updateIP(UserData current) throws IOException{
        if (url.equals("")) return -1;
        String json = exGson.toJson(current);
        System.out.println("Body string:\n" + json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url + "/user/" + current.id + "/ipAddress")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        int resStatus = response.code();
        if (resStatus >= 200 && resStatus < 400) {
            String res = response.body().string();
            System.out.println(res);
            ResponseObj resObj = gson.fromJson(res, ResponseObj.class);
            if (resObj.status.equals(GOOD_RES)) return 0;
        }
        return 1;
    }

    public ChatRequest makeChatRequest(UserData current, String friendName) throws IOException{
        if (url.equals("")) return null;
        ChatRequest chatRequest = new ChatRequest(current, friendName);
        chatRequest.API_token = API_TOKEN;
        String json = gson.toJson(chatRequest);
        //System.out.println("Chat request: " + json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer" + current.token)
                .url(url + "/user/" + current.id + "/chat")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        int resStatus = response.code();
        if (resStatus >= 200 && resStatus < 400) {
            String res = response.body().string();
            ResponseObj resObj = gson.fromJson(res, ResponseObj.class);
            ChatRequest req = gson.fromJson(resObj.message, ChatRequest.class);
//            System.out.println("Chat request res: " + res);
//            System.out.println("ChatRequest target: " + req.targetUser);
            return req;
        }
        return null;
    }

    public String getChatRequests(UserData current, ArrayList<ChatRequest> requestList) throws IOException{
        if (url.equals("")) return null;
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer" + current.token)
                .url(url + "/user/" + current.id + "/chat")
                .build();
        Response response = client.newCall(request).execute();
        String res = response.body().string();
        int resStatus = response.code();
        if (resStatus >= 200 && resStatus < 400) {
            //System.out.println("Chat requests: " + res);
            ResponseObj resObj = gson.fromJson(res, ResponseObj.class);
            if (!resObj.status.equals(GOOD_RES)){
                System.out.println("Invalid response");
                return null;
            }
            //System.out.println("Status was good");
            ChatRequest[] tempReqs = gson.fromJson(resObj.message.get("requests"), ChatRequest[].class);
            for (ChatRequest req : tempReqs){
                requestList.add(req);
            }
            return resObj.message.get("currentTime").getAsString();
        }
        return null;
    }

    public void setURL(String add){
        url = add;
    }

    public OkClient(){}

    public OkClient(String add){
        //TODO: verify address
        url = add;
    }
}

