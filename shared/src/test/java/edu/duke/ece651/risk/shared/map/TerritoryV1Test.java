package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;
class TerritoryV1Test {


    @Test
    void getOwner() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        stormKindom.setOwner(3);
        assert (3==stormKindom.getOwner());
        assert (!stormKindom.isFree());
        assertEquals("The Storm Kindom", stormKindom.getName());
    }

    @Test
    void setNeigh() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        TerritoryV1 n1 = new TerritoryV1("n1");
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> neigh = new HashSet<>(){{
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
        assert (3==stormKindom.getOwner() && !stormKindom.isFree());
    }

    @Test
    void addNUnits() {
        TerritoryV1 test = new TerritoryV1("test");
        assert (test.units.isEmpty());
        test.addNUnits(10);
        assert (10==test.getUnitsNum());
        for (Unit unit : test.units) {
            assertEquals("soldier", unit.name);
        }
        assertThrows(IllegalArgumentException.class, ()->test.addNUnits(-1));
    }

    @Test
    void lossNUnits() {
        TerritoryV1 test = new TerritoryV1("test");
        assert (test.units.isEmpty());
        test.addNUnits(10);
        assert (10==test.getUnitsNum());
        assertThrows(IllegalArgumentException.class, ()-> test.lossNUnits(-1));
        assertThrows(IllegalArgumentException.class, ()-> test.lossNUnits(11));
        assertEquals(10, test.getUnitsNum());
        for (Unit unit : test.units) {
            assertEquals("soldier", unit.name);
        }

        test.lossNUnits(5);
        assert (5==test.getUnitsNum());
        for (Unit unit : test.units) {
            assertEquals("soldier", unit.name);
        }
        test.lossNUnits(5);
        assert (0==test.getUnitsNum());
    }

    @Test
    void setStatus(){
        TerritoryV1 territoryV1 = new TerritoryV1("test");
        assert (territoryV1.isFree());
        territoryV1.setOwner(3);
        assert (!territoryV1.isFree());
        territoryV1.setIsFree(true);
        assert (territoryV1.isFree());
    }

    @Test
    void getNeigh(){
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        TerritoryV1 n1 = new TerritoryV1("n1");
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> neigh = new HashSet<>(){{
            add(n1);
            add(n2);
        }};
        stormKindom.setNeigh(neigh);
        assertTrue(stormKindom.getNeigh().contains(n1));
        assertTrue (stormKindom.getNeigh().contains(n2));
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
        Player<String> p1 = new PlayerV1<>("Red",1);
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
}