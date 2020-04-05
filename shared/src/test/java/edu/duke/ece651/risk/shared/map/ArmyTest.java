package edu.duke.ece651.risk.shared.map;

import org.apache.commons.math3.fitting.leastsquares.EvaluationRmsChecker;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ArmyTest {
    Map<Integer,Integer> map = new HashMap<Integer,Integer>(){{
        put(0,5);
        put(1,2);
    }};
    Army army1 = new Army(1,"storm",map);
    Army army2 = new Army(1,"north",3);

    @Test
    void getSrc() {
        assertEquals("storm",army1.getSrc());
        assertEquals("north",army2.getSrc());
    }

    @Test
    void getUnitNums() {
        assertEquals(5,army1.getUnitNums(0));
        assertEquals(3,army2.getUnitNums(0));
    }

    @Test
    void getTroops() {
        assertEquals(map,army1.getTroops());
    }
}