package edu.duke.ece651.risk.shared.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @program: risk-map
 * @description: This is database which includes all maps we create
 * @author: Chengda Wu(cw402)
 * @create: 2020-03-09 10:20
 **/
public class MapDataBase {
    Map<String, WorldMap> mapHub;
    public MapDataBase(){
        //the first map--A Clash of Kings
        //TODO read from input files rather than hardcoding here
        Map<String,Set<String>> atlas1 = new HashMap<>();
        String name1 = "A Clash of Kings";
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
        WorldMap worldMap1 = new WorldMap(atlas1);
        mapHub.put(name1,worldMap1);
    }
}
