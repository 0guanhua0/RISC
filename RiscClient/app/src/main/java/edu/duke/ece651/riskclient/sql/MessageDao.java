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

    @Query("SELECT * FROM message WHERE roomID=:roomID")
    List<Message> getMessageByRoom(int roomID);

    @Query("SELECT * FROM message WHERE date BETWEEN :from AND :to")
    List<Message> findUsersBornBetweenDates(Date from, Date to);

    @Insert
    void insert(Message message);
}
