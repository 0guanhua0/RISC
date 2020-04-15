package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerritoryTest {

    @Test
    void setOwner() {
        Territory territory = new TerritoryImpl("name",1,1,1);
        assertThrows(IllegalArgumentException.class,()->{territory.setOwner(0);});
        territory.setOwner(1);
        assertEquals(1,territory.getOwner());
    }
}