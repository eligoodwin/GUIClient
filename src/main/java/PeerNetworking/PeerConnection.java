package PeerNetworking;

import Controller.ChatInterface;
import QueryObjects.FriendData;
import QueryObjects.UserData;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class PeerConnection {
    private int localPort;
    private int serverPort;
    private UserData user;
    private FriendData friend;
    private boolean running = true;
    private Socket connectionClient;
    private Thread incomingThread;
    private String peerServerAddress;
    private ChatInterface parentWindow = null;

    public synchronized boolean getRunning(){ return running;}

    public synchronized void setRunning(boolean set){
        running = set;
    }

    public void setParentWindow(ChatInterface window){
        parentWindow = window;
    }

    public PeerConnection(UserData usr, FriendData frnd) {
        this.user = usr;
        this.friend = frnd;
        this.peerServerAddress = friend.ipAddress;
        this.localPort = Integer.parseInt(user.peerServerPort);
        this.serverPort = Integer.parseInt(friend.peerServerPort);
    }

    public int connectNatless(){
        connectionClient = new Socket();
        try {
            findSocket();
        }
        catch(SocketException e){
            e.printStackTrace();
            return -1;
        }
        try {
            connectionClient.connect(new InetSocketAddress(peerServerAddress, serverPort));
        }
        catch(IOException e){
            e.printStackTrace();
            return -2;
        }
        return 0;
    }

    public void startReceiving(){
        incomingThread = new Thread(this::receiveMessages);
        incomingThread.start();
    }

    private void receiveMessages(){
        try {
            BufferedReader input = getBuffer(connectionClient);
            while(getRunning()){
                String msg = input.readLine();
                if(parentWindow != null){
                    parentWindow.sendMessageToWindow(parentWindow.userIsNotSource(msg));
                }
                System.out.println(msg);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public int sendMessage(String msg){
        try {
            PrintWriter out =
                    new PrintWriter(connectionClient.getOutputStream(), true);
            out.println(msg);
        }
        catch(IOException e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    private BufferedReader getBuffer(Socket connectionClient) throws IOException {
        InputStream inputStream = connectionClient.getInputStream();
        return new BufferedReader((new InputStreamReader(inputStream)));
    }

    private void findSocket() throws SocketException {
        ServerSocket sock = null;
        if (localPort > 65535) throw new SocketException();
        while (true) {
            try {
                connectionClient.setReuseAddress(true);
                connectionClient.bind(new InetSocketAddress(localPort));
                break;
            }
            catch(IOException e){
                localPort++;
            }
        }
    }

    public void stopConnection(){
        setRunning(false);
        try {
            connectionClient.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
