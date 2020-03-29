package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TerritoryV2Test {

    @Test
    void getSize() throws IOException {
        TerritoryV2 territory = new TerritoryV2("name",3,2,4);
        assertEquals(territory.getSize(),3);
        assertEquals(territory.getFoodYield(),2);
        assertEquals(territory.getTechYield(),5);
    }
}