package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.AttackResult;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TerritoryV1Test {


    @Test
    void getOwner() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        stormKindom.setOwner(3);
        assert (3 == stormKindom.getOwner());
        assert (!stormKindom.isFree());
        assertEquals("The Storm Kindom", stormKindom.getName());
    }

    @Test
    void setNeigh() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        TerritoryV1 n1 = new TerritoryV1("n1");
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> neigh = new HashSet<>() {{
            add(n1);
            add(n2);
        }};
        stormKindom.setNeigh(neigh);
        assert (stormKindom.neigh.contains(n1));
        assert (stormKindom.neigh.contains(n2));
    }

    @Test
    void isFree() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        assert (stormKindom.isFree());
        stormKindom.setOwner(3);
        assert (3 == stormKindom.getOwner() && !stormKindom.isFree());
    }

    @Test
    void addNUnits() {
        TerritoryV1 test = new TerritoryV1("test");
        assert (test.units.isEmpty());
        test.addNUnits(10);
        assert (10 == test.getUnitsNum());
        for (Unit unit : test.units) {
            assertEquals("soldier", unit.name);
        }
        assertThrows(IllegalArgumentException.class, () -> test.addNUnits(-1));
    }

    @Test
    void lossNUnits() {
        TerritoryV1 test = new TerritoryV1("test");
        assert (test.units.isEmpty());
        test.addNUnits(10);
        assert (10 == test.getUnitsNum());
        assertThrows(IllegalArgumentException.class, () -> test.lossNUnits(-1));
        assertThrows(IllegalArgumentException.class, () -> test.lossNUnits(11));
        assertEquals(10, test.getUnitsNum());
        for (Unit unit : test.units) {
            assertEquals("soldier", unit.name);
        }

        test.lossNUnits(5);
        assert (5 == test.getUnitsNum());
        for (Unit unit : test.units) {
            assertEquals("soldier", unit.name);
        }
        test.lossNUnits(5);
        assert (0 == test.getUnitsNum());
    }

    @Test
    void setStatus() {
        TerritoryV1 territoryV1 = new TerritoryV1("test");
        assert (territoryV1.isFree());
        territoryV1.setOwner(3);
        assert (!territoryV1.isFree());
        territoryV1.setIsFree(true);
        assert (territoryV1.isFree());
    }

    @Test
    void getNeigh() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        TerritoryV1 n1 = new TerritoryV1("n1");
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> neigh = new HashSet<>() {{
            add(n1);
            add(n2);
        }};
        stormKindom.setNeigh(neigh);
        assertTrue(stormKindom.getNeigh().contains(n1));
        assertTrue(stormKindom.getNeigh().contains(n2));
    }

    @Test
    void hasPathTo() throws IOException {
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
        Player<String> p1 = new PlayerV1<>("Red", 1);
        Player<String> p2 = new PlayerV1<>("Blue", 2);
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
    void performMove() throws IOException {
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
        Player<String> p1 = new PlayerV1<>("Red", 1);
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
        dorne.addNUnits(3);
        //player2
        storm.addNUnits(2);
        reach.addNUnits(2);


        //player 1 multiple place attack

        AttackAction a10 = new AttackAction("kingdom of the rock", "kingdom of the reach", 1, 1);
        AttackAction a11 = new AttackAction("principality of dorne", "kingdom of the reach", 1, 1);
        assertTrue(a10.perform(worldMap));
        assertTrue(a11.perform(worldMap));


        //player 2 attack to empty territory
        AttackAction a20 = new AttackAction("kingdom of the reach", "kingdom of the rock", 2, 2);
        assertTrue(a20.perform(worldMap));


        //perform

        List<AttackResult> resultList0 = reach.performAttackMove();
        AttackResult r = new AttackResult(1, 2, "kingdom of the reach", true);

        AttackResult r0 = resultList0.get(0);
        assertEquals(r0.getAttackerID(), r.getAttackerID());
        assertEquals(r0.getDefenderID(), r.getDefenderID());
        assertEquals(r0.getTerritory(), r.getTerritory());
        assertEquals(r0.isAttackerwin(), r.isAttackerwin());

        //check result
        assertEquals(1, reach.getOwner());
        assertEquals(2, reach.getUnitsNum());

        rock.performAttackMove();
        assertEquals(2, rock.getOwner());
        assertEquals(2, rock.getUnitsNum());

        //non-deterministic
        AttackAction a21 = new AttackAction("the storm kingdom", "principality of dorne", 2, 2);
        assertTrue(a21.perform(worldMap));

        List<AttackResult> r3 = dorne.performAttackMove();
        assertEquals(2, storm.getOwner());


    }


}