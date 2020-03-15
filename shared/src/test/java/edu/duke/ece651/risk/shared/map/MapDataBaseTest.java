package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapDataBaseTest {

    @Test
    void containsMap() {
        MapDataBase mapDataBase = new MapDataBase();
        assert (mapDataBase.containsMap("a clash of kings"));
        assert (mapDataBase.containsMap("a Clash of Kings"));
        assert (!mapDataBase.containsMap("Clash of Kings"));
    }

    @Test
    void getMap() {
        MapDataBase mapDataBase = new MapDataBase();
        assertThrows(IllegalArgumentException.class,()->{mapDataBase.getMap("not exist");});
        if (mapDataBase.containsMap("a clash of kings")){
            WorldMap map = mapDataBase.getMap("a clash of kings");
        }else{
            assert (false);
        }
    }
}