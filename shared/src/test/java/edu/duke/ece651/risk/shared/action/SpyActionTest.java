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

import static edu.duke.ece651.risk.shared.Constant.SPY_COST;
import static org.junit.jupiter.api.Assertions.*;

class SpyActionTest {

    @Test
    void isValid() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        WorldState worldState1 = new WorldState(player1, worldMap, Arrays.asList(player1, player2));

        Territory storm = worldMap.getTerritory("the storm kingdom");
        Territory vale = worldMap.getTerritory("kingdom of mountain and vale");
        player1.addTerritory(storm);
        player2.addTerritory(vale);


        SpyAction spyAction0 = new SpyAction(2);////don't have enough resource
        assertFalse(spyAction0.isValid(worldState1));
        for (int i = 0; i < 5; i++) {
            player1.updateState();
        }
        SpyAction spyAction1 = new SpyAction(2);//correct
        assertTrue(spyAction1.isValid(worldState1));

        //check invalid target
        SpyAction spyAction2 = new SpyAction(0);//too small
        SpyAction spyAction3 = new SpyAction(1);//can't spy herself
        SpyAction spyAction4 = new SpyAction(3);//too large
        assertFalse(spyAction2.isValid(worldState1));
        assertFalse(spyAction3.isValid(worldState1));
        assertFalse(spyAction4.isValid(worldState1));

        spyAction1.perform(worldState1);//set the state
        //once set the state, always valid until the next round
        assertFalse(player1.canAffordSpy());
        SpyAction spyAction5 = new SpyAction(2);
        assertTrue(spyAction5.isValid(worldState1));
    }

    @Test
    void perform() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        WorldState worldState1 = new WorldState(player1, worldMap, Arrays.asList(player1, player2));

        Territory storm = worldMap.getTerritory("the storm kingdom");
        Territory vale = worldMap.getTerritory("kingdom of mountain and vale");
        player1.addTerritory(storm);
        player2.addTerritory(vale);


        SpyAction spyAction0 = new SpyAction(2);////don't have enough resource
        assertThrows(IllegalArgumentException.class,()->{spyAction0.perform(worldState1);});

        player2.addAction(new AllyAction(2));
        player2.addAction(new MoveAction("src","dest",0,2));

        assertFalse(spyAction0.isValid(worldState1));
        for (int i = 0; i < 5; i++) {
            player1.updateState();
        }
        SpyAction spyAction1 = new SpyAction(2);//correct
        int techNum = player1.getTechNum();
        spyAction1.perform(worldState1);
        assertEquals(techNum-SPY_COST,player1.getTechNum());
        assertTrue(player1.isSpying());

        SpyAction spyAction2 = new SpyAction(2);//correct
        spyAction2.perform(worldState1);
        assertEquals(techNum-SPY_COST,player1.getTechNum());
        assertTrue(player1.isSpying());




    }
}