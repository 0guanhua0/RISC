package edu.duke.ece651.riskclient;

import android.app.Application;
import android.content.Context;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.duke.ece651.riskclient.objects.Player;

import static edu.duke.ece651.riskclient.Constant.HOST;
import static edu.duke.ece651.riskclient.Constant.PORT;

public class RiskApplication extends Application {
    private static final String TAG = RiskApplication.class.getSimpleName();

    private static Context context;
    private static Player player;
    // this socket is used to play a game
    // will be initialized once you join(or create) a room
    // will be closed once you leave a room
    private static Socket gameSocket;


    @Override
    public void onCreate() {
        super.onCreate();
        RiskApplication.context = getApplicationContext();
    }

    public static Context getContext() {
        return RiskApplication.context;
    }

    public static Player getPlayer() {
        return player;
    }

    public static String getPlayerName() {
        return player.getName();
    }

    public static void setPlayer(Player p) {
        player = p;
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
    }

    /**
     * This function will return the game socket.
     * which used to perform a whole game i.e. keep alive for a long time
     * @return the game socket
     */
    public static Socket getGameSocket(){
        return gameSocket;
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
