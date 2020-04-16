package edu.duke.ece651.riskclient.sql;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import edu.duke.ece651.riskclient.objects.Message;

@Database(entities = {Message.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract MessageDao messageDao();
}
