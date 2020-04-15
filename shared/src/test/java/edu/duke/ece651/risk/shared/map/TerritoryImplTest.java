package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.Utils;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.action.AllyAction;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.AttackResult;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;
import static org.junit.jupiter.api.Assertions.*;

class TerritoryImplTest {
    private static final String storm = "the storm kingdom";
    private static final String reach = "kingdom of the reach";
    private static final String rock = "kingdom of the rock";
    private static final String vale = "kingdom of mountain and vale";
    private static final String north = "kingdom of the north";
    private static final String dorne = "principality of dorne";

    @Test
    void getSize() throws IOException {
        TerritoryImpl territory = new TerritoryImpl("name",3,2,4);
        assertEquals(territory.getSize(),3);
        assertEquals(territory.getFoodYield(),2);
        assertEquals(territory.getTechYield(),4);
    }


    @Test
    void canAddUnits() {
        TerritoryImpl territory = new TerritoryImpl("name",3,2,4);
        assertTrue(territory.canAddUnits(1,1));
        assertFalse(territory.canAddUnits(0,1));
        assertFalse(territory.canAddUnits(0,UNIT_BONUS.keySet().stream().max(Integer::compareTo).get()+1));
    }

    @Test
    void canLoseUnits() {
        TerritoryImpl territory = new TerritoryImpl("name",3,2,4);
        assertFalse(territory.canLoseUnits(0,1));
        territory.addUnits(1,1);
        assertTrue(territory.canLoseUnits(1,1));
        assertFalse(territory.canLoseUnits(1,2));
        assertFalse(territory.canLoseUnits(2,1));
    }

    @Test
    void addBasicUnits() {
        TerritoryImpl territory = new TerritoryImpl("name",3,2,4);
        territory.addBasicUnits(1);
        assertEquals(1,territory.getBasicUnitsNum());
        assertThrows(IllegalArgumentException.class,()->{territory.addBasicUnits(0);});
        assertThrows(IllegalArgumentException.class,()->{territory.addBasicUnits(-1);});

    }

    @Test
    void loseBasicUnits() {
        TerritoryImpl territory = new TerritoryImpl("name",3,2,4);
        territory.addBasicUnits(1);
        assertThrows(IllegalArgumentException.class,()->{territory.loseBasicUnits(-1);});
        assertThrows(IllegalArgumentException.class,()->{territory.loseBasicUnits(2);});
        territory.loseBasicUnits(1);
        assertEquals(0,territory.getBasicUnitsNum());
    }

    @Test
    void addUnits() {
        TerritoryImpl territory = new TerritoryImpl("name",3,2,4);
        territory.addUnits(1,0);
        assertEquals(1,territory.getBasicUnitsNum());
        territory.addUnits(1,1);
        assertEquals(1,territory.getUnitsNum(1));
        assertThrows(IllegalArgumentException.class,()->{territory.addUnits(0,1);});
    }

    @Test
    void loseUnits() {
        TerritoryImpl territory = new TerritoryImpl("name",3,2,4);
        assertThrows(IllegalArgumentException.class,()->{territory.loseUnits(0,1);});
        territory.addUnits(1,1);
        assertDoesNotThrow(()->{territory.loseUnits(1,1);});
    }

    @Test
    void getBasicUnitsNum() {
        TerritoryImpl territory = new TerritoryImpl("name",3,2,4);
        assertEquals(0,territory.getBasicUnitsNum());
        territory.addBasicUnits(1);
        assertEquals(1,territory.getBasicUnitsNum());
    }

    @Test
    void getUnitsNum() {
        TerritoryImpl territory = new TerritoryImpl("name",3,2,4);
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

        WorldState worldState1 = new WorldState(p1,worldMap);
        WorldState worldState2 = new WorldState(p2,worldMap);


        //player 1 combine force(10+5) to attack reach
        AttackAction a10 = new AttackAction(rock, reach, 1, 10);
        AttackAction a11 = new AttackAction(dorne, reach, 1, 5);
        AttackAction a12 = new AttackAction(vale, storm, 1, 1);

        assertTrue(a10.perform(worldState1));
        assertTrue(a11.perform(worldState1));
        assertTrue(a12.perform(worldState1));

        //player 2 attack rock, which is without units now
        AttackAction a20 = new AttackAction(reach, rock, 2, 2);
        assertTrue(a20.perform(worldState2));

        //battle happening at reach
        //caculate the result
        List<AttackResult> resultList0 = reachTerr.resolveCombats();
        AttackResult r0 = resultList0.get(0);

        // get the expected result: attacker: 10 + 5, defender 10
        AttackResult r = new AttackResult(1, 2,
                new ArrayList<String>(Arrays.asList(rock,dorne)), reach, true);
        boolean attackerWin = r0.isAttackerWin();
        boolean attackerWin1 = r.isAttackerWin();
        assertEquals(r0,r);
        //check result
        assertEquals(1, reachTerr.getOwner());
        assertEquals(8, reachTerr.getBasicUnitsNum());

        //battle happening at rock territory
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

        Territory northTerr = worldMap.getTerritory(north);
        northTerr.addBasicUnits(5);
        northTerr.upUnit(5,0,1);
        Map<Integer, List<Unit>> unitGroup = northTerr.getUnitGroup();
        assertFalse(unitGroup.containsKey(0));
        assertEquals(5,unitGroup.get(1).size());
    }

    @Test
    void getDetailInfo(){
        TerritoryImpl t2 = new TerritoryImpl("name", 1, 1, 1);
        assertNotNull(t2.getUnitGroup());
    }

    @Test
    void addAllyUnit() throws IOException {
        //prepare the state
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        player1.addTerritory(test);
        assertThrows(IllegalStateException.class,()->{test.addAllyUnit(new Unit(1));});

        WorldState worldState1 = new WorldState(player1, worldMap, Arrays.asList(player1,player2));
        WorldState worldState2 = new WorldState(player2, worldMap, Arrays.asList(player1,player2));
        //1 submit an ally request to ally with 2
        AllyAction allyAction1 = new AllyAction(2);
        allyAction1.perform(worldState1);
        //2 submit an ally request to ally with 1
        AllyAction allyAction2 = new AllyAction(1);
        allyAction2.perform(worldState2);

        assertEquals(0,test.allyUnits.getOrDefault(1,new ArrayList<>()).size());
        test.addAllyUnit(new Unit(1));
        assertEquals(1,test.allyUnits.get(1).size());
        test.addAllyUnit(new Unit(1));
        assertEquals(2,test.allyUnits.get(1).size());
    }

    @Test
    void ruptureAlly() throws IOException {
        //prepare the state
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        WorldState worldState1 = new WorldState(player1, worldMap, Arrays.asList(player1,player2));
        WorldState worldState2 = new WorldState(player2, worldMap, Arrays.asList(player1,player2));


        Territory storm = worldMap.getTerritory("the storm kingdom");
        Territory reach = worldMap.getTerritory("kingdom of the reach");
        Territory vale = worldMap.getTerritory("kingdom of mountain and vale");

        player1.addTerritory(vale);
        player2.addTerritory(reach);
        player2.addTerritory(storm);


        assertThrows(IllegalStateException.class,()->{vale.ruptureAlly();});

        //1 submit an ally request to ally with 2
        AllyAction allyAction1 = new AllyAction(2);
        allyAction1.perform(worldState1);
        //2 submit an ally request to ally with 1
        AllyAction allyAction2 = new AllyAction(1);
        allyAction2.perform(worldState2);

        vale.addAllyUnit(new Unit(1));
        vale.addAllyUnit(new Unit(1));
        storm.addUnit(new Unit(1));
        storm.addUnit(new Unit(2));


        vale.ruptureAlly();
        assertEquals(3,storm.getUnitsNum(1));
        assertEquals(1,storm.getUnitsNum(2));
    }

    @Test
    void selectMaxDefendUnit() throws IOException {
        //prepare the state
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        player1.addTerritory(test);
        WorldState worldState1 = new WorldState(player1, worldMap, Arrays.asList(player1,player2));
        WorldState worldState2 = new WorldState(player2, worldMap, Arrays.asList(player1,player2));
        AllyAction allyAction1 = new AllyAction(2);
        allyAction1.perform(worldState1);
        AllyAction allyAction2 = new AllyAction(1);
        allyAction2.perform(worldState2);


        assertThrows(IllegalStateException.class,()->{test.selectMaxDefendUnit();});

        test.addAllyUnit(new Unit(1));
        test.addAllyUnit(new Unit(3));

        List<Integer> list = test.selectMaxDefendUnit();
        assertEquals(3,list.get(0));
        assertEquals(1,list.get(1));

        test.addUnit(new Unit(1));
        test.addUnit(new Unit(4));
        List<Integer> list2 = test.selectMaxDefendUnit();
        assertEquals(4,list2.get(0));
        assertEquals(0,list2.get(1));

        test.addAllyUnit(new Unit(4));
        List<Integer> list3 = test.selectMaxDefendUnit();
        assertEquals(4,list3.get(0));
        assertTrue(list.get(1)==0||list.get(1)==1);

    }

    @Test
    void selectMinDefendUnit() throws IOException {
        //prepare the state
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        player1.addTerritory(test);
        WorldState worldState1 = new WorldState(player1, worldMap, Arrays.asList(player1,player2));
        WorldState worldState2 = new WorldState(player2, worldMap, Arrays.asList(player1,player2));
        AllyAction allyAction1 = new AllyAction(2);
        allyAction1.perform(worldState1);
        AllyAction allyAction2 = new AllyAction(1);
        allyAction2.perform(worldState2);

        assertThrows(IllegalStateException.class,()->{test.selectMinDefendUnit();});

        //ally now has a level-1 unit and level-3 unit
        test.addAllyUnit(new Unit(1));
        test.addAllyUnit(new Unit(3));
        List<Integer> list = test.selectMinDefendUnit();
        assertEquals(1,list.get(0));
        assertEquals(1,list.get(1));

        //player herself now have a level-0 unit and level-1 unit
        test.addUnit(new Unit(0));
        test.addUnit(new Unit(1));
        List<Integer> list2 = test.selectMinDefendUnit();
        assertEquals(0,list2.get(0));
        assertEquals(0,list2.get(1));

        test.addAllyUnit(new Unit(0));
        List<Integer> list3 = test.selectMinDefendUnit();
        assertEquals(0,list3.get(0));
        assertTrue(list.get(1)==0||list.get(1)==1);
    }

    @Test
    void selectMaxAttackUnit() {
        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        List<TreeMap<Integer,Integer>> combinedAttack = new ArrayList<>();
        TreeMap<Integer, Integer> treeMap1 = new TreeMap<Integer, Integer>(){{
            put(1,1);
            put(0,2);
        }};
        TreeMap<Integer, Integer> treeMap2 = new TreeMap<>();
        TreeMap<Integer, Integer> treeMap3 = new TreeMap<>();
        combinedAttack.add(treeMap1);
        combinedAttack.add(treeMap2);
        combinedAttack.add(treeMap3);

        List<Integer> list1 = test.selectMaxAttackUnit(combinedAttack);
        assertEquals(1,list1.get(0));
        assertEquals(0,list1.get(1));
        treeMap3.put(1,1);
        List<Integer> list2 = test.selectMaxAttackUnit(combinedAttack);
        assertEquals(1,list2.get(0));
        assertTrue(list1.get(1)==0||list2.get(1)==2);

    }

    @Test
    void selectMinAttackUnit() {
        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        List<TreeMap<Integer,Integer>> combinedAttack = new ArrayList<>();
        TreeMap<Integer, Integer> treeMap1 = new TreeMap<Integer, Integer>(){{
            put(1,1);
            put(0,2);
        }};
        TreeMap<Integer, Integer> treeMap2 = new TreeMap<>();
        TreeMap<Integer, Integer> treeMap3 = new TreeMap<>();
        combinedAttack.add(treeMap1);
        combinedAttack.add(treeMap2);
        combinedAttack.add(treeMap3);

        List<Integer> list1 = test.selectMinAttackUnit(combinedAttack);
        assertEquals(0,list1.get(0));
        assertEquals(0,list1.get(1));
        treeMap3.put(0,1);
        List<Integer> list2 = test.selectMinAttackUnit(combinedAttack);
        assertEquals(0,list2.get(0));
        assertTrue(list1.get(1)==0||list2.get(1)==2);
    }

    @Test
    void updateAttacker() {
        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        List<TreeMap<Integer,Integer>> combinedAttack = new ArrayList<>();
        TreeMap<Integer, Integer> treeMap1 = new TreeMap<Integer, Integer>(){{
            put(1,1);
            put(0,2);
        }};
        TreeMap<Integer, Integer> treeMap2 = new TreeMap<>();
        combinedAttack.add(treeMap1);
        combinedAttack.add(treeMap2);
        test.updateAttacker(1,0,combinedAttack);
        assertFalse(treeMap1.containsKey(1));
        test.updateAttacker(0,0,combinedAttack);
        assertEquals(1,treeMap1.get(0));
        assertThrows(IllegalArgumentException.class,()->{test.updateAttacker(2,0,combinedAttack);});
    }

    @Test
    void updateDefender() throws IOException {
        //prepare the state
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        player1.addTerritory(test);
        WorldState worldState1 = new WorldState(player1, worldMap, Arrays.asList(player1,player2));
        WorldState worldState2 = new WorldState(player2, worldMap, Arrays.asList(player1,player2));
        AllyAction allyAction1 = new AllyAction(2);
        allyAction1.perform(worldState1);
        AllyAction allyAction2 = new AllyAction(1);
        allyAction2.perform(worldState2);

        test.addAllyUnit(new Unit(2));
        test.addAllyUnit(new Unit(2));
        test.addAllyUnit(new Unit(0));

        test.addUnit(new Unit(0));
        test.addUnit(new Unit(2));



        assertTrue(test.unitGroup.containsKey(0));
        test.updateDefender(0,0);
        assertFalse(test.unitGroup.containsKey(0));

        test.updateDefender(0,1);
        assertFalse(test.allyUnits.containsKey(0));
        test.updateDefender(2,1);
        assertEquals(1,test.allyUnits.get(2).size());

    }

    @Test
    void updateForceState() throws IOException {
        //prepare the state
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player3 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player4 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player5 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());

        player1.setId(1);
        player2.setId(2);

        player3.setId(3);
        player4.setId(4);

        player5.setId(5);



        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        player1.addTerritory(test);

        //player1 ally with player2
        WorldState worldState1 = new WorldState(player1, worldMap, Arrays.asList(player1,player2));
        WorldState worldState2 = new WorldState(player2, worldMap, Arrays.asList(player1,player2));
        AllyAction allyAction1 = new AllyAction(2);
        allyAction1.perform(worldState1);
        AllyAction allyAction2 = new AllyAction(1);
        allyAction2.perform(worldState2);

        List<Player> attackers1 = Arrays.asList(player3, player4);
        TreeMap<Integer, Integer> treeMap1 = new TreeMap<Integer, Integer>(){{
            put(1,1);
            put(0,2);
        }};
        TreeMap<Integer, Integer> treeMap2 = new TreeMap<Integer, Integer>(){{
            put(3,1);
        }};
        List<TreeMap<Integer,Integer>> combinedAttack1 = Arrays.asList(treeMap1,treeMap2);

        int x = test.updateState(attackers1, combinedAttack1);
        assertTrue(x==4||x==3);
        assertTrue(3==test.getOwner()&&4==test.getAllyId()||4==test.getOwner()&&3==test.getAllyId());
        assertTrue(
                (2==test.allyUnits.getOrDefault(0,new ArrayList<>()).size()
                &&1==test.unitGroup.getOrDefault(3,new ArrayList<>()).size())
                ||(2==test.unitGroup.getOrDefault(0,new ArrayList<>()).size()
                &&1==test.allyUnits.getOrDefault(3,new ArrayList<>()).size())
        );


        List<Player> attackers2 = Arrays.asList(player5);
        List<TreeMap<Integer,Integer>> combinedAttack2 = Arrays.asList(new TreeMap<>());
        assertEquals(-1,test.updateState(attackers2, combinedAttack2));



    }
}