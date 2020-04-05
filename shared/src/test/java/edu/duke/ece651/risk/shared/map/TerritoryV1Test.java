package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TerritoryV1Test {


    @Test
    void testSetGetOwner() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        stormKindom.setOwner(3);
        assertEquals (3 , stormKindom.getOwner());
        assertFalse (stormKindom.isFree());
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
        assertTrue (stormKindom.neigh.contains(n1));
        assertTrue (stormKindom.neigh.contains(n2));
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
        assertTrue (test.unitsV1.isEmpty());
        test.addBasicUnits(10);
        assertEquals (10 , test.getBasicUnitsNum());
        assertThrows(IllegalArgumentException.class, () -> test.addBasicUnits(-1));
    }

    @Test
    void lossNUnits() {
        TerritoryV1 test = new TerritoryV1("test");
        assertTrue (test.unitsV1.isEmpty());
        test.addBasicUnits(10);
        assertEquals (10 , test.getBasicUnitsNum());
        assertThrows(IllegalArgumentException.class, () -> test.loseBasicUnits(-1));
        assertThrows(IllegalArgumentException.class, () -> test.loseBasicUnits(11));
        assertEquals(10, test.getBasicUnitsNum());


        test.loseBasicUnits(5);
        assertEquals (5 , test.getBasicUnitsNum());

        test.loseBasicUnits(5);
        assertEquals (0 , test.getBasicUnitsNum());
    }

    @Test
    void setStatus() {
        TerritoryV1 territoryV1 = new TerritoryV1("test");
        assertTrue (territoryV1.isFree());
        territoryV1.setOwner(3);
        assertFalse (territoryV1.isFree());
        territoryV1.setFree();
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
    void testResolveCombat() throws IOException {
        TerritoryV1 test = new TerritoryV1("test");
        assertEquals(null,test.resolveCombat(0,null,null,null));
//        MapDataBase<String> mapDataBase = new MapDataBase<>();
//        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
//        Territory storm = worldMap.getTerritory("the storm kingdom");
//        Territory reach = worldMap.getTerritory("kingdom of the reach");
//        Territory rock = worldMap.getTerritory("kingdom of the rock");
//        Territory vale = worldMap.getTerritory("kingdom of mountain and vale");
//        Territory north = worldMap.getTerritory("kingdom of the north");
//        Territory dorne = worldMap.getTerritory("principality of dorne");
//        //two players join this game
//
//        Player<String> p1 = new PlayerV1<>("Red", 1);
//        Player<String> p2 = new PlayerV1<>("Blue", 2);
//        //assign some territories to each player
//        //player1
//        p1.addTerritory(north);
//        p1.addTerritory(vale);
//        p1.addTerritory(rock);
//        p1.addTerritory(dorne);
//        //player2
//        p2.addTerritory(storm);
//        p2.addTerritory(reach);
//        //assign some units to each territory, 5 units for each player
//        //player 1
//        north.addBasicUnits(2);
//        vale.addBasicUnits(2);
//        rock.addBasicUnits(10);
//        dorne.addBasicUnits(5);
//        //player2
//        storm.addBasicUnits(10);
//        reach.addBasicUnits(10);
//
//        //player 1 multiple place attack(10 + 5)
//        AttackAction a10 = new AttackAction("kingdom of the rock", "kingdom of the reach", 1, 10);
//        AttackAction a11 = new AttackAction("principality of dorne", "kingdom of the reach", 1, 5);
//        AttackAction a12 = new AttackAction("kingdom of mountain and vale", "the storm kingdom", 1, 1);
//
//        WorldState worldState1 = new WorldState(p1,worldMap);
//        WorldState worldState2 = new WorldState(p2,worldMap);
//
//        assertTrue(a10.perform(worldState1));
//        assertTrue(a11.perform(worldState1));
//        assertTrue(a12.perform(worldState1));
//
//        //player 2 attack to empty territory
//        AttackAction a20 = new AttackAction("kingdom of the reach", "kingdom of the rock", 2, 2);
//        assertTrue(a20.perform(worldState2));
//
//        //perform
//        List<AttackResult> resultList0 = reach.resolveCombats();
//        // attacker: 10 + 5, defender 10
//        AttackResult r = new AttackResult(1, 2, new ArrayList<>(Arrays.asList("kingdom of the rock")), "kingdom of the reach", true);
//
//        AttackResult r0 = resultList0.get(0);
//        assertEquals(r0.getAttackerID(), r.getAttackerID());
//        assertEquals(r0.getDefenderID(), r.getDefenderID());
//        assertEquals(r0.getDestTerritory(), r.getDestTerritory());
//        assertEquals(r0.isAttackerWin(), r.isAttackerWin());
//
//        //check result
//        assertEquals(1, reach.getOwner());
//        assertEquals(8, reach.getBasicUnitsNum());
//
//        rock.resolveCombats();
//        assertEquals(2, rock.getOwner());
//        assertEquals(2, rock.getBasicUnitsNum());
//
//        // lose
//        assertFalse(storm.resolveCombats().get(0).isAttackerWin());
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


    @Test
    void canUpUnit() {
        TerritoryV1 name = new TerritoryV1("name");
        assertFalse(name.canUpUnit(0,0,0));
        name.upUnit(0,0,0);
    }
    @Test
    void addUnits() {
        TerritoryV1 name = new TerritoryV1("name");
        assertThrows(IllegalArgumentException.class,()->{name.addUnits(1,1);});
        assertDoesNotThrow(()->{name.addUnits(1,0);});
    }

    @Test
    void loseUnits() {
        TerritoryV1 name = new TerritoryV1("name");
        name.addUnits(1,0);
        assertThrows(IllegalArgumentException.class,()->{name.loseUnits(1,1);});
        assertDoesNotThrow(()->{name.loseUnits(1,0);});
    }

    @Test
    void getUnitsNum() {
        TerritoryV1 name = new TerritoryV1("name");
        name.addBasicUnits(1);
        assertEquals(0,name.getUnitsNum(1));
        assertEquals(1,name.getUnitsNum(0));
    }

    @Test
    void getDetailInfo(){
        TerritoryV1 t1 = new TerritoryV1("name");
        assertNull(t1.getUnitGroup());
    }
}