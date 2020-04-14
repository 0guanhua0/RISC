package edu.duke.ece651.riskclient;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.duke.ece651.risk.shared.RoomInfo;
import edu.duke.ece651.risk.shared.player.SMessage;
import edu.duke.ece651.riskclient.listener.onNewMessageListener;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.listener.onResultListener;
import edu.duke.ece651.riskclient.objects.Message;
import edu.duke.ece651.riskclient.objects.SimplePlayer;

import static edu.duke.ece651.risk.shared.Constant.ACTION_CONNECT_CHAT;
import static edu.duke.ece651.risk.shared.Constant.ROOM_ID;
import static edu.duke.ece651.risk.shared.Constant.SUCCESSFUL;
import static edu.duke.ece651.riskclient.Constant.ACTION_CREATE_NEW_ROOM;
import static edu.duke.ece651.riskclient.Constant.ACTION_TYPE;
import static edu.duke.ece651.riskclient.Constant.HOST;
import static edu.duke.ece651.riskclient.Constant.MAP_NAME;
import static edu.duke.ece651.riskclient.Constant.PORT;
import static edu.duke.ece651.riskclient.Constant.ROOM_NAME;
import static edu.duke.ece651.riskclient.Constant.USER_NAME;
import static edu.duke.ece651.riskclient.utils.SQLUtils.insertMessage;

public class RiskApplication extends Application {
    private static final String TAG = RiskApplication.class.getSimpleName();

    private static Context context;
    /* ====== below are the parameters that need in one game(only need one copy in the whole program) ====== */

    private static SimplePlayer player;
    // one player can only in one room at the same time
    private static RoomInfo room;
    // this socket is used to play a game
    // will be initialized once you join(or create) a room
    // will be closed once you leave a room
    private static Socket gameSocket;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    // this is the thread pool which dedicate for network communication
    private static ThreadPoolExecutor threadPool;
    /* ================== chat related variable ================== */
    // this socket is used to chat with other players in one game
    // will be initialized once the game is started
    // will be closed once you leave a room
    private static Socket chatSocket;
    private static ObjectInputStream chatIn;
    private static ObjectOutputStream chatOut;
    // this is the chatThread which dedicate to chatting in one room
    private static Thread chatThread;
    private static onNewMessageListener msgListener;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        // here we only need a relative very small thread pool, since it's almost impossible we will send two request at the same time
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(3);
        threadPool = new ThreadPoolExecutor(3, 5, 5, TimeUnit.SECONDS, workQueue);
        // warm up one core thread
        threadPool.prestartCoreThread();
        gameSocket = null;
        player = new SimplePlayer("", "");
        room = new RoomInfo(1, "");
        chatThread = null;
        msgListener = null;
    }

    public static Context getContext() {
        return context;
    }

    /**
     * Get a thread pool to execute some time-consuming task.
     * @return the global thread pool
     */
    public static ThreadPoolExecutor getThreadPool(){
        return threadPool;
    }

    public static void setPlayer(SimplePlayer p) {
        player = p;
    }

    public static void setPlayerID(int id){
        player.setId(id);
    }

    public static SimplePlayer getPlayer() {
        return player;
    }

    public static String getPlayerName() {
        return player.getName();
    }

    public static int getPlayerID() {
        return player.getIdInt();
    }

    public static void setRoom(RoomInfo r){
        room = r;
    }

    public static String getRoomName(){
        return room.getRoomName();
    }

    public static int getRoomID(){
        return room.getRoomID();
    }

    /**
     * This function is used to get a temporary socket.
     * You should close the socket once you finish using it.
     * @return a temporary socket
     * @throws IOException probably due to invalid host or port(which should not happen)
     */
    public static Socket getTmpSocket() throws IOException {
        Socket socket = new Socket(HOST, PORT);
        socket.setSoTimeout(3000);
        return socket;
    }

    /**
     * Initialize the game socket, will close any old one.
     * This function should be called before you entered any room(e.g. join or reconnect)
     * @param listener result listener
     */
    public static void initGameSocket(onResultListener listener) {
        threadPool.execute(() -> {
            try {
                releaseGameSocket();
                gameSocket = new Socket(HOST, PORT);

                // WARNING!!! here you should initialize "out-in" in this order!!! Otherwise, it will
                // cause deadlock.(Because server will initialize in "in-out" order.
                // https://stackoverflow.com/questions/21075453/objectinputstream-from-socket-getinputstream
                RiskApplication.out = new ObjectOutputStream(gameSocket.getOutputStream());
                RiskApplication.in = new ObjectInputStream(gameSocket.getInputStream());
                Log.e(TAG, "initGameSocket success");
                listener.onSuccessful();
            }catch (IOException e){
                Log.e(TAG, "initGameSocket error");
                listener.onFailure("can't initialize the game socket");
            }
        });
    }

    /**
     *
     * @param listener result listener
     */
    public static void startChatThread(onResultListener listener){
        chatThread = new Thread(() -> {
            try {
                // create a new chat socket
                chatSocket = new Socket(HOST, PORT);
                // WARNING!!! here you should initialize "out-in" in this order!!! Otherwise, it will
                // cause deadlock.(Because server will initialize in "in-out" order.
                // https://stackoverflow.com/questions/21075453/objectinputstream-from-socket-getinputstream
                RiskApplication.chatOut = new ObjectOutputStream(chatSocket.getOutputStream());
                RiskApplication.chatIn = new ObjectInputStream(chatSocket.getInputStream());

                // send the header message, connect to corresponding room
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(USER_NAME, getPlayerName());
                jsonObject.put(ROOM_ID, getRoomID());
                jsonObject.put(ACTION_TYPE, ACTION_CONNECT_CHAT);
                chatOut.writeObject(jsonObject.toString());
                chatOut.flush();
                Object object = chatIn.readObject();
                if (object instanceof String){
                    if (object.equals(SUCCESSFUL)){
                        listener.onSuccessful();
                    }else{
                        listener.onFailure((String) object);
                    }
                }else {
                    listener.onFailure("init chat error");
                }
                // an endless loo which will keep receiving message and write to DB
                while (!Thread.currentThread().isInterrupted()){
                    Object o = recvChatBlock();
                    if (o instanceof SMessage){
                        Message m = new Message((SMessage) o);
                        insertMessage(m);
                        if (msgListener != null){
                            msgListener.onNewMessage(m);
                        }
                    }
                }
            }catch (IOException | JSONException | ClassNotFoundException e){
                Log.e(TAG, "initChatSocket error " + e.toString());
                listener.onFailure("can't initialize the chat socket");
            }
        });
        chatThread.start();
    }

    public static void stopChatThread(){
        if (chatThread != null){
            // interrupt the thread
            chatThread.interrupt();
            // close the socket, make the recv function return
            releaseChatSocket();
            // release the listener
            msgListener = null;
        }
        chatThread = null;
    }

    public static void registerMsgListener(onNewMessageListener listener){
        RiskApplication.msgListener = listener;
    }

    /**
     * Initialize the chat socket, will close any old one.
     * This function should be called only after you successfully join(reconnect the room) and the game is start.
     * The server only support chat function after the game is begin.
     * @param listener result listener
     */
//    public static void initChatSocket(onResultListener listener) {
//        threadPool.execute(() -> {
//            try {
//                releaseChatSocket();
//                chatSocket = new Socket(HOST, PORT);
//                // WARNING!!! here you should initialize "out-in" in this order!!! Otherwise, it will
//                // cause deadlock.(Because server will initialize in "in-out" order.
//                // https://stackoverflow.com/questions/21075453/objectinputstream-from-socket-getinputstream
//                RiskApplication.chatOut = new ObjectOutputStream(chatSocket.getOutputStream());
//                RiskApplication.chatIn = new ObjectInputStream(chatSocket.getInputStream());
//
//                // send the connect chat message
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put(USER_NAME, getPlayerName());
//                jsonObject.put(ROOM_ID, getRoomID());
//                jsonObject.put(ACTION_TYPE, ACTION_CONNECT_CHAT);
//                // send and receive the result
//                chatOut.writeObject(jsonObject.toString());
//                chatOut.flush();
//                Object object = chatIn.readObject();
//                if (object instanceof String){
//                    if (object.equals(SUCCESSFUL)){
//                        listener.onSuccessful();
//                    }else{
//                        listener.onFailure((String) object);
//                    }
//                }else {
//                    listener.onFailure("");
//                }
//            }catch (IOException | JSONException | ClassNotFoundException e){
//                Log.e(TAG, "initGameSocket error " + e.toString());
//                listener.onFailure("can't initialize the game socket");
//            }
//        });
//    }

    /**
     * Send data to remote server, and check whether send successful.
     * Because android doesn't allow networking operation on the main thread.
     * We should use a thread pool to send info.
     * @param object object going to be sent
     * @param listener send result listener
     */
    public synchronized static void send(Object object, onResultListener listener) {
        threadPool.execute(() -> {
            try {
                out.writeObject(object);
                out.flush();
                listener.onSuccessful();
            }catch (Exception e){
                Log.e(TAG, "send error: " + e.toString());
                listener.onFailure(e.toString());
            }
        });
    }

    /**
     * Receive data from remote server.
     * We can't use return value here because android won't allow blocking on the main thread.
     * Also, this function should catch all exception and use the listener to notify main thread.
     */
    public synchronized static void recv(onReceiveListener listener) {
        threadPool.execute(() -> {
            try {
                Object o = in.readObject();
                listener.onSuccessful(o);
            }catch (Exception e){
                Log.e(TAG, "receiver error: " + e.toString());
                listener.onFailure(e.toString());
            }
        });
    }

    /**
     * Send chat message.
     * @param object chat message, should only be SMessage object
     */
    public static void sendChat(Object object) {
        threadPool.execute(() -> {
            try {
                chatOut.writeObject(object);
                chatOut.flush();
            }catch (Exception e){
                Log.e(TAG, "send chat error: " + e.toString());
            }
        });
    }

//    public static void recvChat(onReceiveListener listener) {
//        threadPool.execute(() -> {
//            try {
//                Object o = chatIn.readObject();
//                listener.onSuccessful(o);
//            }catch (Exception e){
//                Log.e(TAG, "receiver chat error: " + e.toString());
//                listener.onFailure(e.toString());
//            }
//        });
//    }

    /**
     * Receive chat message in a blocking way.
     */
    public static Object recvChatBlock() {
        try {
            return chatIn.readObject();
        }catch (Exception e){
            Log.e(TAG, "recvChatBlock: " + e.toString());
        }
        return null;
    }

    /**
     * Release(close) the game socket.
     * Since this function not use thread pool inside, don't call it on MainThread.
     */
    public static void releaseGameSocket() {
        try {
            if (gameSocket != null && !gameSocket.isClosed()){
                gameSocket.shutdownInput();
                gameSocket.shutdownOutput();
                gameSocket.close();
                gameSocket = null;
            }
        }catch (IOException e){
            Log.e(TAG, "releaseGameSocket error");
        }
    }

    /**
     * Release(close) the game socket.
     * Since this function not use thread pool inside, don't call it on MainThread.
     */
    public static void releaseChatSocket() {
        try {
            if (chatSocket != null && !chatSocket.isClosed()){
                chatSocket.shutdownOutput();
                chatSocket.shutdownInput();
                chatSocket.close();
            }
        }catch (IOException e){
            Log.e(TAG, "releaseChatSocket error");
        }
    }

//    public static void releaseChatSocketAsy() {
//        threadPool.execute(() -> {
//            try {
//                if (chatSocket != null && !chatSocket.isClosed()){
//                    chatSocket.close();
//                    chatSocket.shutdownOutput();
//                    chatSocket.shutdownInput();
//                }
//            }catch (IOException e){
//                Log.e(TAG, "releaseChatSocket error");
//            }
//        });
//    }

    /**
     * This function will receive a result message from server, and return the error message if fail.
     */
    public static void checkResult(onResultListener listener){
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                listener.onFailure(error);
                Log.e(TAG, "checkResult: " + error);
            }

            @Override
            public void onSuccessful(Object object) {
                if (object instanceof String){
                    if (object.equals(SUCCESSFUL)){
                        listener.onSuccessful();
                    }else{
                        listener.onFailure((String) object);
                    }
                }else {
                    listener.onFailure("");
                }
            }
        });
    }
}
