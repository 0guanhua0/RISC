package edu.duke.ece651.risk.shared.ToClientMsg;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.network.Client;
import org.apache.commons.math3.fitting.leastsquares.EvaluationRmsChecker;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ClientSelectTest {

    @Test
    void testConstructor(){
        WorldMap<String> map = new MapDataBase<String>().getMap("a clash of kings");
        ClientSelect clientSelect = new ClientSelect(10, 2, map.getName());

        assertEquals(10, clientSelect.getUnitsTotal());
        assertEquals(map.getTerriNum(), clientSelect.getMap().getTerriNum());
        assertEquals(2, clientSelect.getGroups().size());
    }
}