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
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MoveActionTest {


    private static final String storm = "the storm kingdom";
    private static final String reach = "kingdom of the reach";
    private static final String rock = "kingdom of the rock";
    private static final String vale = "kingdom of mountain and vale";
    private static final String north = "kingdom of the north";
    private static final String dorne = "principality of dorne";

    @Test
    void testConst() {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(1,3);
        map.put(2,7);
        MoveAction moveAction = new MoveAction("src", "dest", 1, map);
        assertEquals(moveAction.src,"src");
        assertEquals("dest",moveAction.dest);
        assertEquals(1,moveAction.playerId);
        assertEquals(map,moveAction.levelToNum);
        assertEquals(10,moveAction.unitsNum);
    }

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

        //two players join this game
        Player<String> p1 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p1.setId(1);
        Player<String> p2 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p2.setId(2);

        //assign some territories to each player
        //player1
        assertTrue(northTerr.isFree());
        assertEquals(0,northTerr.getOwner());
        p1.addTerritory(northTerr);
        p1.addTerritory(valeTerr);
        p1.addTerritory(rockTerr);
        p1.addTerritory(dorneTerr);
        //player2
        p2.addTerritory(stormTerr);
        p2.addTerritory(reachTerr);

        //assign some units to each player
        //player 1
        assertEquals(0,northTerr.getBasicUnitsNum());
        northTerr.addBasicUnits(7);
        assertEquals(7,northTerr.getBasicUnitsNum());
        valeTerr.addBasicUnits(2);
        rockTerr.addBasicUnits(10);
        dorneTerr.addBasicUnits(2);
        //player2
        stormTerr.addBasicUnits(3);
        reachTerr.addBasicUnits(3);

        WorldState p1State = new WorldState(p1, worldMap);
        WorldState p2State = new WorldState(p2, worldMap);

        //test invalid input name
        Action a0 = new MoveAction("king of north", "kingdom of mountain and vale", 1, 1);
        assertFalse (a0.isValid(p1State));
        Action a1 = new MoveAction("kingdom of the north", "king of mountain and vale", 1, 1);
        assertFalse (a1.isValid(p1State));

        //test invalid source territory
        Action a2 = new MoveAction(storm, vale, 1, 1);
        assertFalse(a2.isValid(p1State));
        //test invalid target territory
        MoveAction a21 = new MoveAction(vale, storm, 1, 1);
        assertFalse(a21.isValid(p1State));

        //test invalid number of units
        Action a3 = new MoveAction(north,vale,1,0);
        assertFalse(a3.isValid(p1State));
        MoveAction a4 = new MoveAction(north, vale, 1, northTerr.getBasicUnitsNum() + 1);
        assertFalse(a4.isValid(p1State));

        //test invalid path
        MoveAction a5 = new MoveAction(north, dorne, 1, 1);
        assertFalse(a5.isValid(p1State));

        //test invalid number of food storage
        MoveAction a6 = new MoveAction(north, vale, 1, 6);
        assertTrue(a6.isValid(p1State));
        MoveAction a7 = new MoveAction(north, vale, 1, 7);
        assertFalse(a7.isValid(p1State));

        //test legality move
        for (int i=1;i<=10;i++){
            int unit = i;
            MoveAction moveAction = new MoveAction(rock, vale, 1, unit);
            assertTrue(moveAction.isValid(p1State));
        }

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

        //two players join this game
        Player<String> p1 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p1.setId(1);
        Player<String> p2 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p2.setId(2);

        //assign some territories to each player
        //player1
        assertTrue(northTerr.isFree());
        assertEquals(0,northTerr.getOwner());
        p1.addTerritory(northTerr);
        p1.addTerritory(valeTerr);
        p1.addTerritory(rockTerr);
        p1.addTerritory(dorneTerr);
        //player2
        p2.addTerritory(stormTerr);
        p2.addTerritory(reachTerr);

        //assign some units to each player
        //player 1
        assertEquals(0,northTerr.getBasicUnitsNum());
        northTerr.addBasicUnits(7);
        assertEquals(7,northTerr.getBasicUnitsNum());
        valeTerr.addBasicUnits(2);
        rockTerr.addBasicUnits(10);
        dorneTerr.addBasicUnits(2);
        //player2
        stormTerr.addBasicUnits(3);
        reachTerr.addBasicUnits(3);

        WorldState p1State = new WorldState(p1, worldMap);
        WorldState p2State = new WorldState(p2, worldMap);


        //test invalid action
        MoveAction a0 = new MoveAction(north, dorne, 1, 1);
        assertThrows(IllegalArgumentException.class,()->{a0.perform(p1State);});

        int northStart = northTerr.getBasicUnitsNum();
        int valeStart = valeTerr.getBasicUnitsNum();
        int foodStorage = p1.getFoodNum();
        for (int i=1;i<=3;i++){
            MoveAction moveAction = new MoveAction(north, vale, 1, 2);
            moveAction.perform(p1State);
            assertEquals(northTerr.getBasicUnitsNum(),northStart-i*2);
            assertEquals(valeTerr.getBasicUnitsNum(),valeStart+i*2);
            assertEquals(foodStorage-5*i*2,p1.getFoodNum());
        }
        MoveAction moveAction = new MoveAction(rock, vale, 1, 1);
        assertThrows(IllegalArgumentException.class,()->{moveAction.perform(p1State);});
    }

    @Test
    void testEquals() {
        MoveAction a0 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        MoveAction a1 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        assertEquals(a0, a0);
        assertEquals(a0, a1);
        MoveAction a2 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 2);
        assertNotEquals(a0, a2);
        AttackAction a3 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        assertNotEquals(a0, a3);
    }
}