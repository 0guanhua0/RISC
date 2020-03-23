package edu.duke.ece651.risk.shared;

import static org.junit.jupiter.api.Assertions.*;

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
    @Test
    public void testReadFileToString() throws IOException {
        JSONObject jsonObject = new JSONObject(Utils.readFileToString("../config_file/client_config.txt"));
        assertEquals("localhost", jsonObject.getString("host"));
        assertEquals(12345, jsonObject.getInt("port"));
    }
    @Test
    public void testReadNeighConfig() throws  IOException{
        Map<String, Set<String>> atlas1 = new HashMap<>();
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
        Map<String,Set<String>> testAtlas = Utils.readNeighConfig("../config_file/MapDB_config/a clash of kings/neigh.txt");
        assertEquals(atlas1,testAtlas);
    }


    @Test
    void readColorConfig() throws IOException {
        List<String> colorList = new ArrayList<>(Arrays.asList("red","blue"));
        List<String> strings = Utils.readColorConfig("../config_file/MapDB_config/a clash of kings/color.txt");
        assertEquals(colorList,strings);
    }


    @Test
    void readGroupConfig() {
        
    }
}
