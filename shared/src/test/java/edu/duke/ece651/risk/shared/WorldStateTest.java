package edu.duke.ece651.risk.shared;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WorldStateTest {


    @Test
    void testGetMap() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        WorldState worldState = new WorldState(new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()), out), worldMap);
        assertEquals(worldState.getMap(),worldMap);
    }

    @Test
    void testGetPlayer() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlayerV2<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        WorldState worldState = new WorldState(player, worldMap);
        assertEquals(worldState.getMyPlayer(),player);
    }
}