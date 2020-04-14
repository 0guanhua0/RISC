package edu.duke.ece651.riskclient.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;

import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.riskclient.listener.onNewPlayerListener;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.listener.onRecvAttackResultListener;
import edu.duke.ece651.riskclient.listener.onResultListener;
import edu.duke.ece651.riskclient.objects.Message;
import edu.duke.ece651.riskclient.objects.SimplePlayer;

import static edu.duke.ece651.risk.shared.Constant.ACTION_CREATE_GAME;
import static edu.duke.ece651.risk.shared.Constant.ACTION_JOIN_GAME;
import static edu.duke.ece651.risk.shared.Constant.ACTION_RECONNECT_ROOM;
import static edu.duke.ece651.risk.shared.Constant.ROOM_ID;
import static edu.duke.ece651.risk.shared.Constant.ROUND_OVER;
import static edu.duke.ece651.riskclient.Constant.ACTION_CHANGE_PASSWORD;
import static edu.duke.ece651.riskclient.Constant.ACTION_CREATE_NEW_ROOM;
import static edu.duke.ece651.riskclient.Constant.ACTION_GET_IN_ROOM;
import static edu.duke.ece651.riskclient.Constant.ACTION_GET_WAIT_ROOM;
import static edu.duke.ece651.riskclient.Constant.ACTION_LOGIN;
import static edu.duke.ece651.riskclient.Constant.ACTION_SIGN_UP;
import static edu.duke.ece651.riskclient.Constant.ACTION_TYPE;
import static edu.duke.ece651.riskclient.Constant.FAIL_TO_SEND;
import static edu.duke.ece651.riskclient.Constant.INFO_ALL_PLAYER;
import static edu.duke.ece651.riskclient.Constant.MAP_NAME;
import static edu.duke.ece651.riskclient.Constant.PASSWORD_NEW;
import static edu.duke.ece651.riskclient.Constant.PASSWORD_OLD;
import static edu.duke.ece651.riskclient.Constant.ROOM_NAME;
import static edu.duke.ece651.riskclient.Constant.SUCCESSFUL;
import static edu.duke.ece651.riskclient.Constant.USER_NAME;
import static edu.duke.ece651.riskclient.Constant.USER_PASSWORD;
import static edu.duke.ece651.riskclient.RiskApplication.checkResult;
import static edu.duke.ece651.riskclient.RiskApplication.getPlayerName;
import static edu.duke.ece651.riskclient.RiskApplication.getRoomID;
import static edu.duke.ece651.riskclient.RiskApplication.getThreadPool;
import static edu.duke.ece651.riskclient.RiskApplication.getTmpSocket;
import static edu.duke.ece651.riskclient.RiskApplication.recv;
import static edu.duke.ece651.riskclient.RiskApplication.recvChat;
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
    public static void authUser(SimplePlayer player, onResultListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, player.getName());
            jsonObject.put(USER_PASSWORD, player.getPassword());
            jsonObject.put(ACTION_TYPE, ACTION_LOGIN);
            sendAndCheckSuccessT(jsonObject.toString(), listener);
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
    public static void addUser(SimplePlayer player, onResultListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, player.getName());
            jsonObject.put(USER_PASSWORD, player.getPassword());
            jsonObject.put(ACTION_TYPE, ACTION_SIGN_UP);
            sendAndCheckSuccessT(jsonObject.toString(), listener);
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
    public static void changePassword(SimplePlayer player, String newPass, onResultListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, player.getName());
            jsonObject.put(PASSWORD_OLD, player.getPassword());
            jsonObject.put(PASSWORD_NEW, newPass);
            jsonObject.put(ACTION_TYPE, ACTION_CHANGE_PASSWORD);
            sendAndCheckSuccessT(jsonObject.toString(), listener);
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
            sendAndRec(jsonObject.toString(), new onReceiveListener() {
                @Override
                public void onFailure(String error) {
                    listener.onFailure(error);
                }

                @Override
                public void onSuccessful(Object object) {
                    listener.onSuccessful(object);
                }
            });
        }catch (JSONException e){
            Log.e(TAG, e.toString());
            listener.onFailure("JSON error(should not happen");
        }
    }

    /* ====== the functions below should use the game socket rather than a temporary socket ====== */
    /**
     * Tell the server we want to create a new room rather than join in an existing room(use game socket).
     * @param listener result listener
     */
    public static void createNewRoom(onResultListener listener){
        try {
            // information header(tell serve what we want to do)
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, getPlayerName());
            jsonObject.put(ACTION_TYPE, ACTION_CREATE_GAME);
            send(jsonObject.toString(), new onResultListener() {
                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "createNewRoom: " + error);
                }

                @Override
                public void onSuccessful() {
                    // create a new room
                    sendAndCheckSuccessG("-1", listener);
                }
            });
        }catch (JSONException e){
            Log.e(TAG, "createNewRoom: " + e.toString());
            listener.onFailure(e.toString());
        }
    }

    /**
     * Send the new room info(room name + map name) to server and then check whether create successful.
     * @param roomName room name
     * @param mapName map name
     * @param listener result listener
     */
    public static void sendNewRoomInfo(String roomName, String mapName, onResultListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, getPlayerName());
            jsonObject.put(ROOM_NAME, roomName);
            jsonObject.put(MAP_NAME, mapName);
            jsonObject.put(ACTION_TYPE, ACTION_CREATE_NEW_ROOM);
            sendAndCheckSuccessG(jsonObject.toString(), listener);
        }catch (JSONException e){
            Log.e(TAG, e.toString());
            listener.onFailure("JSON error(should not happen");
        }
    }

    /**
     * Set a listener to listen for new player.
     * This function will continue call itself(endless) until receive
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
                    if (object.equals(INFO_ALL_PLAYER)){
                        listener.onAllPlayer();
                    }else {
                        try {
                            listener.onNewPlayer(new SimplePlayer((String) object, ""));
                            waitAllPlayers(listener);
                        }catch (Exception e){
                            Log.e(TAG, "waitAllPlayers: " + e.toString());
                            listener.onFailure(e.toString());
                        }
                    }
                }
            }
        });
    }

    /**
     * A new player want to join an existing room.
     * @param listener result listener
     */
    public static void joinGame(onResultListener listener){
        try {
            // information header(tell serve what we want to do)
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, getPlayerName());
            jsonObject.put(ACTION_TYPE, ACTION_JOIN_GAME);
            send(jsonObject.toString(), new onResultListener() {
                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "joinRoom: " + error);
                }

                @Override
                public void onSuccessful() {
                    // join in an existing room
                    sendAndCheckSuccessG(String.valueOf(getRoomID()), listener);
                }
            });
        }catch (JSONException e){
            Log.e(TAG, "joinRoom: " + e.toString());
        }
    }

    /**
     * A player want to back to one game he was playing but not finish.
     * @param listener result listener
     */
    public static void backGame(onResultListener listener){
        try {
            // information header(tell serve what we want to do)
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, getPlayerName());
            jsonObject.put(ACTION_TYPE, ACTION_RECONNECT_ROOM);
            jsonObject.put(ROOM_ID, getRoomID());
            // join in an existing room
            sendAndCheckSuccessG(jsonObject.toString(), listener);
        }catch (JSONException e){
            Log.e(TAG, "backGame: " + e.toString());
        }
    }

    /**
     * Send the territory group to server to verify, return success or fail message.
     * @param group the territory group you choose
     * @param listener result listener
     */
    public static void verifySelectGroup(Set<String> group, onResultListener listener){
        send(group, new onResultListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "verifySelectGroup: " + error);
            }

            @Override
            public void onSuccessful() {
                checkResult(listener);
            }
        });
    }

    /**
     * Send the result of assigning units to verify.
     * @param selection result of assigning units
     * @param listener result listener
     */
    public static void verifyAssignUnits(ServerSelect selection, onResultListener listener){
        send(selection, new onResultListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "verifyAssignUnits: " + error);
            }

            @Override
            public void onSuccessful() {
                checkResult(listener);
            }
        });
    }

    /**
     * This function will send an action to the server, and receive the validation result.
     * @param action action to be sent
     * @param listener result listener
     */
    public static void sendAction(Action action, onResultListener listener){
        send(action, new onResultListener() {
            @Override
            public void onFailure(String error) {
                // fail to send the action
                listener.onFailure(FAIL_TO_SEND);
            }

            @Override
            public void onSuccessful() {
                // successful send the action, receive the result
                checkResult(new onResultListener() {
                    @Override
                    public void onFailure(String error) {
                        // invalid action
                        listener.onFailure(error);
                    }

                    @Override
                    public void onSuccessful() {
                        // valid action
                        listener.onSuccessful();
                    }
                });
            }
        });
    }

    public static void recvAttackResult(onRecvAttackResultListener listener){
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                listener.onFailure(error);
            }

            @Override
            public void onSuccessful(Object object) {
                if (object instanceof String){
                    String result = (String) object;
                    // received all attack result, start a new round
                    if (result.equals(ROUND_OVER)){
                        listener.onOver();
                    }else {
                        listener.onNewResult(result);
                        // keep listening
                        recvAttackResult(listener);
                    }
                }
            }
        });
    }

    /**
     * This method will listen the chat message as long as the socket is open.
     * @param listener receive listener
     */
    public static void listenChatMessage(onReceiveListener listener){
        recvChat(new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                listener.onFailure(error);
            }

            @Override
            public void onSuccessful(Object object) {
                if (object instanceof Message){
                    listener.onSuccessful(object);
                    // keep receiving
                    listenChatMessage(listener);
                }
            }
        });
    }

    /* ====== helper function ====== */
    /**
     * Send the request string and check the response is successful or not(using a tmp socket).
     * This function is used in the case you don't care the return value, just care about whether
     * success or not.
     * @param request the request string
     * @param listener result listener
     */
    static void sendAndCheckSuccessT(String request, onResultListener listener) {
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
                    listener.onSuccessful();
                } else {
                    listener.onFailure(res);
                }
                socket.close();
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

    /**
     * This function has the same effect as sendAndCheckSuccessT, but using the game socket.
     * @param object object to be sent
     * @param listener result listener
     */
    static void sendAndCheckSuccessG(Object object, onResultListener listener){
        send(object, new onResultListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "sendAndCheckSuccessG: " + error);
            }

            @Override
            public void onSuccessful() {
                checkResult(listener);
            }
        });
    }
}

