package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToClientMsg.ClientSelect;
import edu.duke.ece651.risk.shared.ToClientMsg.RoundInfo;
import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static edu.duke.ece651.risk.shared.Constant.*;
import static edu.duke.ece651.risk.shared.Constant.INVALID_ACTION;

public class PlayerAllThread extends Thread{
    Player<String> player;
    Map<Integer, String> idToName;
    WorldMap<String> map;
    CyclicBarrier barrier;

    GameInfo gameInfo;

    public PlayerAllThread(Player<String> player, WorldMap<String> map, Map<Integer, String> idToName, GameInfo gameInfo, CyclicBarrier barrier) {
        this.player = player;
        this.map = map;
        this.idToName = idToName;
        this.gameInfo = gameInfo;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            selectTerritory();
            System.out.println(Thread.currentThread().getId() + " start wait");
            barrier.await();
            System.out.println(Thread.currentThread().getId() + " end wait");
            while (!gameInfo.hasFinished()){
                playGame();
                barrier.await();
            }
        }catch (Exception ignored){
            System.out.println(ignored.toString());
        }
    }

    void selectTerritory() throws IOException, IllegalArgumentException, ClassNotFoundException, BrokenBarrierException, InterruptedException {
        int terrPerUsr = map.getTerrPerPlayer();
        //the variable below is the total number of units that a single player can choose
        int totalUnits = UNITS_PER_TERR * terrPerUsr;

        // get the current list of occupied territories
        ClientSelect clientSelect = new ClientSelect(totalUnits, terrPerUsr, map.getName());
        // tell player to select
        player.send(clientSelect);

        // stage 1: select territory group(check validation before move on)
        while(true){
            Set<String> recv = (HashSet<String>)player.recv();
            synchronized (this) {
                if (map.hasFreeGroup(recv)){
                    player.send(SUCCESSFUL);
                    this.map.useGroup(recv);
                    break;
                }else {
                    player.send(SELECT_GROUP_ERROR);
                }
            }
        }

        // stage 2: assign units(check validation before move on)
        while (true){
            ServerSelect serverSelect = (ServerSelect)player.recv();
            synchronized (this) {
                if(serverSelect.isValid(map, totalUnits, terrPerUsr)){
                    //if valid, update the map
                    for (String terrName : serverSelect.getAllName()) {
                        Territory territory = map.getTerritory(terrName);
                        territory.addNUnits(serverSelect.getUnitsNum(terrName));
                        player.addTerritory(territory);
                    }
                    player.send(SUCCESSFUL);
                    break;
                }else{
                    player.send(SELECT_TERR_ERROR);
                }
            }
        }

        // wait all players to finish this step
        barrier.await();
    }

    void playGame() throws IOException, ClassNotFoundException, BrokenBarrierException, InterruptedException {
        // tell client the round info
        // 1. latest WorldMap
        // 2. mapping between id and color
        // 3. round number
        RoundInfo roundInfo = new RoundInfo(gameInfo.getRoundNum(), map, idToName);
        System.out.println(Thread.currentThread().getId() + " send roundInfo1");
        player.send(roundInfo);
        System.out.println(Thread.currentThread().getId() + " send roundInfo2");
        while (true){
            Object recvRes = player.recv();
            if (recvRes instanceof Action){
                Action action = (Action) recvRes;

                synchronized (this) {
                    // act accordingly based on whether the input actions are valid or not
                    if (action.isValid(map)){
                        // if valid, update the state of the world
                        action.perform(map);
                        player.send(SUCCESSFUL);
                    }else{
                        // otherwise ask user to resend the information
                        player.send(INVALID_ACTION);
                    }
                }
            }else if (recvRes instanceof String && recvRes.equals(ACTION_DONE)){
                break;
            }else {
                player.send(INVALID_ACTION);
            }
        }
        barrier.await();
    }
}
