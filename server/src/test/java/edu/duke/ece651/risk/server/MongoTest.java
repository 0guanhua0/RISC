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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.duke.ece651.risk.shared.Constant.MAP_NAME;
import static edu.duke.ece651.risk.shared.Constant.ROOM_NAME;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;

class MongoTest {
    @Test
    public void init() throws IOException, ClassNotFoundException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://risc:risc@vcm-12835.vm.duke.edu/risc"));
        DB database = mongoClient.getDB("risc");
        DBCollection collection = database.getCollection("movie");
        //System.out.println(collection.findOne());

        final Morphia morphia = new Morphia();
        morphia.mapPackage("edu.duke.ece651.risk");

        final Datastore datastore = morphia.createDatastore(mongoClient, "risc");
        datastore.ensureIndexes();

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


        datastore.save(room);

    }

}