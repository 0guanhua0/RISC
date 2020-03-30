package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MoveActionTest {

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

        //player 1
        assertEquals(0,northTerr.getUnitsNum());
        northTerr.addNUnits(7);
        assertEquals(7,northTerr.getUnitsNum());
        valeTerr.addNUnits(2);
        rockTerr.addNUnits(2);
        dorneTerr.addNUnits(2);

        //player2
        stormTerr.addNUnits(3);
        reachTerr.addNUnits(3);

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
        MoveAction a4 = new MoveAction(north, vale, 1, northTerr.getUnitsNum() + 1);
        assertFalse(a4.isValid(p1State));

        //test invalid path
        MoveAction a5 = new MoveAction(north, dorne, 1, 1);
        assertFalse(a5.isValid(p1State));

        //test invalid number of food storage
        MoveAction a6 = new MoveAction(north, vale, 1, 6);
        assertTrue(a6.isValid(p1State));
        MoveAction a7 = new MoveAction(north, vale, 1, 7);
        assertFalse(a7.isValid(p1State));


//        //test normal move
//        MoveAction a2 = new MoveAction("kingdom of the northTerr", "kingdom of mountain and valeTerr", 1, 1);
//        assert(a2.isValid(p1State));
//        MoveAction a21 = new MoveAction("kingdom of the reachTerr", "the stormTerr kingdom", 2, 1);
//        assert (a21.isValid(p1State));






    }

    @Test
    void perform() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        //prepare the world
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");
        Territory reach = worldMap.getTerritory("kingdom of the reach");
        Territory rock = worldMap.getTerritory("kingdom of the rock");
        Territory vale = worldMap.getTerritory("kingdom of mountain and vale");
        Territory north = worldMap.getTerritory("kingdom of the north");
        Territory dorne = worldMap.getTerritory("principality of dorne");
        //two players join this game
        Player<String> p1 = new PlayerV1<>("Red",1);
        Player<String> p2 = new PlayerV1<>("Blue", 2);
        //assign some territories to each player
        //player1
        p1.addTerritory(north);
        p1.addTerritory(vale);
        p1.addTerritory(rock);
        p1.addTerritory(dorne);
        //player2
        p2.addTerritory(storm);
        p2.addTerritory(reach);
        //assign some units to each territory, 5 units for each player
        //player 1
        north.addNUnits(2);
        vale.addNUnits(2);
        rock.addNUnits(1);
        dorne.addNUnits(1);
        //player2
        storm.addNUnits(2);
        reach.addNUnits(2);

        WorldState worldState = new WorldState(null, worldMap);


        assert (north.getUnitsNum()==2&&vale.getUnitsNum()==2);
        MoveAction a0 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        a0.perform(worldState);
        assert (north.getUnitsNum()==1&&vale.getUnitsNum()==3);
        MoveAction a1 = new MoveAction("kingdom of mountain and vale","kingdom of the north", 1, 1);
        a1.perform(worldState);
        assert (north.getUnitsNum()==2&&vale.getUnitsNum()==2);

        assert (2==reach.getUnitsNum()&&2==storm.getUnitsNum());
        MoveAction a2 = new MoveAction("kingdom of the reach", "the storm kingdom", 2, 1);
        a2.perform(worldState);
        System.out.println(reach.getUnitsNum());
        System.out.println(storm.getUnitsNum());
        assert (1==reach.getUnitsNum()&&3==storm.getUnitsNum());
        MoveAction a3 = new MoveAction("the storm kingdom", "kingdom of the reach", 2, 1);
        a3.perform(worldState);
        assert (2==reach.getUnitsNum()&&2==storm.getUnitsNum());

        MoveAction a4 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 2, 1);
        assertThrows(IllegalArgumentException.class, ()->a4.perform(worldState));
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