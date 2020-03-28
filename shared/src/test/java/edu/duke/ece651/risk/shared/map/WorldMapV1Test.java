package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class WorldMapV1Test {
    @Test
    void testWorldMapV1Constructor(){
        //test constructor
        Map<String, Set<String>> map = new HashMap<>() {{
            put("a", new HashSet<String>(Arrays.asList("b","c")));
            put("b", new HashSet<String>(Arrays.asList("a","c")));
            put("c", new HashSet<String>(Arrays.asList("a","b")));
        }};
        Map<Set<String>,Boolean> groups = new HashMap<>(){{
            put(new HashSet<>(Arrays.asList("a")),false);
            put(new HashSet<>(Arrays.asList("b")),false);
            put(new HashSet<>(Arrays.asList("c")),false);
        }};

        List<String> colorList = new ArrayList<>(Arrays.asList("red","blue"));
        assertThrows(AssertionError.class,()->{new WorldMapV1<>(map,colorList,groups);});
        List<String> colorList2 = new ArrayList<>(Arrays.asList("red","blue","pink","yellow"));
        assertThrows(AssertionError.class,()->{new WorldMapV1<>(map,colorList2,groups);});
        List<String> colorList3 = new ArrayList<>(Arrays.asList("red","blue","pink"));
        assertDoesNotThrow(()->{new WorldMapV1<>(map,colorList3,groups);});


    }

}