package edu.duke.ece651.risk.shared.ToServerMsg;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ServerSelectTest {

    @Test
    void isValid() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Map<String,Integer> chosen = new HashMap<>();
        //here we have 2 territories and 6 units
        chosen.put("kingdom of the reach",3);
        chosen.put("kingdom of the rock",3);
        ServerSelect serverSelect = new ServerSelect(chosen);
        assertTrue(serverSelect.isValid(worldMap,6,2));
        assertFalse(serverSelect.isValid(worldMap,5,2));
        assertFalse(serverSelect.isValid(worldMap,7,2));
        assertFalse(serverSelect.isValid(worldMap,6,3));
        assertFalse(serverSelect.isValid(worldMap,6,1));

        chosen.put("kingdom of the reach",3);
        chosen.put("kingdom of the rock",-1);
        assertFalse(serverSelect.isValid(worldMap,2,2));

        Territory territory = worldMap.getTerritory("kingdom of the reach");
        territory.setOwner(1);
        assertFalse(serverSelect.isValid(worldMap,6,2));

    }

    @Test
    void perform() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Map<String,Integer> chosen = new HashMap<>();
        chosen.put("kingdom of the reach",3);
        chosen.put("kingdom of the rock",3);
        ServerSelect serverSelect = new ServerSelect(chosen);
        PlayerV1<String> playerV1 = new PlayerV1<>("Blue",1);
        assertThrows(IllegalArgumentException.class,()->{serverSelect.perform(worldMap,5,2,playerV1);});
        serverSelect.perform(worldMap,6,2,playerV1);
        assertEquals(worldMap.getTerritory("kingdom of the reach").getOwner(),1);
        assertEquals(worldMap.getTerritory("kingdom of the rock").getOwner(),1);
    }

    @Test
    void getAllName() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Map<String,Integer> chosen = new HashMap<>();
        //here we have 2 territories and 6 units
        chosen.put("kingdom of the reach",3);
        chosen.put("kingdom of the rock",3);
        ServerSelect serverSelect = new ServerSelect(chosen);
        assertTrue(serverSelect.getAllName().contains("kingdom of the reach"));
        assertTrue(serverSelect.getAllName().contains("kingdom of the rock"));
        assertEquals(serverSelect.getAllName().size(),2);

    }

    @Test
    void getUnitsNum() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Map<String,Integer> chosen = new HashMap<>();
        //here we have 2 territories and 6 units
        chosen.put("kingdom of the reach",3);
        chosen.put("kingdom of the rock",3);
        ServerSelect serverSelect = new ServerSelect(chosen);
        assertEquals(3,serverSelect.getUnitsNum("kingdom of the reach"));
        assertThrows(IllegalArgumentException.class,()->{serverSelect.getUnitsNum("not");});
    }
}