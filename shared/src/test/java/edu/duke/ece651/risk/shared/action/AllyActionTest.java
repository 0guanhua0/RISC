package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AllyActionTest {


    @Test
    void isValid() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player3 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        player3.setId(3);
        WorldState worldState = new WorldState(player1, worldMap, Arrays.asList(player1, player2,player3));
        AllyAction allyAction = new AllyAction(2);
        assertTrue(allyAction.isValid(worldState));
        player1.setAllyRequest(2);
        assertFalse(allyAction.isValid(worldState));
        player2.setAllyRequest(1);
        player1.allyWith(player2);
        assertEquals(player1,player2.getAlly());
        assertEquals(player2,player1.getAlly());
        player1.updateState();
        assertFalse(allyAction.isValid(worldState));
    }

    @Test
    void perform() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player3 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        player3.setId(3);
        WorldState worldState1 = new WorldState(player1, worldMap, Arrays.asList(player1,player2,player3));
        WorldState worldState2 = new WorldState(player2, worldMap, Arrays.asList(player1,player2,player3));
        WorldState worldState3 = new WorldState(player3, worldMap, Arrays.asList(player1,player2,player3));

        //1 submit an ally request to ally with 2
        AllyAction allyAction1 = new AllyAction(2);
        assertTrue(allyAction1.perform(worldState1));
        //2 submit an ally request to ally with 1
        AllyAction allyAction2 = new AllyAction(1);
        assertTrue(allyAction2.perform(worldState2));
        assertTrue(player1.isAllyWith(player2));
        assertTrue(player2.isAllyWith(player1));

        AllyAction allyAction3 = new AllyAction(1);
        assertDoesNotThrow(()->{allyAction3.perform(worldState3);});
        assertThrows(IllegalArgumentException.class,()->{new AllyAction(3).perform(worldState1);});

    }
}