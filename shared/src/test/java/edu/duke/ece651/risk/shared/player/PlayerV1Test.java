package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PlayerV1Test {

    @Test
    void getFoodNum() throws IOException {
        PlayerV1<String> playerV1 = new PlayerV1<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        assertEquals(playerV1.getFoodNum(),Integer.MAX_VALUE);
    }
    @Test
    void getTechNum() throws IOException {
        PlayerV1<String> playerV1 = new PlayerV1<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        assertEquals(playerV1.getTechNum(),Integer.MAX_VALUE);
    }

    @Test
    void canUpTech() throws IOException {
        PlayerV1<String> playerV1 = new PlayerV1<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        assertFalse(playerV1.canUpMaxTech());
    }

    @Test
    void updateState() throws IOException {
        PlayerV1<String> playerV1 = new PlayerV1<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        playerV1.setId(1);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");
        assertEquals(0,storm.getBasicUnitsNum());
        playerV1.addTerritory(storm);
        playerV1.updateState();
        assertEquals(1,storm.getBasicUnitsNum());
    }
    @Test
    void testOthers() throws IOException {
        PlayerV1<String> playerV1 = new PlayerV1<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        playerV1.setId(1);
        assertDoesNotThrow(()->{playerV1.useFood(1);});
        assertDoesNotThrow(()->{playerV1.useTech(1);});
        assertDoesNotThrow(()->{playerV1.upMaxTech();});
        assertEquals(1,playerV1.getTechLevel());


    }
}