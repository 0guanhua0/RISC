package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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

        //test normal attack
        AttackAction a0 = new AttackAction("kingdom of the north", "the storm kingdom", 1, 1);
        assertTrue(a0.isValid(worldMap));

        //invalid unit
        AttackAction a1 = new AttackAction("kingdom of the north", "the storm kingdom", 1, 0);
        assertFalse(a1.isValid(worldMap));

        //invalid owner
        AttackAction a2 = new AttackAction("kingdom of the north", "the storm kingdom", 2, 1);
        assertFalse(a2.isValid(worldMap));

        //invalid src unit
        AttackAction a3 = new AttackAction("kingdom of the north", "the storm kingdom", 1, 3);
        assertFalse(a3.isValid(worldMap));

        //invalid dst
        AttackAction a4 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        assertFalse(a4.isValid(worldMap));




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


        //normal attack
        AttackAction a0 = new AttackAction("kingdom of the north", "the storm kingdom", 1, 1);
        assertTrue(a0.perform(worldMap));

        assertEquals(2, worldMap.getTerritory(a0.dest).getOwner());
        assertEquals(1, worldMap.getTerritory(a0.dest).getUnitsNum());

        //combo move
        AttackAction a1 = new AttackAction("kingdom of mountain and vale", "kingdom of the reach", 1, 2);
        a1.perform(worldMap);
        assertEquals(1, worldMap.getTerritory(a1.dest).getOwner());
        assertEquals(0, worldMap.getTerritory(a1.dest).getUnitsNum());

        //abnormal attack
        AttackAction a4 = new AttackAction("kingdom of the north", "kingdom of mountain and vale", 2, 4);
        assertThrows(IllegalArgumentException.class, ()->a4.perform(worldMap));
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