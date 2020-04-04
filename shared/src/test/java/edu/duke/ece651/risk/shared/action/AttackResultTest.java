package edu.duke.ece651.risk.shared.action;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AttackResultTest {

    @Test
    void getAttackerID() {
        AttackResult r = new AttackResult(0, 1, new ArrayList<>(Arrays.asList("a", "b")),"storm", true);

        assertEquals(0, r.getAttackerID());
        assertEquals(1, r.getDefenderID());
        assertEquals("a", r.getSrcTerritories().get(0));
        assertEquals("b", r.getSrcTerritories().get(1));
        assertEquals("storm", r.getDestTerritory());
        assertTrue(r.isAttackerWin());

    }

    @Test
    void testEquals() {
        AttackResult r = new AttackResult(0, 1, new ArrayList<>(Arrays.asList("a", "b")),"storm", true);
        AttackResult r0 = new AttackResult(0, 1, new ArrayList<>(Arrays.asList("a", "b")),"storm", true);
        AttackResult r1 = new AttackResult(1, 1, new ArrayList<>(Arrays.asList("a", "b")),"storm", true);
        assertEquals(r,r0);
        assertNotEquals(r,r1);
    }
}