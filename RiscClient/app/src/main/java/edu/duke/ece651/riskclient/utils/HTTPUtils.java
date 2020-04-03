package edu.duke.ece651.riskclient.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.duke.ece651.riskclient.objects.Player;
import edu.duke.ece651.riskclient.listener.onResultListener;

import static edu.duke.ece651.riskclient.Constant.ACTION_GET_ROOM_IN;
import static edu.duke.ece651.riskclient.Constant.ACTION_GET_ROOM_WAIT;
import static edu.duke.ece651.riskclient.Constant.ACTION_LOGIN;
import static edu.duke.ece651.riskclient.Constant.ACTION_SIGN_UP;
import static edu.duke.ece651.riskclient.Constant.ACTION_TYPE;
import static edu.duke.ece651.riskclient.Constant.HOST;
import static edu.duke.ece651.riskclient.Constant.PORT;
import static edu.duke.ece651.riskclient.Constant.SUCCESSFUL;
import static edu.duke.ece651.riskclient.Constant.USER_NAME;
import static edu.duke.ece651.riskclient.Constant.USER_PASSWORD;

/**
 * This class contains some method which can be used to get data from server.
 * All these method use short connection, i.e. the socket will close after receive the response.
 */
public class HTTPUtils {
    private static final String TAG = HTTPUtils.class.getSimpleName();

    /**
     * This function will authenticate the player.
     * @param player player to be authenticated
     * @param listener result listener
     */
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

    /**
     * This function responsible for user sign up, add a user to DB.
     * @param player new player object
     * @param listener result listener
     */
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
     * This function will get the latest room list.
     * @param player player object(used to authentication)
     * @param isRoomIn the room type, true for rooms you are in, false for not
     * @param listener result listener
     */
    public static void getRoomList(Player player, boolean isRoomIn, onResultListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, player.getName());
            jsonObject.put(ACTION_TYPE, isRoomIn ? ACTION_GET_ROOM_IN : ACTION_GET_ROOM_WAIT);
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
        listener.onSuccessful(null);

//        new Thread(() -> {
//            try (Socket socket = new Socket(getIP(), PORT)) {
//                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
//                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
//                in.readObject();
//                out.writeObject(str);
//                out.flush();
//                String res = (String) in.readObject();
//                if (res.equals(SUCCESSFUL)){
//                    listener.onSuccessful();
//                }else {
//                    listener.onFailure(res);
//                }
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//                listener.onFailure("server is not running");
//            }
//        }).start();
    }

    static String getIP() throws UnknownHostException {
        return InetAddress.getByName(HOST).getHostAddress();
    }

}
