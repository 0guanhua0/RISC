package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UpMaxTechActionTest {

    @Test
    void isValid() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlayerV2<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        player.setId(1);
        player.addTerritory(storm);

        WorldState worldState = new WorldState(player,worldMap);

        UpMaxTechAction upMaxTechAction = new UpMaxTechAction();
        assertTrue(upMaxTechAction.isValid(worldState));
        player.upTech();
        assertFalse(upMaxTechAction.isValid(worldState));

    }

    @Test
    void perform() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlayerV2<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        player.setId(1);
        player.addTerritory(storm);
        WorldState worldState = new WorldState(player,worldMap);

        UpMaxTechAction upMaxTechAction = new UpMaxTechAction();
        assertDoesNotThrow(()->{upMaxTechAction.perform(worldState);});
        assertThrows(IllegalArgumentException.class,()->{upMaxTechAction.perform(worldState);});

    }
}