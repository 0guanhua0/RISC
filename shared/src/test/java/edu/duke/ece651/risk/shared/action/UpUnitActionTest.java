package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UpUnitActionTest {
    private static final String storm = "the storm kingdom";
    private static final String reach = "kingdom of the reach";
    private static final String rock = "kingdom of the rock";
    private static final String vale = "kingdom of mountain and vale";
    private static final String north = "kingdom of the north";
    private static final String dorne = "principality of dorne";

    @Test
    void isValid() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        //prepare the world
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory stormTerr = worldMap.getTerritory("the storm kingdom");
        Territory reachTerr = worldMap.getTerritory("kingdom of the reach");
        Territory rockTerr = worldMap.getTerritory("kingdom of the rock");
        Territory valeTerr = worldMap.getTerritory("kingdom of mountain and vale");
        Territory northTerr = worldMap.getTerritory("kingdom of the north");
        Territory dorneTerr = worldMap.getTerritory("principality of dorne");

        //player join this game
        Player<String> p1 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p1.setId(1);
        Player<String> p2 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p2.setId(2);

        WorldState worldState1 = new WorldState(p1, worldMap);
        WorldState worldState2 = new WorldState(p2, worldMap);

        //assign some territories to player1
        p1.addTerritory(northTerr);
        p1.addTerritory(valeTerr);
        p1.addTerritory(rockTerr);
        p1.addTerritory(dorneTerr);
        //assign some territories to player2
        p2.addTerritory(stormTerr);
        p2.addTerritory(reachTerr);

        //assign some units to player 1
        northTerr.addBasicUnits(8);
        valeTerr.addBasicUnits(2);
        rockTerr.addBasicUnits(10);
        dorneTerr.addBasicUnits(2);
        //assign some units to player 2
        stormTerr.addBasicUnits(3);
        reachTerr.addBasicUnits(3);


        //test invalid territory name
        UpUnitAction action1 = new UpUnitAction("test", 0, 1, 1,1);
        assertFalse(action1.isValid(worldState1));
        //test invalid owner name
        UpUnitAction action2 = new UpUnitAction(storm, 0, 1, 1,1);
        assertFalse(action2.isValid(worldState1));
        //test invalid number of units with specified level
        UpUnitAction action3 = new UpUnitAction(vale, 0, 1, 1, 3);
        assertFalse(action3.isValid(worldState1));
        //test target level too high
        UpUnitAction action4 = new UpUnitAction(vale, 0, 2, 1, 1);
        assertFalse(action4.isValid(worldState1));
        //test invalid target tech level
        UpUnitAction action5 = new UpUnitAction(vale, 0, -1, 1, 1);
        assertFalse(action5.isValid(worldState1));
        //test not enough technology resources
        p1.useTech(30);
        UpUnitAction action6 = new UpUnitAction(north, 0, 1, 1, 7);
        assertFalse(action6.isValid(worldState1));


        //correct case
        UpUnitAction action7 = new UpUnitAction(vale, 0, 1, 1, 1);
        assertTrue(action7.isValid(worldState1));

    }

    @Test
    void perform() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        //prepare the world
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory stormTerr = worldMap.getTerritory("the storm kingdom");
        Territory reachTerr = worldMap.getTerritory("kingdom of the reach");
        Territory rockTerr = worldMap.getTerritory("kingdom of the rock");
        Territory valeTerr = worldMap.getTerritory("kingdom of mountain and vale");
        Territory northTerr = worldMap.getTerritory("kingdom of the north");
        Territory dorneTerr = worldMap.getTerritory("principality of dorne");

        //player join this game
        Player<String> p1 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p1.setId(1);
        Player<String> p2 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p2.setId(2);

        WorldState worldState1 = new WorldState(p1, worldMap);
        WorldState worldState2 = new WorldState(p2, worldMap);

        //assign some territories to player1
        p1.addTerritory(northTerr);
        p1.addTerritory(valeTerr);
        p1.addTerritory(rockTerr);
        p1.addTerritory(dorneTerr);
        //assign some territories to player2
        p2.addTerritory(stormTerr);
        p2.addTerritory(reachTerr);

        //assign some units to player 1
        northTerr.addBasicUnits(8);
        valeTerr.addBasicUnits(2);
        rockTerr.addBasicUnits(10);
        dorneTerr.addBasicUnits(2);
        //assign some units to player 2
        stormTerr.addBasicUnits(3);
        reachTerr.addBasicUnits(3);

        //test invalid territory name
        UpUnitAction action1 = new UpUnitAction("test", 0, 1, 1,1);
        assertThrows(IllegalArgumentException.class,()->{action1.perform(worldState1);});

        //test correct case
        UpUnitAction action2 = new UpUnitAction(vale, 0, 1, 1, 1);
        assertDoesNotThrow(()->{action2.perform(worldState1);});
        assertTrue(action2.perform(worldState1));
    }
}