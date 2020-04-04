package edu.duke.ece651.riskclient.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.riskclient.listener.onNewPlayerListener;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.listener.onSendListener;
import edu.duke.ece651.riskclient.objects.Player;

import static edu.duke.ece651.riskclient.Constant.ACTION_CHANGE_PASSWORD;
import static edu.duke.ece651.riskclient.Constant.ACTION_GET_IN_ROOM;
import static edu.duke.ece651.riskclient.Constant.ACTION_GET_WAIT_ROOM;
import static edu.duke.ece651.riskclient.Constant.ACTION_LOGIN;
import static edu.duke.ece651.riskclient.Constant.ACTION_SIGN_UP;
import static edu.duke.ece651.riskclient.Constant.ACTION_TYPE;
import static edu.duke.ece651.riskclient.Constant.PASSWORD_NEW;
import static edu.duke.ece651.riskclient.Constant.PASSWORD_OLD;
import static edu.duke.ece651.riskclient.Constant.SUCCESSFUL;
import static edu.duke.ece651.riskclient.Constant.USER_NAME;
import static edu.duke.ece651.riskclient.Constant.USER_PASSWORD;
import static edu.duke.ece651.riskclient.RiskApplication.getPlayerName;
import static edu.duke.ece651.riskclient.RiskApplication.getThreadPool;
import static edu.duke.ece651.riskclient.RiskApplication.getTmpSocket;
import static edu.duke.ece651.riskclient.RiskApplication.recv;
import static edu.duke.ece651.riskclient.RiskApplication.send;

/**
 * This class contains some method which can be used to get data from server.
 * All these method use short connection, i.e. the socket will close after receive the response.
 */
public class HTTPUtils {
    private static final String TAG = HTTPUtils.class.getSimpleName();

    /**
     * This function will authenticate the player.
     * since we don't authenticate the player we won't save it globally.
     * @param player player to be authenticated
     * @param listener result listener
     */
    public static void authUser(Player player, onReceiveListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, player.getName());
            jsonObject.put(USER_PASSWORD, player.getPassword());
            jsonObject.put(ACTION_TYPE, ACTION_LOGIN);
            sendAndCheckSuccess(jsonObject.toString(), listener);
        } catch (JSONException e){
            Log.e(TAG, e.toString());
            listener.onFailure("JSON error(should not happen");
        }
    }

    /**
     * This function responsible for user sign up, add a user to DB.
     * * @param player player to be add
     * @param listener result listener
     */
    public static void addUser(Player player, onReceiveListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, player.getName());
            jsonObject.put(USER_PASSWORD, player.getPassword());
            jsonObject.put(ACTION_TYPE, ACTION_SIGN_UP);
            sendAndCheckSuccess(jsonObject.toString(), listener);
        }catch (JSONException e){
            Log.e(TAG, e.toString());
            listener.onFailure("JSON error(should not happen");
        }
    }

    /**
     * Change the password of particular user.
     * @param player player object(contains old password)
     * @param newPass new password
     * @param listener result listener
     */
    public static void changePassword(Player player, String newPass, onReceiveListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, player.getName());
            jsonObject.put(PASSWORD_OLD, player.getPassword());
            jsonObject.put(PASSWORD_NEW, newPass);
            jsonObject.put(ACTION_TYPE, ACTION_CHANGE_PASSWORD);
            sendAndCheckSuccess(jsonObject.toString(), listener);
        }catch (JSONException e){
            Log.e(TAG, e.toString());
            listener.onFailure("JSON error(should not happen");
        }
    }

    /**
     * This function will get the latest room list.
     * @param isRoomIn the room type, true for rooms you are in, false for not
     * @param listener result listener
     */
    public static void getRoomList(boolean isRoomIn, onReceiveListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, getPlayerName());
            jsonObject.put(ACTION_TYPE, isRoomIn ? ACTION_GET_IN_ROOM : ACTION_GET_WAIT_ROOM);
            send(jsonObject.toString(), new onSendListener() {
                @Override
                public void onFailure(String error) {
                    listener.onFailure(error);
                }

                @Override
                public void onSuccessful() {
                    // receive the room list
                    recv(listener);
                }
            });
        }catch (JSONException e){
            Log.e(TAG, e.toString());
            listener.onFailure("JSON error(should not happen");
        }

    }

    // receive the MapDataBase
    public static void getMapList(onReceiveListener listener){

    }

    // send info of the new room
    public static void createNewRoom(String roomName, String mapName, onReceiveListener listener){

    }

    /**
     * Set a listener to listen for new player.
     * @param listener new player listener
     */
    public static void waitAllPlayers(onNewPlayerListener listener){
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                listener.onFailure(error);
            }

            @Override
            public void onSuccessful(Object object) {
                if (object instanceof String){
                    // do something
                    if (object.equals("all")){
                        listener.onAllPlayer();
                    }else {
                        listener.onFailure((String) object);
                    }
                }else {
                    listener.onNewPlayer((Player) object);
                }
            }
        });
    }

    public static void getRoundInfo(onReceiveListener listener){
        recv(listener);
    }

    public static void sendAction(Action action, onSendListener listener){
        send(action, listener);
    }

    /**
     * Send the request string and check the response is successful or not.
     * This function is used in the case you don't care the return value, just care about whether
     * success or not.
     * @param request the request string
     * @param listener result listener
     */
    static void sendAndCheckSuccess(String request, onReceiveListener listener) {
//        listener.onSuccessful(null);
        // use the global thread pool to execute
        getThreadPool().execute(() -> {
            try {
                Socket socket = getTmpSocket();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                out.writeObject(request);
                out.flush();
                String res = (String) in.readObject();
                if (res.equals(SUCCESSFUL)) {
                    listener.onSuccessful(null);
                } else {
                    listener.onFailure(res);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                listener.onFailure("server is not running");
            }
        });
    }

    /**
     * Send the request and check the response is successful or not.
     * This function is used in the case you don't care the return value, just care about whether
     * success or not.
     * @param request the request object(ccan be anything)
     * @param listener result listener
     */
    static void sendAndRec(Object request, onReceiveListener listener) {
        // use the global thread pool to execute
        getThreadPool().execute(() -> {
            try {
                Socket socket = getTmpSocket();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                in.readObject();
                out.writeObject(request);
                out.flush();
                Object res = in.readObject();
                listener.onSuccessful(res);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                listener.onFailure("server is not running");
            }
        });
    }

}
