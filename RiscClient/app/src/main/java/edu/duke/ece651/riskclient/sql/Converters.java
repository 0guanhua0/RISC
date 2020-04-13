package edu.duke.ece651.riskclient.sql;

import android.util.Log;

import androidx.room.TypeConverter;

import com.stfalcon.chatkit.commons.models.IUser;

import org.json.JSONObject;

import java.util.Date;

import edu.duke.ece651.riskclient.objects.SimplePlayer;

public class Converters {
    private static final String TAG = Converters.class.getSimpleName();
    private static final String USER_NAME = "userName";
    private static final String USER_ID = "userID";

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static IUser fromString(String user){
        try {
            JSONObject object = new JSONObject(user);
            return new SimplePlayer(object.getInt(USER_ID), object.optString(USER_NAME));
        }catch (Exception e){
            Log.e(TAG, "fromString: " + e.toString());
        }
        return null;
    }

    @TypeConverter
    public static String userTOString(IUser user){
        try {
            JSONObject object = new JSONObject();
            object.put(USER_ID, user.getId());
            object.put(USER_NAME, user.getName());
            return object.toString();
        }catch (Exception e){
            Log.e(TAG, "userTOString: " + e.toString());
        }
        return "";
    }
}
