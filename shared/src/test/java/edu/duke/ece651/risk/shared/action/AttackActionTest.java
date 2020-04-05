package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AttackActionTest {
    private static final String storm = "the storm kingdom";
    private static final String reach = "kingdom of the reach";
    private static final String rock = "kingdom of the rock";
    private static final String vale = "kingdom of mountain and vale";
    private static final String north = "kingdom of the north";
    private static final String dorne = "principality of dorne";

    @Test
    void testConstructor(){
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(0,2);
        map.put(1,3);
        map.put(4,7);
        AttackAction attackAction = new AttackAction("src", "dest", 1, map);
        assertEquals(attackAction.src,"src");
        assertEquals("dest",attackAction.dest);
        assertEquals(1,attackAction.playerId);
        assertEquals(map,attackAction.levelToNum);
        assertEquals(12,attackAction.unitsNum);
    }


    @Test
    void isValid() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        //prepare the world
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");
        Territory reach = worldMap.getTerritory("kingdom of the reach");
        Territory rock = worldMap.getTerritory("kingdom of the rock");
        Territory vale = worldMap.getTerritory("kingdom of mountain and vale");
        Territory north = worldMap.getTerritory("kingdom of the north");
        Territory dorne = worldMap.getTerritory("principality of dorne");

        //two players join this game
        Player<String> p1 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p1.setId(1);
        Player<String> p2 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p2.setId(2);

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
        north.addBasicUnits(2);
        vale.addBasicUnits(2);
        rock.addBasicUnits(1);
        dorne.addBasicUnits(1);
        //player2
        storm.addBasicUnits(2);
        reach.addBasicUnits(10);

        WorldState worldState1 = new WorldState(p1, worldMap);
        WorldState worldState2 = new WorldState(p2, worldMap);

        //invalid: not connected
        AttackAction a0 = new AttackAction("kingdom of the north", "the storm kingdom", 1, 1);
        assertFalse(a0.isValid(worldState1));

        //invalid unit
        AttackAction a1 = new AttackAction("kingdom of the north", "the storm kingdom", 1, 0);
        assertFalse(a1.isValid(worldState1));

        //invalid owner
        AttackAction a2 = new AttackAction("kingdom of the north", "the storm kingdom", 2, 1);
        assertFalse(a2.isValid(worldState1));

        //invalid src unit
        AttackAction a3 = new AttackAction("kingdom of the north", "the storm kingdom", 1, 3);
        assertFalse(a3.isValid(worldState1));

        //invalid dst
        AttackAction a4 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        assertFalse(a4.isValid(worldState1));

        //invalid territory name
        AttackAction a5 = new AttackAction("kingdo of the reach","kingdom of the rock",  2, 1);
        assertFalse(a5.isValid(worldState2));
        AttackAction a6 = new AttackAction("kingdom of the reach","kingdo of the rock",  2, 1);
        assertFalse(a6.isValid(worldState2));

        //invalid units storage
        p2.useFood(28);
        AttackAction a7 = new AttackAction("kingdom of the reach","kingdom of the rock",  2, 2);
        assertTrue(a7.isValid(worldState2));
        AttackAction a8 = new AttackAction("kingdom of the reach","kingdom of the rock",  2, 3);
        assertFalse(a8.isValid(worldState2));



        //valid
        AttackAction b1 = new AttackAction("kingdom of the reach","kingdom of the rock",  2, 1);
        assertTrue(b1.isValid(worldState2));



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
        Player<String> p1 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p1.setId(1);
        Player<String> p2 = new PlayerV2<>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        p2.setId(2);

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
        north.addBasicUnits(2);
        vale.addBasicUnits(2);
        rock.addBasicUnits(1);
        dorne.addBasicUnits(1);
        //player2
        storm.addBasicUnits(2);
        reach.addBasicUnits(2);

        WorldState worldState2 = new WorldState(p2, worldMap);

        //normal attack
        int startFood = p2.getFoodNum();
        AttackAction a0 = new AttackAction("kingdom of the reach","kingdom of the rock",  2, 1);
        assertTrue(a0.perform(worldState2));
        assertEquals(p2.getFoodNum(),startFood-1);

        //abnormal attack
        AttackAction a1 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 2, 4);
        assertThrows(IllegalArgumentException.class, ()->a1.perform(worldState2));
    }

    @Test
    void testEquals() {
        AttackAction a0 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        AttackAction a1 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        assertEquals(a0, a0);
        assertTrue(a0.equals(a1));

        MoveAction a2 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 2);
        assertFalse(a0.equals(a2));
        AttackAction a3 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 2, 1);
        assertFalse(a0.equals(a3));
    }

    @Test
    void testToString() {
        AttackAction a0 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        System.out.println(a0.toString());
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1,1);
        map.put(0,4);
        map.put(2,1);
        AttackAction a1 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 1, map);
        System.out.println(a1.toString());
    }
}