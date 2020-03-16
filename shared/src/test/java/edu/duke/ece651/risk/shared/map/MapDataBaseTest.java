package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapDataBaseTest {

    @Test
    void containsMap() {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        assert (mapDataBase.containsMap("a clash of kings"));
        assert (mapDataBase.containsMap("a Clash of Kings"));
        assert (!mapDataBase.containsMap("Clash of Kings"));
        assertFalse(mapDataBase.containsMap(null));
    }

    @Test
    void getMap() {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        assertThrows(IllegalArgumentException.class,()->{mapDataBase.getMap("not exist");});
        assertTrue(mapDataBase.containsMap("a clash of kings"));
    }
}