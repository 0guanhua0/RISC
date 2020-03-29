package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

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
        Map<String, Set<String>> map = new HashMap<>() {{
            put("a", new HashSet<>());
            put("b", new HashSet<>());
            put("c", new HashSet<>());
        }};
        Map<Set<String>,Boolean> groups = new HashMap<>(){{
            put(new HashSet<>(Arrays.asList("a")),false);
            put(new HashSet<>(Arrays.asList("b")),false);
            put(new HashSet<>(Arrays.asList("c")),false);
        }};
        Map<String,Integer> sizes = new HashMap<>(){{
           put("a",2);
           put("b",2);
           put("c",2);
        }};

        Map<String,Integer> food = new HashMap<>(){{
            put("a",3);
            put("b",3);
            put("c",3);
        }};

        Map<String,Integer> tech = new HashMap<>(){{
            put("a",4);
            put("b",4);
            put("c",4);
        }};
        List<String> colorList = new ArrayList<>(Arrays.asList("red","blue"));
        assertThrows(AssertionError.class,()->{new WorldMapV2<>(map,colorList,groups,sizes,food,tech);});

        List<String> colorList2 = new ArrayList<>(Arrays.asList("red","blue","pink","yellow"));
        assertThrows(AssertionError.class,()->{new WorldMapV2<>(map,colorList2,groups,sizes,food,tech);});


        MapDataBase<String> mapDataBase = new MapDataBase<>();
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
        assert (4==neigh.size());
        assert (neigh.contains(territory2));
        assert (neigh.contains(territory3));
        assert (neigh.contains(territory7));

        assertThrows(IllegalArgumentException.class, ()->worldMap.getTerritory("The Storm Kingdo"));

        //test hasTerritory()
        assertTrue(worldMap.hasTerritory("The Storm Kingdom"));
        assertTrue(worldMap.hasTerritory("The stoRm Kingdom"));
        assertFalse(worldMap.hasTerritory("The stoRm Kingdo"));


        //test hasFreeTerritory()
        assert (worldMap.hasFreeTerritory("The Storm Kingdom"));
        territory1.setOwner(1);
        assert (!worldMap.hasFreeTerritory("The Storm Kingdom"));
        assert (!worldMap.hasFreeTerritory("Kingdom"));

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
        Map<String,Territory> map = new HashMap<>();
        map.put(storm,territory1);
        map.put(reach,territory2);
        map.put(rock,territory3);
        map.put(vale,territory4);
        map.put(north,territory6);
        map.put(dorne,territory7);
    }

    @Test
    void testGroup() throws IOException {
        MapDataBase<Serializable> mapDataBase = new MapDataBase<>();
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
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> kingMap = mapDataBase.getMap("a clash of kings");
        assertThrows(IllegalArgumentException.class,()->{kingMap.getMinCtrlDist("a", storm);});
        assertThrows(IllegalArgumentException.class,()->{kingMap.getMinCtrlDist(storm,"a");});
        assertEquals(kingMap.getMinCtrlDist(storm, storm),0);
        assertEquals(kingMap.getMinCtrlDist(north,rock),5);
        assertEquals(kingMap.getMinCtrlDist(rock,north),3);
        assertEquals(kingMap.getMinCtrlDist(reach,vale),5);
        assertEquals(kingMap.getMinCtrlDist(dorne,rock),6);

        WorldMap<String> ringMap = mapDataBase.getMap("ring");
        assertEquals(ringMap.getMinCtrlDist("a","a"),0);
        assertEquals(ringMap.getMinCtrlDist("a","c"),4);
        assertEquals(ringMap.getMinCtrlDist("a","e"),8);

        Map<String, Set<String>> adjaList = new HashMap<>(){{
            put("a",new HashSet<>());
            put("b",new HashSet<>());
        }};
        List<String> colorList = new ArrayList<>(){{
            add("red");
        }};
        Set<String> set = new HashSet<>(){{
            add("a");
            add("b");
        }};
        Map<Set<String>, Boolean> groups = new HashMap<>(){{
            put(set,false);
        }};
        Map<String,Integer> sizes = new HashMap<>(){{
            put("a",0);
            put("b",0);
        }};
        Map<String,Integer> food = new HashMap<>(){{
            put("a",0);
            put("b",0);
        }};  Map<String,Integer> tech = new HashMap<>(){{
            put("a",0);
            put("b",0);
        }};
        WorldMap<String> test = new WorldMapV2(adjaList,colorList,groups,sizes,food,tech);
        assertThrows(IllegalArgumentException.class,()->{test.getMinCtrlDist("a","b");});
    }
}