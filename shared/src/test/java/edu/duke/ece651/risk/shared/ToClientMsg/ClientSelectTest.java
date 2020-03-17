package edu.duke.ece651.risk.shared.ToClientMsg;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import org.apache.commons.math3.fitting.leastsquares.EvaluationRmsChecker;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ClientSelectTest {
    @Test
    void testConstructor(){
        ClientSelect cs = new ClientSelect(3,9, new HashSet<String>() {{
            add("test");
        }});
        assertEquals(cs.terrNum,3);
        assertEquals(cs.unitsNum,9);
        assertEquals(cs.occupied.size(),1);
        assertTrue(cs.occupied.contains("test"));
    }
}