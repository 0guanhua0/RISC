package edu.duke.ece651.riskclient.sql;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

import edu.duke.ece651.riskclient.objects.Message;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM message")
    List<Message> getAll();

    @Query("SELECT * FROM message WHERE roomID=:roomID ORDER BY date DESC")
    List<Message> getMessageByRoom(int roomID);

    @Insert
    long insert(Message message);
}
