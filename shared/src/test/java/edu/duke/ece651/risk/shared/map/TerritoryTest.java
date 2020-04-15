package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.action.AllyAction;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TerritoryTest {

    @Test
    void setOwner() {
        Territory territory = new TerritoryImpl("name",1,1,1);
        assertThrows(IllegalArgumentException.class,()->{territory.setOwner(0);});
        territory.setOwner(1);
        assertEquals(1,territory.getOwner());
    }

    @Test
    void buildUnifiedArmy() throws IOException {
        //prepare the state
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        //player1 and player2 ally with each other and will attack together
        //player3 and player4 ally with each other, only will player3 will engage in this attack
        //player5 don't have an ally
        //player6 owns a territory to be attacked
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player3 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player4 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player5 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player6 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        List<Player<String>> playerList = new ArrayList<>(Arrays.asList(player1,player2,player3,player4,player5,player6));
        player1.setId(1);
        player2.setId(2);
        player3.setId(3);
        player4.setId(4);
        player5.setId(5);
        player6.setId(6);

        WorldState worldState1 = new WorldState(player1, worldMap, playerList);
        WorldState worldState2 = new WorldState(player2, worldMap, playerList);
        WorldState worldState3 = new WorldState(player3, worldMap, playerList);
        WorldState worldState4 = new WorldState(player4, worldMap, playerList);
        WorldState worldState5 = new WorldState(player5, worldMap, playerList);
        WorldState worldState6 = new WorldState(player6, worldMap, playerList);

        //territory to be attacked
        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        player6.addTerritory(test);
        test.addUnit(new Unit(0));

        //player1 and player2 ally with each other
        AllyAction allyAction1 = new AllyAction(2);
        allyAction1.perform(worldState1);
        AllyAction allyAction2 = new AllyAction(1);
        allyAction2.perform(worldState2);
        //player3 and player4 ally with each other and will attack together
        AllyAction allyAction3 = new AllyAction(4);
        allyAction3.perform(worldState3);
        AllyAction allyAction4 = new AllyAction(3);
        allyAction4.perform(worldState4);

        Army army0 = new Army(1,"p1",5);


        test.addAttack(player1, army0);
        test.addAttack(player3, new Army(1,"p3",4));
        test.addAttack(player5, new Army(1,"p5",1));
        test.addAttack(player2, new Army(1,"p2",2));


        List<Map<Player, List<Army>>> maps = test.buildUnifiedArmy();
        assertEquals(3,maps.size());
        //for player1&2
        Map<Player, List<Army>> unifiedArmy0 = new HashMap<>();
        //for player3
        Map<Player, List<Army>> unifiedArmy1 = new HashMap<>();
        //for player5
        Map<Player, List<Army>> unifiedArmy2 = new HashMap<>();

        for (Map<Player, List<Army>> map : maps) {
            if (map.size()==2){
                unifiedArmy0 = map;
            }else{
                if (map.containsKey(player3)){
                    unifiedArmy1 = map;
                }else{
                    unifiedArmy2 = map;
                }
            }
        }

        assertTrue(unifiedArmy0.containsKey(player1)&&unifiedArmy0.containsKey(player2));
        assertEquals(army0,unifiedArmy0.get(player1).get(0));
        assertTrue(unifiedArmy1.containsKey(player3));
        assertTrue(unifiedArmy2.containsKey(player5));



    }
}