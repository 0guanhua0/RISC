package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.BeforeAll;
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
    static Set<String> p1Group = new HashSet<>();
    static Set<String> p2Group = new HashSet<>();
    static HashMap<String, Integer> p1Chosen1  = new HashMap<>();
    static HashMap<String, Integer> p1Chosen2  = new HashMap<>();
    static ServerSelect s1 = new ServerSelect(p1Chosen1);
    static ServerSelect s2 = new ServerSelect(p1Chosen2);
    static MoveAction a1;
    static MoveAction a2;
    static String a3;

    @BeforeAll
    public static void beforeAll(){
        // invalid select group of objects for p1
        p1Group.add("kingdom of the north");
        p1Group.add("kingdom of mountain and vale");

        // valid select group
        p2Group.add("kingdom of the north");
        p2Group.add("kingdom of mountain and vale");
        p2Group.add("the storm kingdom");

        // invalid input objects for p1
        p1Chosen1.put("kingdom of the north", 5);
        p1Chosen1.put("kingdom of mountain and vale", 5);

        // valid input objects for p1
        p1Chosen2.put("kingdom of the north", 5);
        p1Chosen2.put("kingdom of mountain and vale", 5);
        p1Chosen2.put("the storm kingdom", 5);

        // invalid move action
        a1 = new MoveAction("kingdom of the north", "principality of dorne", 1, 1);
        // valid move action
        a2 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        // invalid input
        a3 = "invalid";
    }

    @Test
    public void testRun() throws IOException, BrokenBarrierException, InterruptedException {
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
        PlayerThread playerThread = new PlayerThread(player, map, gameInfo, barrier,new ArrayList<>());
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
        playerThread.interrupt();
        playerThread.join();
    }

    @Test
    public void testReconnect() throws IOException, BrokenBarrierException, InterruptedException {
        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(
                p1Group,
                p2Group,
                s1,
                s2,
                a1,
                a2,
                ACTION_DONE,
                a3,
                ACTION_DONE
        ))), new ByteArrayOutputStream());
        player.setId(1);
        player.setConnect(false);

        GameInfo gameInfo = new GameInfo(-1, 1);
        WorldMap<String> map = new MapDataBase<String>().getMap("a clash of kings");

        CyclicBarrier b = new CyclicBarrier(2);
        CyclicBarrier barrier = spy(b);
        PlayerThread playerThread = new PlayerThread(player, map, gameInfo, barrier,new ArrayList<>());
        playerThread.start();

        barrier.await(); // select territory
        barrier.await(); // start playing game
        // sleep some time and then reconnect
        Thread.sleep(1000);
        player.setConnect(true);
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
        playerThread.interrupt();
        playerThread.join();
    }

    @Test
    public void testTimeOut() throws IOException, BrokenBarrierException, InterruptedException {
        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(
                p1Group,
                p2Group,
                s1,
                s2,
                a1,
                a2,
                ACTION_DONE,
                a3,
                ACTION_DONE
        ))), new ByteArrayOutputStream());
        player.setId(1);
        player.setConnect(false);

        GameInfo gameInfo = new GameInfo(-1, 1);
        WorldMap<String> map = new MapDataBase<String>().getMap("a clash of kings");

        CyclicBarrier b = new CyclicBarrier(2);
        CyclicBarrier barrier = spy(b);
        PlayerThread playerThread = new PlayerThread(player, map, gameInfo, barrier, 3,Arrays.asList());
        playerThread.start();

        barrier.await(); // select territory
        barrier.await(); // start playing game
        // sleep some time and then reconnect
        Thread.sleep(5000);
        player.setConnect(true);
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
        playerThread.interrupt();
        playerThread.join();
    }

}

