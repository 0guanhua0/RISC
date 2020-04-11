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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static edu.duke.ece651.risk.shared.Constant.*;

public class PlayerThread extends Thread{
    Player<String> player;
    WorldMap<String> map;
    GameInfo gameInfo;
    CyclicBarrier barrier;
    int waitTimeOut;
    List<Player<String>> players;
    public PlayerThread(Player<String> player, WorldMap<String> map, GameInfo gameInfo, CyclicBarrier barrier, List<Player<String>> players) {
        this.player = player;
        this.map = map;
        this.gameInfo = gameInfo;
        this.barrier = barrier;
        this.waitTimeOut = WAIT_TIME_OUT;
        this.players = players;
    }

    public PlayerThread(Player<String> player, WorldMap<String> map, GameInfo gameInfo, CyclicBarrier barrier, int timeout) {
        this.player = player;
        this.map = map;
        this.gameInfo = gameInfo;
        this.barrier = barrier;
        this.waitTimeOut = timeout;
        this.players = new ArrayList<>();
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
                    // if valid, update the map
                    for (String terrName : serverSelect.getAllName()) {
                        Territory territory = map.getTerritory(terrName);
                        territory.addBasicUnits(serverSelect.getUnitsNum(terrName));
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
        // 4. player object(contains the information of resources)
        RoundInfo roundInfo = new RoundInfo(gameInfo.getRoundNum(), map, gameInfo.getIdToName(), player);
        player.send(roundInfo);

        // build the current state of game
        WorldState worldState = new WorldState(this.player, this.map,this.players);
        // if player hasn't lose yet, let him or her play another round of game
        if (this.player.getTerrNum() > 0){
            int checkCnt = 0;
            boolean reconnect = false;
            while (true){
                // we will only wait for user input unless he/she is connected
                // otherwise we will want 1s and then check if user is connected or not
                if (this.player.isConnect()){
                    if (reconnect){
                        // once user reconnect, send he/she the latest roundInfo
                        this.player.send(roundInfo);
                    }
                    Object recvRes = this.player.recv();
                    if (recvRes instanceof Action){
                        Action action = (Action) recvRes;
                        synchronized (this) {
                            // act accordingly based on whether the input actions are valid or not
                            if (action.isValid(worldState)){
                                // if valid, update the state of the world
                                action.perform(worldState);
                                this.player.send(SUCCESSFUL);
                            }else{
                                // otherwise ask user to resend the information
                                this.player.send(INVALID_ACTION);
                            }
                        }
                    }else if (recvRes instanceof String && recvRes.equals(ACTION_DONE)){
                        break;
                    }else {
                        this.player.send(INVALID_ACTION);
                    }
                }else {
                    Thread.sleep(1000);
                    checkCnt++;
                    reconnect = true;
                }
                // if user disconnect more than 60s in one round, we force he/she finish this round
                if (checkCnt > waitTimeOut){
                    break;
                }
            }
        }
        barrier.await();
    }
}
