package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;
import java.util.*;

/**
 * @program: risk-map
 * @description: This is database which includes all maps we create
 * @author: Chengda Wu(cw402)
 * @create: 2020-03-09 10:20
 **/

// TODO: read from input files rather than hardcoding here
// TODO: do we really need T here? since we hard code the map, we already know the type
public class MapDataBase<T extends Serializable> implements Serializable{
    Map<String, WorldMap<T>> mapHub;
    public MapDataBase(){
        mapHub = new HashMap<>();
        //the first map--A Clash of Kings
        //the visualization of the worldmap:
        // https://www.google.com/imgres?imgurl=https%3A%2F%2F4.bp.blogspot.com%2F-KeZBbmX_Lbg%2FWmXhqT8wdmI%2FAAAAAAAABCQ%2FUR2pXAxwcss25bhcvUTiGojqCOxrgtkWACLcBGAs%2Fs1600%2FSix-Kingdoms-Map-Cropped2.png&imgrefurl=http%3A%2F%2Fsevenkingdomssevenhells.blogspot.com%2F2018%2F01%2Fthe-six-kingdoms.html&tbnid=f3Q1CgrIMiNzlM&vet=12ahUKEwiRufeZzo3oAhXH41MKHePwD_MQMygFegUIARDwAQ..i&docid=O_sfDTzxZzV-MM&w=473&h=1059&q=%20seven%20kingdom%20atlas&client=safari&ved=2ahUKEwiRufeZzo3oAhXH41MKHePwD_MQMygFegUIARDwAQ
        Map<String,Set<String>> atlas1 = new HashMap<>();
        String name1 = "a clash of kings";
        String t1 = "the storm kingdom";
        String t2 = "kingdom of the reach";
        String t3 = "kingdom of the rock";
        String t4 = "kingdom of mountain and vale";
        String t5 = "principality of dorne";
        String t6 = "kingdom of the north";
        Set<String> s1 = new HashSet<>(){{
            add(t3);
            add(t2);
            add(t5);
        }};
        atlas1.put(t1,s1);
        Set<String> s2 = new HashSet<>(){{
            add(t3);
            add(t1);
            add(t5);
        }};
        atlas1.put(t2,s2);
        Set<String> s3 = new HashSet<>(){{
            add(t6);
            add(t4);
            add(t1);
            add(t2);
        }};
        atlas1.put(t3,s3);
        Set<String> s4 = new HashSet<>(){{
            add(t6);
            add(t3);
            add(t1);
        }};
        atlas1.put(t4,s4);
        /*temporarily delete Iron Islands*/
        Set<String> s6 = new HashSet<>(){{
            add(t3);
            add(t4);
        }};
        atlas1.put(t6,s6);
        Set<String> s7 = new HashSet<>(){{
            add(t2);
            add(t1);
        }};

        atlas1.put(t5,s7);
        List<String> colorList = new ArrayList<>(Arrays.asList("red","blue"));

        Map<Set<String>,Boolean> groups = new HashMap<>();
        Set<String> group1 = new HashSet<>(){{
            add(t6);
            add(t4);
            add(t3);
        }};
        Set<String> group2 = new HashSet<>(){{
            add(t1);
            add(t2);
            add(t5);
        }};
        groups.put(group1,false);
        groups.put(group2,false);

        WorldMap<T> worldMap1 = new WorldMap(atlas1,colorList,groups);
        worldMap1.setName(name1);
        mapHub.put(name1,worldMap1);

        /*
        the second map
         */
        String testName1 = "t1";
        String testName2 = "t2";
        String testName3 = "t7";
        Map<String,Set<String>> atlas2 = new HashMap<>();
        atlas2.put(testName1, new HashSet<>(){{ add(testName2); }});
        atlas2.put(testName2, new HashSet<>(){{ add(testName1);add(testName3);}});
        atlas2.put(testName3, new HashSet<>(){{ add(testName2); }});

        Map<Set<String>,Boolean> groups2 = new HashMap<>();
        groups2.put(new HashSet<>(Arrays.asList(testName1)),false);
        groups2.put(new HashSet<>(Arrays.asList(testName2)),false);
        groups2.put(new HashSet<>(Arrays.asList(testName3)),false);

        WorldMap<T> worldMap2 = new WorldMap(atlas2,Arrays.asList("red","blue","black"),groups2);
        worldMap2.setName("test");
        mapHub.put("test",worldMap2);

    }
    public boolean containsMap(String inputName){
        if (null==inputName){
            return false;
        }else{
            String mapName = inputName.toLowerCase();
            return mapHub.containsKey(mapName);
        }
    }
    public WorldMap<T> getMap(String inputName){
        if (!containsMap(inputName)){
            throw new IllegalArgumentException("Input map name doesn't exist!");
        }
        String mapName = inputName.toLowerCase();
        return mapHub.get(mapName);
    }
    public Map<String, WorldMap<T>> getAllMaps(){
        return mapHub;
    }
}
