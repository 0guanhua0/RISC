package edu.duke.ece651.risk.shared.ToClientMsg;

import static org.junit.jupiter.api.Assertions.*;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RoundInfoTest {

    static WorldMap<String> map;



    static Map<Integer, String> players = new HashMap<>();
    static RoundInfo roundInfo;

    @BeforeAll
    static void beforeAll() throws IOException, ClassNotFoundException {
        map = new MapDataBase<String>().getMap("a clash of kings");
        String t1 = "the storm kingdom";
        String t2 = "kingdom of the reach";
        String t3 = "kingdom of the rock";
        String t4 = "kingdom of mountain and vale";
        String t5 = "kingdom of the north";
        String t6 = "principality of dorne";
        map.getTerritory(t1).addBasicUnits(1);
        map.getTerritory(t2).addBasicUnits(2);
        map.getTerritory(t3).addBasicUnits(3);
        map.getTerritory(t4).addBasicUnits(4);
        map.getTerritory(t5).addBasicUnits(5);
        map.getTerritory(t6).addBasicUnits(6);

        map.getTerritory(t1).setOwner(1);
        map.getTerritory(t2).setOwner(1);
        map.getTerritory(t3).setOwner(2);
        map.getTerritory(t4).setOwner(2);
        map.getTerritory(t5).setOwner(3);
        map.getTerritory(t6).setOwner(3);

        players.put(1, "Green");
        players.put(2, "Blue");
        players.put(3, "Red");

        roundInfo = new RoundInfo(1, map, players, null);
    }

    @Test
    public void testGetMap() { 
        assertEquals(map.getAtlas().size(), roundInfo.getMap().getAtlas().size());
    }
    
    @Test
    public void testGetIdToColor() {
        assertEquals(1, roundInfo.getRoundNum());
        assertEquals(3, roundInfo.getIdToName().size());
        assertEquals("Green", roundInfo.getIdToName().get(1));
        assertEquals("Blue", roundInfo.getIdToName().get(2));
        assertEquals("Red", roundInfo.getIdToName().get(3));
    }

} 
