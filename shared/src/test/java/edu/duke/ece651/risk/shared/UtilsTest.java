package edu.duke.ece651.risk.shared;

import static org.junit.jupiter.api.Assertions.*;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

public class UtilsTest {
    private static String name1 = "a clash of kings";
    private static String t1 = "the storm kingdom";
    private static String t2 = "kingdom of the reach";
    private static String t3 = "kingdom of the rock";
    private static String t4 = "kingdom of mountain and vale";
    private static String t5 = "principality of dorne";
    private static String t6 = "kingdom of the north";


    private static String dir = "../config_file/MapDB_config/a clash of kings/";

    @Test
    public void testReadFileToString() throws IOException {
        JSONObject jsonObject = new JSONObject(Utils.readFileToString("../config_file/client_config.txt"));
        assertEquals("localhost", jsonObject.getString("host"));
        assertEquals(12345, jsonObject.getInt("port"));
        assertThrows(IOException.class,()->{Utils.readFileToString("fake.txt");});

    }
    @Test
    public void testReadNeighConfig() throws  IOException{

        Map<String, Set<String>> atlas1 = new HashMap<>();
        Set<String> s1 = new HashSet<>(){{
            add(t3);
            add(t2);
            add(t5);
            add(t4);
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
        Set<String> s5 = new HashSet<>(){{
            add(t2);
            add(t1);
        }};

        atlas1.put(t5,s5);
        Set<String> s6 = new HashSet<>(){{
            add(t3);
            add(t4);
        }};
        atlas1.put(t6,s6);
        Map<String,Set<String>> testAtlas = Utils.readNeighConfig(dir+"neigh.txt");
        assertEquals(atlas1,testAtlas);

        assertThrows(IOException.class,()->{Utils.readNeighConfig(dir+"fake.txt");});
    }


    @Test
    void readColorConfig() throws IOException {
        List<String> colorList = new ArrayList<>(Arrays.asList("red","blue"));
        List<String> strings = Utils.readColorConfig(dir+"color.txt");
        assertEquals(colorList,strings);
        assertThrows(IOException.class,()->{Utils.readColorConfig(dir+"fake.txt");});
    }


    @Test
    void readGroupConfig() throws IOException {
        Map<Set<String>,Boolean> groups = new HashMap<>();
        Set<String> group1 = new HashSet<>(){{
            add(t6);
            add(t4);
            add(t1);
        }};
        Set<String> group2 = new HashSet<>(){{
            add(t2);
            add(t3);
            add(t5);
        }};
        groups.put(group1,false);
        groups.put(group2,false);
        Map<Set<String>, Boolean> setBooleanMap = Utils.readGroupConfig(dir+"group.txt");
        assertEquals(setBooleanMap,groups);
        assertThrows(IOException.class,()->{Utils.readGroupConfig(dir+"fake.txt");});

    }

    @Test
    void readSizeConfig() throws IOException {
        Map<String, Integer> stringIntegerMap = Utils.readSizeConfig(dir + "size.txt");
        Map<String,Integer> map = new HashMap<>(){{
            put("the storm kingdom",2);
            put("kingdom of the reach",3);
            put("kingdom of the rock",3);
            put("kingdom of mountain and vale",2);
            put("principality of dorne",4);
            put("kingdom of the north",5);
        }};
        assertEquals(map,stringIntegerMap);
    }

    @Test
    void testClone() throws IOException, ClassNotFoundException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");
        storm.setOwner(2);
        storm.addBasicUnits(5);
        Territory north = worldMap.getTerritory("kingdom of the north");
        north.setOwner(3);


        WorldMap<String> cloneRes = (WorldMap)Utils.clone(worldMap);
        Territory stormClone = cloneRes.getTerritory("the storm kingdom");
        Territory northClone = cloneRes.getTerritory("kingdom of the north");
        Territory rockClone = cloneRes.getTerritory("kingdom of the rock");
        assertEquals(2,stormClone.getOwner());
        assertEquals(5,stormClone.getUnitsNum());
        assertEquals(3,northClone.getOwner());
        assertEquals(0,northClone.getUnitsNum());
        assertEquals(0,rockClone.getOwner());

        assertFalse(stormClone==storm);
        assertFalse(cloneRes==worldMap);
    }
}
