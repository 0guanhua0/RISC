package edu.duke.ece651.riskclient;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.duke.ece651.risk.shared.RoomInfo;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.listener.onResultListener;
import edu.duke.ece651.riskclient.objects.SimplePlayer;

import static edu.duke.ece651.risk.shared.Constant.SUCCESSFUL;
import static edu.duke.ece651.riskclient.ClientConstant.HOST;
import static edu.duke.ece651.riskclient.ClientConstant.PORT;

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

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        // here we only need a relative very small thread pool, since it's almost impossible we will send two request at the same time
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(3);
        threadPool = new ThreadPoolExecutor(1, 3, 5, TimeUnit.SECONDS, workQueue);
        // warm up one core thread
        threadPool.prestartCoreThread();
        gameSocket = null;
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
        return player.getId();
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
                listener.onSuccessful();
            }catch (IOException e){
                Log.e(TAG, "initGameSocket error");
                listener.onFailure("can't initialize the game socket");
            }
        });
    }

    /**
     * Send data to remote server(use the game socket).
     * @param object object going to be sent
     */
    public static void send(Object object) {
        threadPool.execute(() -> {
            try {
                out.writeObject(object);
                out.flush();
            }catch (Exception e){
                Log.e(TAG, "send error: " + e.toString());
            }
        });
    }

    /**
     * Send data to remote server, and check whether send successful.
     * Because android doesn't allow networking operation on the main thread.
     * We should use a thread pool to send info.
     * @param object object going to be sent
     * @param listener send result listener
     */
    public static void send(Object object, onResultListener listener) {
//        listener.onSuccessful();
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
    public static void recv(onReceiveListener listener) {
//        listener.onSuccessful(SUCCESSFUL);
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
     * Release(close) the game socket.
     * Since this function not use thread pool inside, don't call it on MainThread.
     */
    public static void releaseGameSocket() {
        try {
            if (gameSocket != null && !gameSocket.isClosed()){
                gameSocket.close();
            }
        }catch (IOException e){
            Log.e(TAG, "releaseGameSocket error");
        }
    }

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
