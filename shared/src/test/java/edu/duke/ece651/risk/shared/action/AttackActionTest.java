package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import static org.junit.jupiter.api.Assertions.*;

class AttackActionTest {

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

        //invalid: not connected
        AttackAction a0 = new AttackAction("kingdom of the north", "the storm kingdom", 1, 1);
        assertFalse(a0.isValid(worldState));

        //invalid unit
        AttackAction a1 = new AttackAction("kingdom of the north", "the storm kingdom", 1, 0);
        assertFalse(a1.isValid(worldState));

        //invalid owner
        AttackAction a2 = new AttackAction("kingdom of the north", "the storm kingdom", 2, 1);
        assertFalse(a2.isValid(worldState));

        //invalid src unit
        AttackAction a3 = new AttackAction("kingdom of the north", "the storm kingdom", 1, 3);
        assertFalse(a3.isValid(worldState));

        //invalid dst
        AttackAction a4 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        assertFalse(a4.isValid(worldState));

        //valid
        AttackAction a5 = new AttackAction("kingdom of the reach","kingdom of the rock",  2, 1);
        assertTrue(a5.isValid(worldState));



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

        //normal attack
        AttackAction a0 = new AttackAction("kingdom of the reach","kingdom of the rock",  2, 1);
        assertTrue(a0.perform(worldState));

        //abnormal attack
        AttackAction a1 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 2, 4);
        assertThrows(IllegalArgumentException.class, ()->a1.perform(worldState));
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
}