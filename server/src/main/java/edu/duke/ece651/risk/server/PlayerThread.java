package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToClientMsg.ClientSelect;
import edu.duke.ece651.risk.shared.ToClientMsg.RoundInfo;
import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.SPlayer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static edu.duke.ece651.risk.shared.Constant.*;

/**
 * This is a separate thread for each player, handle all events related to the player
 * e.g. select territory, assign units, perform actions
 */
public class PlayerThread extends Thread{
    Player<String> player;
    List<SPlayer> allPlayers;
    WorldMap<String> map;
    GameInfo gameInfo;
    CyclicBarrier barrier;
    int waitTimeOut;
    List<Player<String>> players;
    onNewActionListener listener;

    /**
     *
     * @param player current player
     * @param map the map playing
     * @param gameInfo current playGame info(e.g. round number, winner ID etc.)
     * @param barrier barrier, used to synchronous multi-threading
     */
    public PlayerThread(Player<String> player, WorldMap<String> map,
                        GameInfo gameInfo, CyclicBarrier barrier,
                        List<Player<String>> players, onNewActionListener actionListener) {
        this(player, map, gameInfo, barrier, WAIT_TIME_OUT, players, actionListener);
    }

    /**
     * This is mainly for testing purpose, control the timeout
     */
    public PlayerThread(Player<String> player, WorldMap<String> map,
                        GameInfo gameInfo, CyclicBarrier barrier,
                        int timeout, List<Player<String>> players,
                        onNewActionListener actionListener) {
        this.player = player;
        this.map = map;
        this.gameInfo = gameInfo;
        this.barrier = barrier;
        this.waitTimeOut = timeout;
        this.players = players;
        this.listener = actionListener;
        allPlayers = new ArrayList<>();
        for (Player<String> p : this.players){
            allPlayers.add(new SPlayer(p.getId(), p.getName()));
        }
    }



    @Override
    public void run() {
        try {
            selectTerritory();
            barrier.await();
            // once the player finished assign units, send them the player info
            player.send(allPlayers);
            while (!gameInfo.hasFinished()){
                playGame();
                // give main thread some time to process round result
                barrier.await();
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
                        player.addTerritory(territory);
                        territory.addBasicUnits(serverSelect.getUnitsNum(terrName));
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
        // tell client the round info, contains:
        // 1. latest WorldMap
        // 2. mapping between id and color
        // 3. round number
        // 4. player object(contains the information of resources)
        RoundInfo roundInfo = new RoundInfo(gameInfo.getRoundNum(), map, gameInfo.getIdToName(), player);

        //only send if player is connect
        if(player.isConnect()) {
            player.send(roundInfo);
        }

        // build the current state of playGame
        WorldState worldState = new WorldState(player, map, players);
        // if player hasn't lose yet, let him or her play another round of playGame
        if (player.getTerrNum() > 0){
            int checkCnt = 0;
            boolean reconnect = false;
            while (true){
                // we will only wait for user input unless he/she is connected
                // otherwise we will want 1s and then check if user is connected or not
                if (player.isConnect()){
                    if (reconnect){
                        // once user reconnect, send he/she all player info & the latest roundInfo
                        player.send(allPlayers);
                        player.send(roundInfo);
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
                                if (listener != null){
                                    listener.newAction(player.getName(), action);
                                }
                            }else{
                                // otherwise ask user to resend the information
                                this.player.send(INVALID_ACTION);
                            }
                        }
                    }else if (recvRes instanceof String && recvRes.equals(ACTION_DONE)){
                        if (listener != null){
                            listener.finishRound(player.getName());
                        }
                        break;
                    }else {
                        player.send(INVALID_ACTION);
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
