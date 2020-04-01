package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MapDataBaseTest {

    @Test
    void containsMap() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        assertTrue (mapDataBase.containsMap("a clash of kings"));
        assertTrue (mapDataBase.containsMap("a Clash of Kings"));
        assertTrue (!mapDataBase.containsMap("Clash of Kings"));
        assertFalse(mapDataBase.containsMap(null));
    }

    @Test
    void getMap() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        assertThrows(IllegalArgumentException.class,()->{mapDataBase.getMap("not exist");});
        assertTrue(mapDataBase.containsMap("a clash of kings"));
    }

    @Test
    void testGetAllMap() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        assertTrue(mapDataBase.getAllMaps().containsKey("a clash of kings"));
    }
}