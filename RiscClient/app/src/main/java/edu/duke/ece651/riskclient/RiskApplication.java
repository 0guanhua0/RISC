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

import edu.duke.ece651.risk.shared.Room;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.listener.onSendListener;
import edu.duke.ece651.riskclient.objects.Player;

import static edu.duke.ece651.riskclient.Constant.HOST;
import static edu.duke.ece651.riskclient.Constant.PORT;

public class RiskApplication extends Application {
    private static final String TAG = RiskApplication.class.getSimpleName();

    private static Context context;
    /* ====== below are the parameters that need in one game(only need one copy in the whole program) ====== */

    private static Player player;
    // one player can only in one room at the same time
    private static Room room;
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

    public static void setPlayer(Player p) {
        player = p;
    }

    public static Player getPlayer() {
        return player;
    }

    public static String getPlayerName() {
        return player.getName();
    }

    public static void setRoom(Room r){
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
        return new Socket(HOST, PORT);
    }

    /**
     * Initialize the game socket, will close any old one.
     * @throws IOException probably due to invalid host or port(which should not happen)
     */
    public static void initGameSocket() throws IOException {
        releaseGameSocket();
        gameSocket = new Socket(HOST, PORT);
        /*
          WARNING!!! here you should initialize "out-in" in this order!!! Otherwise, it will
          cause deadlock.(Because server will initialize in "in-out" order.
          https://stackoverflow.com/questions/21075453/objectinputstream-from-socket-getinputstream
         */
        out = new ObjectOutputStream(gameSocket.getOutputStream());
        in = new ObjectInputStream(gameSocket.getInputStream());
    }

    /**
     * Send data to remote server.
     * Because android doesn't allow networking operation on the main thread.
     * We should use a thread pool to send info.
     * @param object object going to be sent
     * @param listener send result listener
     */
    public static void send(Object object, onSendListener listener) {
        threadPool.execute(() -> {
            try {
                out.writeObject(object);
                out.flush();
                listener.onSuccessful();
            }catch (IOException e){
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
        threadPool.execute(() -> {
            try {
                Object o = in.readObject();
                listener.onSuccessful(o);
            }catch (IOException | ClassNotFoundException e){
                Log.e(TAG, "receiver error: " + e.toString());
                listener.onFailure(e.toString());
            }
        });
    }

    /**
     * Release(close) the game socket.
     * @throws IOException IO error occur when closing the socket
     */
    public static void releaseGameSocket() throws IOException {
        if (gameSocket != null && !gameSocket.isClosed()){
            gameSocket.close();
        }
    }
}
