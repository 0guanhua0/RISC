package edu.duke.ece651.riskclient.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.duke.ece651.riskclient.Player;
import edu.duke.ece651.riskclient.onResultListener;

import static edu.duke.ece651.riskclient.Constant.ACTION_LOGIN;
import static edu.duke.ece651.riskclient.Constant.ACTION_SIGN_UP;
import static edu.duke.ece651.riskclient.Constant.ACTION_TYPE;
import static edu.duke.ece651.riskclient.Constant.HOST;
import static edu.duke.ece651.riskclient.Constant.PORT;
import static edu.duke.ece651.riskclient.Constant.SUCCESSFUL;
import static edu.duke.ece651.riskclient.Constant.USER_NAME;
import static edu.duke.ece651.riskclient.Constant.USER_PASSWORD;

public class HTTPUtils {
    private static final String TAG = HTTPUtils.class.getSimpleName();

    public static void authUser(Player player, onResultListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, player.getName());
            jsonObject.put(USER_PASSWORD, player.getPassword());
            jsonObject.put(ACTION_TYPE, ACTION_LOGIN);
            sendAndRec(jsonObject.toString(), listener);
        } catch (JSONException e){
            Log.e(TAG, e.toString());
            listener.onFailure("JSON error(should not happen");
        }
    }

    public static void addUser(Player player, onResultListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, player.getName());
            jsonObject.put(USER_PASSWORD, player.getPassword());
            jsonObject.put(ACTION_TYPE, ACTION_SIGN_UP);
            sendAndRec(jsonObject.toString(), listener);
        }catch (JSONException e){
            Log.e(TAG, e.toString());
            listener.onFailure("JSON error(should not happen");
        }
    }

    /**
     * This function will construct a socket, send the request, receive a response and then close the socket.
     * @param str the request string
     * @param listener result listener
     */
    static void sendAndRec(String str, onResultListener listener) {
        listener.onSuccessful();
        // no serve for now, so simply successful
        /*new Thread(() -> {
            try {
                Socket socket = new Socket(getIP(), PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(str);
                out.flush();
                String res = in.readLine();
                if (res.equals(SUCCESSFUL)){
                    listener.onSuccessful();
                }else {
                    listener.onFailure(res);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                listener.onFailure("server is not running");
            }
        }).start();*/
    }

    static String getIP() throws UnknownHostException {
        return InetAddress.getByName(HOST).getHostAddress();
    }

}
