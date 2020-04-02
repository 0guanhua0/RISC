package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToClientMsg.ClientSelect;
import edu.duke.ece651.risk.shared.ToClientMsg.RoundInfo;
import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static edu.duke.ece651.risk.shared.Constant.*;

public class PlayerThread extends Thread{
    Player<String> player;
    WorldMap<String> map;
    GameInfo gameInfo;
    CyclicBarrier barrier;

    public PlayerThread(Player<String> player, WorldMap<String> map, GameInfo gameInfo, CyclicBarrier barrier) {
        this.player = player;
        this.map = map;
        this.gameInfo = gameInfo;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            selectTerritory();
            barrier.await();
            while (!gameInfo.hasFinished()){
                playGame();
                // give main thread some time to process round result
                barrier.await();
            }
        }catch (Exception ignored){
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
        RoundInfo roundInfo = new RoundInfo(gameInfo.getRoundNum(), map, gameInfo.getIdToName());

        player.send(roundInfo);
        //build the current state of game
        WorldState worldState = new WorldState(this.player, this.map);
        //if player hasn't losed yet, let him or her play another round of game
        if (player.getTerrNum() > 0){
            while (true){
                //if player disconnect, simply sleep for 60s
                if (!player.isConnect) {
                    Thread.sleep(WAIT_TIME);
                    break;
                }


                Object recvRes = player.recv();
                if (recvRes instanceof Action){
                    Action action = (Action) recvRes;
                    synchronized (this) {
                        // act accordingly based on whether the input actions are valid or not
                        if (action.isValid(worldState)){
                            // if valid, update the state of the world
                            action.perform(worldState);
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
        }
        barrier.await();
    }
}
