package edu.duke.ece651.risk.server;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import edu.duke.ece651.risk.shared.player.PlayerV2;
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
        m.save(room);

        final Morphia morphia = new Morphia();

        MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));
        final Datastore datastore = morphia.createDatastore(mongoClient, MONGO_DB_NAME);

        final Query<Room> query = datastore.createQuery(Room.class);
        final List<Room> rooms = query.asList();

        rooms.get(0);
        System.out.println(rooms.get(0).roomID);

    }

}