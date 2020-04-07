package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static edu.duke.ece651.risk.shared.Constant.ACTION_DONE;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.mockito.Mockito.*;

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

        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(
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
        player.setId(1);

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
    }
    
    @Test
    public void testReconnect () throws IOException, BrokenBarrierException, InterruptedException {
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

        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(
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
        player.setId(1);
        player.setConnect(false);

        GameInfo gameInfo = new GameInfo(-1, 1);
        WorldMap<String> map = new MapDataBase<String>().getMap("a clash of kings");


        CyclicBarrier b = new CyclicBarrier(2);
        CyclicBarrier barrier = spy(b);
        PlayerThread playerThread = new PlayerThread(player, map, gameInfo, barrier);
        playerThread.start();

        Thread.sleep(2000);
        player.setConnect(true);

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


    }

/*
    @Test
    void timeOut() throws InterruptedException, IOException, BrokenBarrierException {
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

        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(
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
        player.setId(1);

        GameInfo gameInfo = new GameInfo(-1, 1);
        WorldMap<String> map = new MapDataBase<String>().getMap("a clash of kings");


        CyclicBarrier b = new CyclicBarrier(2);
        CyclicBarrier barrier = spy(b);
        PlayerThread playerThread = new PlayerThread(player, map, gameInfo, barrier);
        playerThread.start();

        player.setConnect(false);
        Thread.sleep(70000);
        player.setConnect(true);


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

    }


*/

}

