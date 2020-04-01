package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.AttackResult;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TerritoryV1Test {


    @Test
    void testSetGetOwner() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        stormKindom.setOwner(3);
        assertEquals(3, stormKindom.getOwner());
        assertFalse(stormKindom.isFree());
        assertEquals("The Storm Kindom", stormKindom.getName());
        assertThrows(IllegalArgumentException.class,()->{stormKindom.setOwner(0);});
        assertThrows(IllegalArgumentException.class,()->{stormKindom.setOwner(-1);});

    }

    @Test
    void setNeigh() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        TerritoryV1 n1 = new TerritoryV1("n1");
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> neigh = new HashSet<Territory>() {{
            add(n1);
            add(n2);
        }};
        stormKindom.setNeigh(neigh);
        assertTrue(stormKindom.neigh.contains(n1));
        assertTrue(stormKindom.neigh.contains(n2));
    }

    @Test
    void isFree() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        assertTrue (stormKindom.isFree());
        stormKindom.setOwner(3);
        assertTrue (3 == stormKindom.getOwner() && !stormKindom.isFree());
    }

    @Test
    void addNUnits() {
        TerritoryV1 test = new TerritoryV1("test");
        assertTrue (test.units.isEmpty());
        test.addNUnits(10);
        assertEquals(10, test.getUnitsNum());
        for (Unit unit : test.units) {
            assertEquals("soldier", unit.name);
        }
        assertThrows(IllegalArgumentException.class, () -> test.addNUnits(-1));
    }

    @Test
    void lossNUnits() {
        TerritoryV1 test = new TerritoryV1("test");
        assertTrue (test.units.isEmpty());
        test.addNUnits(10);
        assertEquals(10, test.getUnitsNum());
        assertThrows(IllegalArgumentException.class, () -> test.lossNUnits(-1));
        assertThrows(IllegalArgumentException.class, () -> test.lossNUnits(11));
        assertEquals(10, test.getUnitsNum());
        for (Unit unit : test.units) {
            assertEquals("soldier", unit.name);
        }

        test.lossNUnits(5);
        assertEquals(5, test.getUnitsNum());
        for (Unit unit : test.units) {
            assertEquals("soldier", unit.name);
        }
        test.lossNUnits(5);
        assertEquals(0, test.getUnitsNum());
    }

    @Test
    void setStatus() {
        TerritoryV1 territoryV1 = new TerritoryV1("test");
        assertTrue (territoryV1.isFree());
        territoryV1.setOwner(3);
        assertFalse(territoryV1.isFree());
        territoryV1.setIsFree();
        assertTrue (territoryV1.isFree());
    }

    @Test
    void getNeigh() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        TerritoryV1 n1 = new TerritoryV1("n1");
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> neigh = new HashSet<Territory>() {{
            add(n1);
            add(n2);
        }};
        stormKindom.setNeigh(neigh);
        assertTrue(stormKindom.getNeigh().contains(n1));
        assertTrue(stormKindom.getNeigh().contains(n2));
    }

    @Test
    void hasPathTo() throws IOException {
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
        Player<String> p1 = new PlayerV1<String>("Red", 1);
        Player<String> p2 = new PlayerV1<String>("Blue", 2);
        //assign some territories to each player
        p1.addTerritory(north);
        p1.addTerritory(vale);
        p1.addTerritory(rock);
        p1.addTerritory(dorne);
        p2.addTerritory(storm);
        p2.addTerritory(reach);

        assertTrue(storm.hasPathTo(reach));
        assertTrue(reach.hasPathTo(storm));
        assertTrue(north.hasPathTo(vale));
        assertFalse(storm.hasPathTo(storm));
        assertFalse(storm.hasPathTo(vale));
        assertFalse(storm.hasPathTo(dorne));

        //change the ownership of reach to p1
        p2.loseTerritory(reach);
        p1.addTerritory(reach);
        assertEquals(1, reach.getOwner());
        assertTrue(north.hasPathTo(dorne));
        assertFalse(north.hasPathTo(storm));

    }

    @Test
    void testResolveCombat() throws IOException {
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

        Player<String> p1 = new PlayerV1<String>("Red", 1);
        Player<String> p2 = new PlayerV1<String>("Blue", 2);
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
        rock.addNUnits(10);
        dorne.addNUnits(5);
        //player2
        storm.addNUnits(10);
        reach.addNUnits(10);

        //player 1 multiple place attack(10 + 5)
        AttackAction a10 = new AttackAction("kingdom of the rock", "kingdom of the reach", 1, 10);
        AttackAction a11 = new AttackAction("principality of dorne", "kingdom of the reach", 1, 5);
        AttackAction a12 = new AttackAction("kingdom of mountain and vale", "the storm kingdom", 1, 1);

        WorldState worldState1 = new WorldState(p1,worldMap);
        WorldState worldState2 = new WorldState(p2,worldMap);

        assertTrue(a10.perform(worldState1));
        assertTrue(a11.perform(worldState1));
        assertTrue(a12.perform(worldState1));

        //player 2 attack to empty territory
        AttackAction a20 = new AttackAction("kingdom of the reach", "kingdom of the rock", 2, 2);
        assertTrue(a20.perform(worldState2));

        //perform
        List<AttackResult> resultList0 = reach.resolveCombats();
        // attacker: 10 + 5, defender 10
        AttackResult r = new AttackResult(1, 2, new ArrayList<String>(Arrays.asList("kingdom of the rock")), "kingdom of the reach", true);

        AttackResult r0 = resultList0.get(0);
        assertEquals(r0.getAttackerID(), r.getAttackerID());
        assertEquals(r0.getDefenderID(), r.getDefenderID());
        assertEquals(r0.getDestTerritory(), r.getDestTerritory());
        assertEquals(r0.isAttackerWin(), r.isAttackerWin());

        //check result
        assertEquals(1, reach.getOwner());
        assertEquals(8, reach.getUnitsNum());

        rock.resolveCombats();
        assertEquals(2, rock.getOwner());
        assertEquals(2, rock.getUnitsNum());

        // lose
        assertFalse(storm.resolveCombats().get(0).isAttackerWin());

    }


    @Test
    void getSize() throws IOException {
        TerritoryV1 name = new TerritoryV1("name");
        assertEquals(name.getSize(),0);
    }

    @Test
    void getFoodYield() {
        TerritoryV1 name = new TerritoryV1("name");
        assertEquals(0,name.getFoodYield());
    }

    @Test
    void getTechYield() {
        TerritoryV1 name = new TerritoryV1("name");
        assertEquals(0,name.getTechYield());
    }
}