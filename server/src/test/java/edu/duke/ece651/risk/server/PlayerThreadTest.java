package edu.duke.ece651.risk.server;

import static edu.duke.ece651.risk.shared.Constant.ACTION_DONE;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PlayerThreadTest {

    @Test
    public void testRun() throws IOException, BrokenBarrierException, InterruptedException, ClassNotFoundException {
        // invalid select group of objects for p1
        Set<String> p1Group = new HashSet<>();
        p1Group.add("kingdom of the north");
        p1Group.add("kingdom of mountain and vale");

        // valid select group
        Set<String> p2Group = new HashSet<>();
        p2Group.add("kingdom of the north");
        p2Group.add("kingdom of mountain and vale");
        p2Group.add("the storm kingdom");

        // invalid input objects for p1
        HashMap<String, Integer> p1Chosen1  = new HashMap<>();
        p1Chosen1.put("kingdom of the north", 5);
        p1Chosen1.put("kingdom of mountain and vale", 5);
        ServerSelect s1 = new ServerSelect(p1Chosen1);

        // valid input objects for p1
        HashMap<String, Integer> p1Chosen2  = new HashMap<>();
        p1Chosen2.put("kingdom of the north", 5);
        p1Chosen2.put("kingdom of mountain and vale", 5);
        p1Chosen2.put("the storm kingdom", 5);
        ServerSelect s2 = new ServerSelect(p1Chosen2);

        // invalid move action
        MoveAction a1 = new MoveAction("kingdom of the north", "principality of dorne", 1, 1);
        // valid move action
        MoveAction a2 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        // invalid input
        String a3 = "invalid";

        Player<String> p = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(
                p1Group,
                p2Group,
                s1,
                s2,
                a1,
                a2,
                a3,
                ACTION_DONE,
                ACTION_DONE
        ))), new ByteArrayOutputStream());
        Player<String> player = spy(p);

        GameInfo gameInfo = new GameInfo(-1, 1);
        WorldMap<String> map = new MapDataBase<String>().getMap("a clash of kings");
        CyclicBarrier b = new CyclicBarrier(2);
        CyclicBarrier barrier = spy(b);
        PlayerThread playerThread = new PlayerThread(player, map, gameInfo, barrier);
        playerThread.start();

        barrier.await(); // select territory
        barrier.await(); // start playing game
        barrier.await(); // finish one round
        player.loseTerritory(map.getTerritory("kingdom of the north"));
        player.loseTerritory(map.getTerritory("kingdom of mountain and vale"));
        player.loseTerritory(map.getTerritory("the storm kingdom"));
        barrier.await(); // main thread finish processing round result
        barrier.await(); // finish one round
        gameInfo.winnerID = 1;
        barrier.await(); // main thread finish processing round result
        gameInfo.winnerID = 1;

        verify(barrier, times(12)).await();
        verify(player, times(8)).recv();
    }
    
    @Test
    public void testPlayGame() { 
        
    }

//    @Test
//    void testSelectTerritory() throws IOException, ClassNotFoundException {
//        MapDataBase<String> mapDataBase = new MapDataBase<>();
//        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
//        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
//
//        //valid select group of objects for p1
//        Set<String> p1Group = new HashSet<>();
//        p1Group.add("kingdom of the north");
//        p1Group.add("kingdom of mountain and vale");
//        p1Group.add("the storm kingdom");
//
//
//        // first invalid input objects for p1
//        HashMap<String, Integer> p1Chosen1 = new HashMap<>();
//        p1Chosen1.put("kingdom of the north", 5);
//        p1Chosen1.put("kingdom of mountain and vale", 5);
//        ServerSelect s11 = new ServerSelect(p1Chosen1);
//
//        // valid input objects for p1
//        HashMap<String, Integer> p1Chosen2  = new HashMap<>();
//        p1Chosen2.put("kingdom of the north", 5);
//        p1Chosen2.put("kingdom of mountain and vale", 5);
//        p1Chosen2.put("the storm kingdom", 5);
//        ServerSelect s12 = new ServerSelect(p1Chosen2);
//
//
//        //invalid select group of objects for p2
//        Set<String> p2Group = new HashSet<>();
//        p2Group.add("kingdom of the north");
//        p2Group.add("kingdom of mountain and vale");
//        p2Group.add("the storm kingdom");
//
//        //valid select group of objects for p2
//        Set<String> p2Group1 = new HashSet<>();
//        p2Group1.add("kingdom of the rock");
//        p2Group1.add("kingdom of the reach");
//        p2Group1.add("principality of dorne");
//
//
//        // first invalid input objects for p2
//        HashMap<String, Integer> p2Chosen1  = new HashMap<>();
//        p1Chosen2.put("the storm kingdom", 5);
//        p2Chosen1.put("kingdom of the reach", 5);
//        p2Chosen1.put("principality of dorne", 5);
//        ServerSelect s21 = new ServerSelect(p2Chosen1);
//
//        // second invalid input objects for p2
//        HashMap<String, Integer> p2Chosen2  = new HashMap<>();
//        p2Chosen2.put("kingdom of the rock", 6);
//        p2Chosen2.put("kingdom of the reach", 5);
//        p2Chosen2.put("principality of dorne", 5);
//        ServerSelect s22 = new ServerSelect(p2Chosen2);
//
//        // valid input objects for p2
//        HashMap<String, Integer> p2Chosen3  = new HashMap<>();
//        p2Chosen3.put("kingdom of the rock", 7);
//        p2Chosen3.put("kingdom of the reach", 5);
//        p2Chosen3.put("principality of dorne", 3);
//        ServerSelect s23 = new ServerSelect(p2Chosen3);
//
//        Player<String> player1 = new PlayerV1<>(
//                setupMockInput(new ArrayList<>(Arrays.asList("a clash of kings",p1Group, s11, s12))), stream1);
//        Player<String> player2 = new PlayerV1<>("Green", 2,
//                setupMockInput(new ArrayList<>(Arrays.asList(p2Group,p2Group1, s21, s22, s23))), stream2);
//        RoomController roomController = new RoomController(0, player1, mapDataBase);
//        roomController.players.add(player2);
////        roomController.selectTerritory();
//
//        //test state of the system is correct
//        assertEquals(player1.getId(),
//                mapDataBase.getMap("a clash of kings").getTerritory("kingdom of the north").getOwner());
//        assertEquals(5,
//                mapDataBase.getMap("a clash of kings").getTerritory("kingdom of the north").getUnitsNum());
//        assertEquals(player2.getId(),
//                mapDataBase.getMap("a clash of kings").getTerritory("principality of dorne").getOwner());
//        assertEquals(3,
//                mapDataBase.getMap("a clash of kings").getTerritory("principality of dorne").getUnitsNum());
//    }
//
//    @Test
//    void testPlaySingleRound() throws IOException, ClassNotFoundException {
//        // set up the game
//        MapDataBase<String> mapDataBase = new MapDataBase<>();
//        // invalid move actions(under initial map) for player1 --- don't has path
//        MoveAction a01 = new MoveAction("kingdom of the north", "principality of dorne", 1, 1);
//        // valid move actions(under initial map) for player1
//        MoveAction a11 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
//        MoveAction a12 = new MoveAction("kingdom of mountain and vale", "kingdom of the rock",1, 1);
//
//        // invalid move actions(under initial map) for player2 --- move 0 makes no sense
//        MoveAction a21 = new MoveAction("the storm kingdom","kingdom of the reach",  2, 0);
//        // valid move actions(under initial map) for player2
//        MoveAction a31 = new MoveAction("kingdom of the reach", "the storm kingdom", 2, 1);
//
//        // valid action for player1
//        AttackAction a41 = new AttackAction("principality of dorne", "kingdom of the reach", 1, 1);
//        // invalid action for player2 (territory doesn't belong to he)
//        AttackAction a42 = new AttackAction("principality of dorne", "kingdom of the reach", 2, 1);
//
//        //build the room
//        Player<String> player1 = new PlayerV1<>(
//                1,
//                setupMockInput(
//                        new ArrayList<>(
//                                Arrays.asList(
//                                        "a clash of kings",
//                                        a01,
//                                        "invalid",
//                                        1,
//                                        a11,
//                                        a12,
//                                        ACTION_DONE,
//                                        a41,
//                                        ACTION_DONE
//                                ))), new ByteArrayOutputStream());
//
//        Player<String> player2 = new PlayerV1<>(
//                2,
//                setupMockInput(
//                        new ArrayList<>(
//                                Arrays.asList(
//                                        a21,
//                                        "invalid",
//                                        a31,
//                                        ACTION_DONE,
//                                        a42,
//                                        ACTION_DONE
//                                ))), new ByteArrayOutputStream());
//
//        RoomController roomController = new RoomController(0, player1, mapDataBase);
//        roomController.players.add(player2);
//        //let each player choose some territories they want
//        WorldMap<String> curMap = mapDataBase.getMap("a clash of kings");
//        Territory t1 = curMap.getTerritory("kingdom of the north");
//        Territory t2 = curMap.getTerritory("kingdom of mountain and vale");
//        Territory t3 = curMap.getTerritory("kingdom of the rock");
//        Territory t4 = curMap.getTerritory("kingdom of the reach");
//        Territory t5 = curMap.getTerritory("the storm kingdom");
//        Territory t6 = curMap.getTerritory("principality of dorne");
//
//        roomController.players.get(0).addTerritory(t1);//kingdom of the north
//        roomController.players.get(0).addTerritory(t2);//kingdom of mountain and vale
//        roomController.players.get(0).addTerritory(t3);//kingdom of the rock
//        roomController.players.get(0).addTerritory(t6);//principality of dorne
//
//        roomController.players.get(1).addTerritory(t4);//kingdom of the reach
//        roomController.players.get(1).addTerritory(t5);//the storm kingdom
//
//        //assign some units to each territory, 6 units for each player
//        //player 1
//        t1.addNUnits(3);
//        t2.addNUnits(2);
//        t3.addNUnits(1);
//        t6.addNUnits(3);
//        //player 2
//        t4.addNUnits(5);
//        t5.addNUnits(2);
//
////        roomController.playSingleRound(1);
//
//        assertEquals(roomController.players.get(0).getTerrNum(),4);
//        assertEquals(roomController.players.get(1).getTerrNum(),2);
//        assertEquals(t1.getUnitsNum(),2);
//        assertEquals(t2.getUnitsNum(),2);
//        assertEquals(t3.getUnitsNum(),2);
//        assertEquals(t4.getUnitsNum(),4); // reach
//        assertEquals(t5.getUnitsNum(),3);
//        assertEquals(t6.getUnitsNum(),3); // dorne
//
////        roomController.playSingleRound(2);
//        // attacker will lose
//        assertEquals(3, roomController.players.get(0).getTerrNum());
//        assertEquals(3, roomController.players.get(1).getTerrNum());
//    }
} 
