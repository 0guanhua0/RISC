package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {
    private static final String storm = "the storm kingdom";
    private static final String reach = "kingdom of the reach";
    private static final String rock = "kingdom of the rock";
    private static final String vale = "kingdom of mountain and vale";
    private static final String north = "kingdom of the north";
    private static final String dorne = "principality of dorne";
    @Test
    void testWorldMap() throws IOException {
        //test constructor
        Map<String, Set<String>> map = new HashMap<String, Set<String>>() {{
            put("a", new HashSet<String>());
            put("b", new HashSet<String>());
            put("c", new HashSet<String>());
        }};
        Map<Set<String>,Boolean> groups = new HashMap<Set<String>, Boolean>(){{
            put(new HashSet<String>(Arrays.asList("a")),false);
            put(new HashSet<String>(Arrays.asList("b")),false);
            put(new HashSet<String>(Arrays.asList("c")),false);
        }};
        Map<String,Integer> sizes = new HashMap<String, Integer>(){{
           put("a",2);
           put("b",2);
           put("c",2);
        }};

        Map<String,Integer> food = new HashMap<String, Integer>(){{
            put("a",3);
            put("b",3);
            put("c",3);
        }};

        Map<String,Integer> tech = new HashMap<String, Integer>(){{
            put("a",4);
            put("b",4);
            put("c",4);
        }};
        List<String> colorList = new ArrayList<String>(Arrays.asList("red","blue"));
        assertThrows(AssertionError.class,()->{new WorldMapV2<String>(map,colorList,groups,sizes,food,tech);});

        List<String> colorList2 = new ArrayList<String>(Arrays.asList("red","blue","pink","yellow"));
        assertThrows(AssertionError.class,()->{new WorldMapV2<String>(map,colorList2,groups,sizes,food,tech);});


        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("A clash of Kings");
        //test getTerritoryName
        Territory territory1 = worldMap.getTerritory(storm);
        Territory territory2 = worldMap.getTerritory(reach);
        Territory territory3 = worldMap.getTerritory(rock);
        Territory territory4 = worldMap.getTerritory(vale);
        Territory territory6 = worldMap.getTerritory(north);
        Territory territory7 = worldMap.getTerritory(dorne);

        assertEquals(6, worldMap.getTerriNum());
        assertEquals(3, worldMap.getTerrPerPlayer());

        assertEquals("the storm kingdom", territory1.status.getName());
        Set<Territory> neigh = territory1.getNeigh();
        assertEquals(4, neigh.size());
        assertTrue (neigh.contains(territory2));
        assertTrue (neigh.contains(territory3));
        assertTrue (neigh.contains(territory7));

        assertThrows(IllegalArgumentException.class, ()->worldMap.getTerritory("The Storm Kingdo"));

        //test hasTerritory()
        assertTrue(worldMap.hasTerritory("The Storm Kingdom"));
        assertTrue(worldMap.hasTerritory("The stoRm Kingdom"));
        assertFalse(worldMap.hasTerritory("The stoRm Kingdo"));


        //test hasFreeTerritory()
        assertTrue (worldMap.hasFreeTerritory("The Storm Kingdom"));
        territory1.setOwner(1);
        assertFalse(worldMap.hasFreeTerritory("The Storm Kingdom"));
        assertFalse(worldMap.hasFreeTerritory("Kingdom"));

        assertEquals(2, worldMap.getPlayerNums());
    }


    @Test
    void setPlayerColor() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("A clash of Kings");
        Territory territory1 = worldMap.getTerritory(storm);
        Territory territory2 = worldMap.getTerritory(reach);
        Territory territory3 = worldMap.getTerritory(rock);
        Territory territory4 = worldMap.getTerritory(vale);
        Territory territory6 = worldMap.getTerritory(north);
        Territory territory7 = worldMap.getTerritory(dorne);
        List<String> playerColor = worldMap.getColorList();
        Map<String,Territory> map = new HashMap<String, Territory>();
        map.put(storm,territory1);
        map.put(reach,territory2);
        map.put(rock,territory3);
        map.put(vale,territory4);
        map.put(north,territory6);
        map.put(dorne,territory7);
    }

    @Test
    void testGroup() throws IOException {
        MapDataBase<Serializable> mapDataBase = new MapDataBase<Serializable>();
        WorldMap<Serializable> worldMap = mapDataBase.getMap("a clash of kings");
        assertTrue(worldMap.hasFreeGroup(new HashSet<String>(Arrays.asList(
                "kingdom of mountain and vale",
                "kingdom of the north",
                "the storm kingdom"
        ))));
        assertFalse(worldMap.hasFreeGroup(new HashSet<String>(Arrays.asList(
                "kingdom of mountain and vale",
                "kingdom of the north",
                "he storm kingdom"
        ))));
        worldMap.useGroup(new HashSet<String>(Arrays.asList(
                "kingdom of mountain and vale",
                "kingdom of the north",
                "the storm kingdom"
        )));
        assertFalse(worldMap.hasFreeGroup(new HashSet<String>(Arrays.asList(
                "kingdom of mountain and vale",
                "kingdom of the north",
                "the storm kingdom"
        ))));
    }


    @Test
    void testGetDist() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> kingMap = mapDataBase.getMap("a clash of kings");
        //in the begining, the owner id of all maps is 0, which means they are all free now
        assertEquals(Integer.MAX_VALUE,kingMap.getMinCtrlDist("a", storm));
        assertEquals(Integer.MAX_VALUE,kingMap.getMinCtrlDist(storm,"a"));
        assertEquals(kingMap.getMinCtrlDist(storm, storm),0);
        assertEquals(kingMap.getMinCtrlDist(north,rock),5);
        assertEquals(kingMap.getMinCtrlDist(rock,north),3);
        assertEquals(kingMap.getMinCtrlDist(reach,vale),5);
        assertEquals(kingMap.getMinCtrlDist(dorne,rock),6);

        //test when territories are owned by different user
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player2.setId(2);

        player1.addTerritory(kingMap.getTerritory(storm));
        player1.addTerritory(kingMap.getTerritory(north));

        assertEquals(Integer.MAX_VALUE,kingMap.getMinCtrlDist(storm,north));
        player1.addTerritory(kingMap.getTerritory(rock));
        assertEquals(5,kingMap.getMinCtrlDist(storm,north));
        player1.addTerritory(kingMap.getTerritory(vale));
        assertEquals(4,kingMap.getMinCtrlDist(storm,north));
        player1.loseTerritory(kingMap.getTerritory(vale));
        player2.addTerritory(kingMap.getTerritory(vale));
        assertEquals(5,kingMap.getMinCtrlDist(storm,north));
        assertEquals(Integer.MAX_VALUE,kingMap.getMinCtrlDist(vale,north));

        WorldMap<String> ringMap = mapDataBase.getMap("ring");
        assertEquals(ringMap.getMinCtrlDist("a","a"),0);
        assertEquals(ringMap.getMinCtrlDist("a","c"),4);
        assertEquals(ringMap.getMinCtrlDist("a","e"),8);


    }
}