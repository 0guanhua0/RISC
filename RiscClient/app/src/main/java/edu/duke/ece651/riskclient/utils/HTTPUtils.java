package edu.duke.ece651.riskclient.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import edu.duke.ece651.riskclient.Player;

import static edu.duke.ece651.riskclient.Constant.ACTION_LOGIN;
import static edu.duke.ece651.riskclient.Constant.ACTION_SIGN_UP;
import static edu.duke.ece651.riskclient.Constant.ACTION_TYPE;
import static edu.duke.ece651.riskclient.Constant.SUCCESSFUL;
import static edu.duke.ece651.riskclient.Constant.USER_NAME;
import static edu.duke.ece651.riskclient.Constant.USER_PASSWORD;

public class HTTPUtils {
    private static final String TAG = HTTPUtils.class.getSimpleName();

    public static String authUser(Player player){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, player.getName());
            jsonObject.put(USER_PASSWORD, player.getPassword());
            jsonObject.put(ACTION_TYPE, ACTION_LOGIN);
            // TODO: socket communicate with server here
            return SUCCESSFUL;
        }catch (JSONException e){
            Log.e(TAG, e.toString());
        }
        return "Magic error, try again.";
    }

    public static String addUser(Player player){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(USER_NAME, player.getName());
            jsonObject.put(USER_PASSWORD, player.getPassword());
            jsonObject.put(ACTION_TYPE, ACTION_SIGN_UP);
            // TODO: socket communicate with server here
            return SUCCESSFUL;
        }catch (JSONException e){
            Log.e(TAG, e.toString());
        }
        return "Magic error, try again.";
    }
}
