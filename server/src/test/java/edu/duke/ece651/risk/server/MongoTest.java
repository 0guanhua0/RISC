package edu.duke.ece651.risk.server;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.model.Filters;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.map.WorldMapImpl;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.duke.ece651.risk.shared.Constant.*;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;

class MongoTest {
    //clean mongo db

    @AfterEach
    public void cleanMongo() {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));
        mongoClient.getDatabase(MONGO_DB_NAME).getCollection(MONGO_COLLECTION).drop();
        mongoClient.getDatabase(MONGO_DB_NAME).getCollection(MONGO_USERLIST).drop();
    }


    @Test
    public void save() throws IOException, ClassNotFoundException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();


        String s11 = "{}";
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");
        Player<String> player = new PlayerV2<>(setupMockInput(new ArrayList<>(Arrays.asList(s11))), stream);
        Room room = new Room(0, player, mapDataBase);
        room.roomName = "0";
        room.map = worldMap;
        player.addTerritory(storm);

        room.gameInfo.idToName.clear();
        room.gameInfo.idToName.put(1, "1");

        Mongo m = new Mongo();
        Datastore datastore = m.morCon();
        datastore.save(room);

        final Query<Room> query = datastore.createQuery(Room.class);
        final List<Room> rooms = query.asList();

        assertEquals(0, rooms.get(0).roomID);

        MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));
        mongoClient.getDatabase(MONGO_DB_NAME).getCollection(MONGO_COLLECTION).drop();

    }



}