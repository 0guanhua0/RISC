package edu.duke.ece651.risk.shared.action;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttackresultTest {


    @Test
    void testAll() {
        Attackresult r = new Attackresult(0, 1, "storm", true);

        assertEquals(r.getAttackerID(), 0);
        assertEquals(1, r.getDefenderID());
        assertEquals("storm", r.getTerritory());
        assertEquals(true, r.isAttackerwin());

    }
}