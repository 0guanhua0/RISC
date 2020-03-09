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
        Map<String, Set<String>> atlas1 = new HashMap<>();
        String t1 = "The Storm Kingdom";
        String t2 = "Kingdom of the Reach";
        String t3 = "Kingdom of the Rock";
        String t4 = "Kingdom of Mountain and Vale";
        String t5 = "Kingdom of the Isles and the Rivers";
        String t6 = "Kingdom of the North";
        String t7 = "Principality of Dorne";
        Set<String> s1 = new HashSet<>(){{
            add(t3);
            add(t2);
            add(t7);
            add(t5);
        }};
        atlas1.put(t1,s1);
        Set<String> s2 = new HashSet<>(){{
            add(t3);
            add(t1);
            add(t7);
            add(t5);
        }};
        atlas1.put(t2,s2);
        Set<String> s3 = new HashSet<>(){{
            add(t6);
            add(t4);
            add(t1);
            add(t2);
            add(t5);
        }};
        atlas1.put(t3,s3);
        Set<String> s4 = new HashSet<>(){{
            add(t6);
            add(t3);
            add(t1);
            add(t5);
        }};
        atlas1.put(t4,s4);
        Set<String> s5 = new HashSet<>(){{
            add(t1);
            add(t2);
            add(t3);
            add(t4);
            add(t6);
            add(t7);
        }};
        atlas1.put(t5,s5);
        Set<String> s6 = new HashSet<>(){{
            add(t3);
            add(t4);
            add(t5);
        }};
        atlas1.put(t6,s6);
        Set<String> s7 = new HashSet<>(){{
            add(t2);
            add(t1);
            add(t5);
        }};
        WorldMap worldMap = new WorldMap(atlas1);
        Territory territory1 = worldMap.getTerritory(t1);
        Territory territory2 = worldMap.getTerritory(t2);
        Territory territory3 = worldMap.getTerritory(t3);
        Territory territory4 = worldMap.getTerritory(t4);
        Territory territory5 = worldMap.getTerritory(t5);
        Territory territory6 = worldMap.getTerritory(t6);
        Territory territory7 = worldMap.getTerritory(t7);

        territory1.status.getName().equals("The Storm Kingdom");
        Set<Territory> neigh = territory1.neigh;
        assert (4==neigh.size());
        assert (neigh.contains(territory2));
        assert (neigh.contains(territory3));
        assert (neigh.contains(territory5));
        assert (neigh.contains(territory7));

        assert (worldMap.hasFreeTerritory("The Storm Kingdom"));
        territory1.setOwner(1);
        assert (!worldMap.hasFreeTerritory("The Storm Kingdom"));

        assert (!worldMap.hasFreeTerritory("Kingdom"));

    }


}