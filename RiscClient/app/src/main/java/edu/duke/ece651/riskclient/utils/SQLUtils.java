package edu.duke.ece651.riskclient.utils;

import androidx.room.Room;

import edu.duke.ece651.riskclient.listener.onInsertListener;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.objects.Message;
import edu.duke.ece651.riskclient.sql.AppDatabase;

import static edu.duke.ece651.riskclient.RiskApplication.getContext;
import static edu.duke.ece651.riskclient.RiskApplication.getThreadPool;

/**
 * This class contains all function you need to deal with the database related stuff.
 * To avoid blocking UI thread, all method will be asynchronous, if you want to get the result, pass in a listener.
 */
public class SQLUtils {
    // make this object singleton
    private static AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, "risk")
            .fallbackToDestructiveMigration()
            .build();

    public static void insertMessageAsy(Message message){
        getThreadPool().execute(() -> {
            // store the message to Local database
            db.messageDao().insert(message);
        });
    }

    public static void insertMessageAsy(Message message, onInsertListener listener){
        getThreadPool().execute(() -> {
            // store the message to Local database
            long id = db.messageDao().insert(message);
            listener.onSuccessful(id);
        });
    }

    public static void insertMessage(Message message){
        db.messageDao().insert(message);
    }

    public static void getAllMessagesAsy(int roomID, onReceiveListener listener){
        getThreadPool().execute(() -> {
            // store the message to Local database
            listener.onSuccessful(db.messageDao().getMessageByRoom(roomID));
        });
    }
}
