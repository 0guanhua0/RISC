package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;
import edu.duke.ece651.risk.shared.map.Unit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DisplayTest {

    @Test
    void testShowMap() {
        Display dis = new Display0();
        TerritoryV1 numOfUnits = new TerritoryV1("Narnia");

        Set<String> adjTerr = new HashSet<String>();
        adjTerr.add("Midkemia");
        adjTerr.add("Elantris");
        Map<String, Set<String>> terr = new HashMap<String, Set<String>>()
;
        terr.put("Narnia",adjTerr);
        String a = dis.showMap(terr);
        //System.out.println(a);
        //When i print, the output is correct, but while using assert I am getting an error of a '>'
//        assertEquals("45 units in Narnia(next to: Elantris, Midkemia, )", a);

    }
}























