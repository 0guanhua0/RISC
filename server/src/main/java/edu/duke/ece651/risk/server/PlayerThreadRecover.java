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

public class PlayerThreadRecover extends Thread {
    Player<String> player;
    List<SPlayer> allPlayers;
    WorldMap<String> map;
    GameInfo gameInfo;
    CyclicBarrier barrier;
    int waitTimeOut;
    List<Player<String>> players;
    onNewActionListener listener;

    //actual player thread in game
    public PlayerThreadRecover(Player<String> player, WorldMap<String> map,
                               GameInfo gameInfo, CyclicBarrier barrier, List<Player<String>> players,
                               onNewActionListener actionListener) {
        this.player = player;
        this.map = map;
        this.gameInfo = gameInfo;
        this.barrier = barrier;
        this.waitTimeOut = WAIT_TIME_OUT;
        this.players = players;
        this.listener = actionListener;
        allPlayers = new ArrayList<>();
        for (Player<String> p : this.players) {
            allPlayers.add(new SPlayer(p.getId(), p.getName()));
        }
    }


    @Override
    public void run() {
        try {
            barrier.await();
            while (!gameInfo.hasFinished()) {
                playGame();
                // give main thread some time to process round result
                barrier.await();
                barrier.await();

            }
        } catch (Exception ignored) {
        }
    }


    void playGame() throws IOException, ClassNotFoundException, BrokenBarrierException, InterruptedException {
        // tell client the round info, contains:
        // 1. latest WorldMap
        // 2. mapping between id and color
        // 3. round number
        // 4. player object(contains the information of resources)
        RoundInfo roundInfo = new RoundInfo(gameInfo.getRoundNum(), map, gameInfo.getIdToName(), player);

        //only send if player is connect
        if (player.isConnect()) {
            player.send(roundInfo);
        }

        // build the current state of playGame
        WorldState worldState = new WorldState(player, map, players);
        // if player hasn't lose yet, let him or her play another round of playGame
        if (player.getTerrNum() > 0) {
            int checkCnt = 0;
            boolean reconnect = false;
            while (true) {
                // we will only wait for user input unless he/she is connected
                // otherwise we will want 1s and then check if user is connected or not
                if (player.isConnect()) {
                    if (reconnect) {
                        // once user reconnect, send he/she all player info & the latest roundInfo
                        player.send(allPlayers);
                        roundInfo = new RoundInfo(gameInfo.getRoundNum(), map, gameInfo.getIdToName(), player);
                        player.send(roundInfo);
                    }
                    Object recvRes = player.recv();
                    if (recvRes instanceof Action) {
                        Action action = (Action) recvRes;
                        synchronized (this) {
                            // act accordingly based on whether the input actions are valid or not
                            if (action.isValid(worldState)) {
                                // if valid, update the state of the world
                                action.perform(worldState);
                                player.send(SUCCESSFUL);
                                if (listener != null) {
                                    listener.newAction(player.getName(), action);
                                }
                            } else {
                                // otherwise ask user to resend the information
                                this.player.send(INVALID_ACTION);
                            }
                        }
                    } else if (recvRes instanceof String && recvRes.equals(ACTION_DONE)) {
                        if (listener != null) {
                            listener.finishRound(player.getName());
                        }
                        break;
                    } else {
                        player.send(INVALID_ACTION);
                    }
                } else {
                    Thread.sleep(100);
                    checkCnt++;
                    reconnect = true;
                }
                // if user disconnect more than 60s in one round, we force he/she finish this round
                if (checkCnt > waitTimeOut) {
                    break;
                }
            }
        }
        barrier.await();
    }

}
