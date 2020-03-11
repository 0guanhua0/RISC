package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        MapDataBase mapDataBase = new MapDataBase();
        WorldMap worldMap = mapDataBase.getMap("A clash of Kings");
        //test getTerritory
        Territory territory1 = worldMap.getTerritory(t1);
        Territory territory2 = worldMap.getTerritory(t2);
        Territory territory3 = worldMap.getTerritory(t3);
        Territory territory4 = worldMap.getTerritory(t4);
        Territory territory6 = worldMap.getTerritory(t6);
        Territory territory7 = worldMap.getTerritory(t7);

        territory1.status.getName().equals("The Storm Kingdom");
        Set<Territory> neigh = territory1.getNeigh();
        assert (3==neigh.size());
        assert (neigh.contains(territory2));
        assert (neigh.contains(territory3));
        assert (neigh.contains(territory7));

        try {
            Territory storm = worldMap.getTerritory("The Storm Kingdo");
            assertTrue(false);
        }catch (IllegalArgumentException e){}


        //test hasTerritory()
        assertTrue(worldMap.hasTerritory("The Storm Kingdom"));
        assertTrue(worldMap.hasTerritory("The stoRm Kingdom"));
        assertFalse(worldMap.hasTerritory("The stoRm Kingdo"));


        //test hasFreeTerritory()
        assert (worldMap.hasFreeTerritory("The Storm Kingdom"));
        territory1.setOwner(1);
        assert (!worldMap.hasFreeTerritory("The Storm Kingdom"));
        assert (!worldMap.hasFreeTerritory("Kingdom"));


    }


}