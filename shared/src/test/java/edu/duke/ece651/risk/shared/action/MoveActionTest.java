package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class MoveActionTest {

    @Test
    void isValid() {
        MapDataBase mapDataBase = new MapDataBase();
        //prepare the world
        WorldMap worldMap = mapDataBase.getMap("a clash of kings");
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
        assert (north.isFree());
        assert (0==north.getOwner());
        p1.addTerritory(north);
        assert (!north.isFree());
        assert (north.getOwner()==1);
        p1.addTerritory(vale);
        p1.addTerritory(rock);
        p1.addTerritory(dorne);
        //player2
        p2.addTerritory(storm);
        p2.addTerritory(reach);
        //assign some units to each territory, 5 units for each player
        //player 1
        assert (north.getUnitsNum()==0);
        north.addNUnits(2);
        assert (north.getUnitsNum()==2);
        vale.addNUnits(2);
        rock.addNUnits(1);
        dorne.addNUnits(1);
        //player2
        storm.addNUnits(2);
        reach.addNUnits(2);

        //test invalid input name
        MoveAction a0 = new MoveAction("king of north", "kingdom of mountain and vale", 1, 1);
        assert (!a0.isValid(worldMap));
        MoveAction a1 = new MoveAction("kingdom of the north", "king of mountain and vale", 1, 1);
        assert (!a1.isValid(worldMap));

        //test normal move
        MoveAction a2 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        assert(a2.isValid(worldMap));
        MoveAction a21 = new MoveAction("kingdom of the reach", "the storm kingdom", 2, 1);
        assert (a21.isValid(worldMap));

        //test unexisted path between two territories controlled by the same player
        MoveAction a3 = new MoveAction("kingdom of the north", "principality of dorne", 1, 1);
        assert (!a3.isValid(worldMap));
        //test move to a territories which the user has no control over
        MoveAction a4 = new MoveAction("kingdom of the rock", "kingdom of the reach", 1, 1);
        assert (!a4.isValid(worldMap));

        //test invalid move units
        MoveAction a5 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 2);
        assert (!a5.isValid(worldMap));
        MoveAction a51 = new MoveAction("kingdom of the rock", "kingdom of mountain and vale", 1, 1);
        assert (!a51.isValid(worldMap));

        //test move between units that this player has no control over
        MoveAction a6 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 2, 1);
        assert (!a6.isValid(worldMap));

    }

    @Test
    void perform() {
        MapDataBase mapDataBase = new MapDataBase();
        //prepare the world
        WorldMap worldMap = mapDataBase.getMap("a clash of kings");
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

        assert (north.getUnitsNum()==2&&vale.getUnitsNum()==2);
        MoveAction a0 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        a0.perform(worldMap);
        assert (north.getUnitsNum()==1&&vale.getUnitsNum()==3);
        MoveAction a1 = new MoveAction("kingdom of mountain and vale","kingdom of the north", 1, 1);
        a1.perform(worldMap);
        assert (north.getUnitsNum()==2&&vale.getUnitsNum()==2);

        assert (2==reach.getUnitsNum()&&2==storm.getUnitsNum());
        MoveAction a2 = new MoveAction("kingdom of the reach", "the storm kingdom", 2, 1);
        a2.perform(worldMap);
        System.out.println(reach.getUnitsNum());
        System.out.println(storm.getUnitsNum());
        assert (1==reach.getUnitsNum()&&3==storm.getUnitsNum());
        MoveAction a3 = new MoveAction("the storm kingdom", "kingdom of the reach", 2, 1);
        a3.perform(worldMap);
        assert (2==reach.getUnitsNum()&&2==storm.getUnitsNum());

        try {
            MoveAction a4 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 2, 1);
            a4.perform(worldMap);
            assert (false);
        }catch (Exception e){
            assert (true);
        }




    }

    @Test
    void testEquals() {
//        MoveAction a0 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
//        MoveAction a1 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
//        assert(a0.equals(a1));
//        MoveAction a2 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 2);
//        assert(!a0.equals(a2));
    }

}