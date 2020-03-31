package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TStatusTest {

    @Test
    void testOwnerId() {
        TStatus test = new TStatus("test");
        test.setOwnerId(1);
        assert (1==test.getOwnerId());
    }

    @Test
    void testFree() {
        TStatus test = new TStatus("test");
        assertTrue(test.isFree());
        test.setOwnerId(1);
        assertFalse(test.isFree());
        test.setOwnerId(0);
        assertTrue(test.isFree());
    }

    @Test
    void getName() {
        TStatus test = new TStatus("test");
        String name = test.getName();
        assert ("test" ==name);
    }
}
