package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {
    private static final String t1 = "The Storm Kingdom";
    private static final String t2 = "Kingdom of the Reach";
    private static final String t3 = "Kingdom of the Rock";
    private static final String t4 = "Kingdom of Mountain and Vale";
    private static final String t6 = "Kingdom of the North";
    private static final String t7 = "Principality of Dorne";
    @Test
    void testWorldMap() {
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

        List<String> colorList = new ArrayList<>(Arrays.asList("red","blue"));
        assertThrows(IllegalArgumentException.class,()->{new WorldMap<>(map,colorList,groups);});

        List<String> colorList2 = new ArrayList<>(Arrays.asList("red","blue","pink","yellow"));
        assertThrows(IllegalArgumentException.class,()->{new WorldMap<>(map,colorList2,groups);});





        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("A clash of Kings");
        //test getTerritoryName
        Territory territory1 = worldMap.getTerritory(t1);
        Territory territory2 = worldMap.getTerritory(t2);
        Territory territory3 = worldMap.getTerritory(t3);
        Territory territory4 = worldMap.getTerritory(t4);
        Territory territory6 = worldMap.getTerritory(t6);
        Territory territory7 = worldMap.getTerritory(t7);

        assertEquals(6, worldMap.getTerriNum());

        assertEquals("the storm kingdom", territory1.status.getName());
        Set<Territory> neigh = territory1.getNeigh();
        assert (3==neigh.size());
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
    void setPlayerColor() {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("A clash of Kings");
        Territory territory1 = worldMap.getTerritory(t1);
        Territory territory2 = worldMap.getTerritory(t2);
        Territory territory3 = worldMap.getTerritory(t3);
        Territory territory4 = worldMap.getTerritory(t4);
        Territory territory6 = worldMap.getTerritory(t6);
        Territory territory7 = worldMap.getTerritory(t7);
        List<String> playerColor = worldMap.getColorList();
        Map<String,Territory> map = new HashMap<>();
        map.put(t1,territory1);
        map.put(t2,territory2);
        map.put(t3,territory3);
        map.put(t4,territory4);
        map.put(t6,territory6);
        map.put(t7,territory7);

        WorldMap<String> myMap = new WorldMap<>();
        myMap.setAtlas(map);
        myMap.setColorList(playerColor);
        assertEquals(myMap.atlas.get(t1),territory1);
        assertEquals(myMap.atlas.get(t2),territory2);
        assertTrue(myMap.colorList.contains("red"));
        assertTrue(myMap.colorList.contains("blue"));

    }

    @Test
    void testGroup() {
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


}