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
        mapHub = new HashMap<>();
        //the first map--A Clash of Kings
        //TODO read from input files rather than hardcoding here
        Map<String,Set<String>> atlas1 = new HashMap<>();
        String name1 = "a clash of kings";
        String t1 = "the storm kingdom";
        String t2 = "kingdom of the reach";
        String t3 = "kingdom of the rock";
        String t4 = "kingdom of mountain and vale";
        String t5 = "kingdom of the isles and the rivers";
        String t6 = "kingdom of the north";
        String t7 = "principality of dorne";
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
    public boolean containsMap(String inputName){
        String mapName = inputName.toLowerCase();
        return mapHub.containsKey(mapName);
    }
    public WorldMap getMap(String inputName){
        if (!containsMap(inputName)){
            throw new IllegalArgumentException("Input map name doesn't exist!");
        }
        String mapName = inputName.toLowerCase();
        return mapHub.get(mapName);
    }
}
