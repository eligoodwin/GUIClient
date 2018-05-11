
//Source: http://www.vogella.com/tutorials/JavaLibrary-OkHttp/article.html
package ClientAccountNetworking;

import java.io.IOException;
import java.util.ArrayList;

import QueryObjects.FriendData;
import QueryObjects.ResponseArray;
import QueryObjects.ResponseMsg;
import QueryObjects.UserData;
import Util.JSONhelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import static ClientAccountNetworking.QueryObjectsHelper.getLogon;
import static java.lang.Integer.parseInt;

public class OkClient {
    private final static String GOOD_RES = "VALID REQUEST";
    private final static String API_TOKEN = "fXtas7yB2HcIVoCyyQ78";
    private final static String SERVER_ADDRESS = "http://localhost:8080";
    public static Gson gson = new Gson();
    //Source: https://stackoverflow.com/questions/4802887/gson-how-to-exclude-specific-fields-from-serialization-without-annotations
    public static Gson exGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();
    public String url = SERVER_ADDRESS;
    public int connectToServer(int token){
        return 0;
    }

    //TODO: store response to modularize code and allow flexible handling of response
    public String sendGet() throws IOException {
        if (url == "") return "No URL set!";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    //TODO: don't need UserLogon, can just use UserData
    //RES: {"message" : {"authToken" : "testToken"}, "status" : "VALID REQUEST"}
    public String logon(UserData user) throws IOException {
        String ret = "Error";
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
            ResponseMsg resObj = gson.fromJson(res, ResponseMsg.class);
            ret = resObj.status;
            if (ret.equals(GOOD_RES)) {
                user.token = resObj.message.get("authToken").toString();
                user.id = Long.parseLong(resObj.message.get("id").toString());
            }
            //TODO: retry route
        }
        return ret;
    }

    public String sendPost(String msg) throws IOException{
        if (url == "") return "No URL set!";
        RequestBody body = RequestBody.create(JSON, msg);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public void addUser(UserData user) throws IOException {
        if (url == "") return;
        user.API_token = API_TOKEN;
        String json = exGson.toJson(user);
        System.out.println("JSON string:\n" + json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url + "/user")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        //TODO: this may need to be updated for the new message format
        json = response.body().string();
        System.out.println(json);
        ResponseMsg resObj = gson.fromJson(json, ResponseMsg.class);
        UserData tempUser = gson.fromJson(resObj.message.getAsJsonObject("userDetails"), UserData.class);
        user.token = tempUser.token;
        user.id = tempUser.id;
    }

    public String checkToken(UserData user) throws IOException{
        if (url == "") return "No URL set!";
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
        if (url == "") return "No URL set!";
        String json = "{\"jwt\":\"" + jwt + "\"}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url + "/testJWT")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String requestFriend(String username, UserData current) throws IOException{
        if (url == "") return "No URL set!";
        Request request = new Request.Builder()
                .url(url + "/user/" + current.id + "/friend/" + username)
                .build();
        Response response = client.newCall(request).execute();
        String json = response.body().string();
        System.out.println(json);
        int status = response.code();
        if (status >= 200 && status < 400) {
            JsonObject resObj = gson.fromJson(json, JsonObject.class);
            String res = resObj.get("status").toString();
            System.out.println("Status: " + res);
            if (res.equals("\"VALID REQUEST\"")) return "OK";
        }
        return "Request Error";
    }

    public String acceptFriend(String username, UserData current) throws IOException{
        if (url == "") return "No URL set!";
        Request request = new Request.Builder()
                .url(url + "/user/" + current.id + "/friend/" + username + "/2")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    //TODO: update return type when determined how to parse code
    public String getFriends(UserData current, ArrayList<FriendData> friends) throws IOException {
        if (url == "") return "No URL";
        Request request = new Request.Builder()
                .url(url + "/user/" + current.id + "/friend")
                .build();
        Response response = client.newCall(request).execute();
        int status = response.code();
        if (status >= 200 && status < 400) {
            String res = response.body().string();
            System.out.println(res); //TODO: trace
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
            String res = response.body().string();
            ResponseArray resObj = gson.fromJson(res, ResponseArray.class);
            System.out.println(res);
            res = resObj.status;
            return res;
        }
    }

    public String updateIP(UserData current) throws IOException{
        if (url == "") return "No URL set!";
        RequestBody body = RequestBody.create(JSON, "{\"newIP\":\"\"");
        Request request = new Request.Builder()
                .url(url + "/user/" + current.id + "/ipAddress=?" + current.ipAddress + "&portNumber=?" + current.peerServerPort)
                .put(body)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
        return response.body().string();
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

