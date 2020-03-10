package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    @Test
    void getTerritory() {
        String t1 = "The Storm Kingdom";
        String t2 = "Kingdom of the Reach";
        String t3 = "Kingdom of the Rock";
        String t4 = "Kingdom of Mountain and Vale";
        String t6 = "Kingdom of the North";
        String t7 = "Principality of Dorne";

        MapDataBase mapDataBase = new MapDataBase();
        WorldMap worldMap = mapDataBase.getMap("A clash of Kings");
        Territory territory1 = worldMap.getTerritory(t1);
        Territory territory2 = worldMap.getTerritory(t2);
        Territory territory3 = worldMap.getTerritory(t3);
        Territory territory4 = worldMap.getTerritory(t4);
        Territory territory6 = worldMap.getTerritory(t6);
        Territory territory7 = worldMap.getTerritory(t7);

        territory1.status.getName().equals("The Storm Kingdom");
        Set<Territory> neigh = territory1.neigh;
        assert (3==neigh.size());
        assert (neigh.contains(territory2));
        assert (neigh.contains(territory3));
        assert (neigh.contains(territory7));

        assert (worldMap.hasFreeTerritory("The Storm Kingdom"));
        territory1.setOwner(1);
        assert (!worldMap.hasFreeTerritory("The Storm Kingdom"));

        assert (!worldMap.hasFreeTerritory("Kingdom"));

    }


}