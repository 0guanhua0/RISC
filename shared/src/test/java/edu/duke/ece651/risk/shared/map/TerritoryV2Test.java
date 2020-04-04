package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.Utils;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.AttackResult;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;
import static org.junit.jupiter.api.Assertions.*;

class TerritoryV2Test {
    private static final String storm = "the storm kingdom";
    private static final String reach = "kingdom of the reach";
    private static final String rock = "kingdom of the rock";
    private static final String vale = "kingdom of mountain and vale";
    private static final String north = "kingdom of the north";
    private static final String dorne = "principality of dorne";

    @Test
    void getSize() throws IOException {
        TerritoryV2 territory = new TerritoryV2("name",3,2,4);
        assertEquals(territory.getSize(),3);
        assertEquals(territory.getFoodYield(),2);
        assertEquals(territory.getTechYield(),4);
    }


    @Test
    void canAddUnits() {
        TerritoryV2 territory = new TerritoryV2("name",3,2,4);
        assertTrue(territory.canAddUnits(1,1));
        assertFalse(territory.canAddUnits(0,1));
        assertFalse(territory.canAddUnits(0,UNIT_BONUS.keySet().stream().max(Integer::compareTo).get()+1));
    }

    @Test
    void canLoseUnits() {
        TerritoryV2 territory = new TerritoryV2("name",3,2,4);
        assertFalse(territory.canLoseUnits(0,1));
        territory.addUnits(1,1);
        assertTrue(territory.canLoseUnits(1,1));
        assertFalse(territory.canLoseUnits(1,2));
        assertFalse(territory.canLoseUnits(2,1));
    }

    @Test
    void addBasicUnits() {
        TerritoryV2 territory = new TerritoryV2("name",3,2,4);
        territory.addBasicUnits(1);
        assertEquals(1,territory.getBasicUnitsNum());
        assertThrows(IllegalArgumentException.class,()->{territory.addBasicUnits(0);});
        assertThrows(IllegalArgumentException.class,()->{territory.addBasicUnits(-1);});

    }

    @Test
    void loseBasicUnits() {
        TerritoryV2 territory = new TerritoryV2("name",3,2,4);
        territory.addBasicUnits(1);
        assertThrows(IllegalArgumentException.class,()->{territory.loseBasicUnits(-1);});
        assertThrows(IllegalArgumentException.class,()->{territory.loseBasicUnits(2);});
        territory.loseBasicUnits(1);
        assertEquals(0,territory.getBasicUnitsNum());
    }

    @Test
    void addUnits() {
        TerritoryV2 territory = new TerritoryV2("name",3,2,4);
        territory.addUnits(1,0);
        assertEquals(1,territory.getBasicUnitsNum());
        territory.addUnits(1,1);
        assertEquals(1,territory.getUnitsNum(1));
        assertThrows(IllegalArgumentException.class,()->{territory.addUnits(0,1);});
    }

    @Test
    void loseUnits() {
        TerritoryV2 territory = new TerritoryV2("name",3,2,4);
        assertThrows(IllegalArgumentException.class,()->{territory.loseUnits(0,1);});
        territory.addUnits(1,1);
        assertDoesNotThrow(()->{territory.loseUnits(1,1);});
    }

    @Test
    void getBasicUnitsNum() {
        TerritoryV2 territory = new TerritoryV2("name",3,2,4);
        assertEquals(0,territory.getBasicUnitsNum());
        territory.addBasicUnits(1);
        assertEquals(1,territory.getBasicUnitsNum());
    }

    @Test
    void getUnitsNum() {
        TerritoryV2 territory = new TerritoryV2("name",3,2,4);
        assertEquals(0,territory.getUnitsNum(0));
        territory.addUnits(1,1);
        assertEquals(1,territory.getUnitsNum(1));
    }

    @Test
    void resolveCombat() throws IOException {
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

        p1.addTerritory(northTerr);
        p1.addTerritory(valeTerr);
        p1.addTerritory(rockTerr);
        p1.addTerritory(dorneTerr);
        //player2
        p2.addTerritory(stormTerr);
        p2.addTerritory(reachTerr);
        //assign some units to each territory, 5 units for each player
        //player 1
        northTerr.addBasicUnits(2);
        valeTerr.addBasicUnits(2);
        rockTerr.addBasicUnits(10);
        dorneTerr.addBasicUnits(5);
        //player2
        stormTerr.addBasicUnits(10);
        reachTerr.addBasicUnits(10);

        //player 1 multiple place attack(10 + 5)
        AttackAction a10 = new AttackAction(rock, reach, 1, 10);
        AttackAction a11 = new AttackAction(dorne, reach, 1, 5);
        AttackAction a12 = new AttackAction(vale, storm, 1, 1);

        WorldState worldState1 = new WorldState(p1,worldMap);
        WorldState worldState2 = new WorldState(p2,worldMap);

        assertTrue(a10.perform(worldState1));
        assertTrue(a11.perform(worldState1));
        assertTrue(a12.perform(worldState1));

        //player 2 attack to territory without units
        AttackAction a20 = new AttackAction(reach, rock, 2, 2);
        assertTrue(a20.perform(worldState2));

        //perform
        List<AttackResult> resultList0 = reachTerr.resolveCombats();

        // attacker: 10 + 5, defender 10
        AttackResult r = new AttackResult(1, 2,
                new ArrayList<String>(Arrays.asList(rock,dorne)), reach, true);


        AttackResult r0 = resultList0.get(0);
        boolean attackerWin = r0.isAttackerWin();
        boolean attackerWin1 = r.isAttackerWin();
        assertEquals(r0,r);


        //check result
        assertEquals(1, reachTerr.getOwner());
        assertEquals(8, reachTerr.getBasicUnitsNum());

        rockTerr.resolveCombats();
        assertEquals(2, rockTerr.getOwner());
        assertEquals(2, rockTerr.getBasicUnitsNum());

        //lose
        assertFalse(stormTerr.resolveCombats().get(0).isAttackerWin());
    }
    @Test
    void canUpUnit() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        //prepare the world
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory stormTerr = worldMap.getTerritory("the storm kingdom");
        stormTerr.addBasicUnits(5);
        stormTerr.addUnits(2,1);
        assertFalse(stormTerr.canUpUnit(0,0,1));
        assertTrue(stormTerr.canUpUnit(5,0,1));
        assertFalse(stormTerr.canUpUnit(5,0,Utils.getMaxKey(UNIT_BONUS)+1));

        assertFalse(stormTerr.canUpUnit(6,0,1));
        assertFalse(stormTerr.canUpUnit(1,1,0));
        assertFalse(stormTerr.canUpUnit(1, Utils.getMaxKey(UNIT_BONUS),0));
    }

    @Test
    void upUnit() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        //prepare the world
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory stormTerr = worldMap.getTerritory("the storm kingdom");
        stormTerr.addBasicUnits(5);
        stormTerr.addUnits(4,1);
        assertThrows(IllegalArgumentException.class,()->{stormTerr.upUnit(6,0,1);});
        stormTerr.upUnit(3,0,1);
        assertEquals(2,stormTerr.getBasicUnitsNum());
        assertEquals(7,stormTerr.getUnitsNum(1));
    }


}