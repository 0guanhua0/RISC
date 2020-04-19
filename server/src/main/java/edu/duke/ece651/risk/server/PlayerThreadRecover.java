package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToClientMsg.RoundInfo;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.SPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static edu.duke.ece651.risk.shared.Constant.*;

public class PlayerThreadRecover extends PlayerThread {
    Player<String> player;
    List<SPlayer> allPlayers;
    WorldMap<String> map;
    GameInfo gameInfo;
    CyclicBarrier barrier;
    int waitTimeOut;
    List<Player<String>> players;

    //actual player thread in game
    public PlayerThreadRecover(Player<String> player, WorldMap<String> map,
                               GameInfo gameInfo, CyclicBarrier barrier, List<Player<String>> players) {
        this.player = player;
        this.map = map;
        this.gameInfo = gameInfo;
        this.barrier = barrier;
        this.waitTimeOut = WAIT_TIME_OUT;
        this.players = players;
        allPlayers = new ArrayList<>();
        for (Player<String> p : this.players){
            allPlayers.add(new SPlayer(p.getId(), p.getName()));
        }
    }


    @Override
    public void run() {
        try {
            player.send(allPlayers);
            while (!gameInfo.hasFinished()){
                playGame();
                // give main thread some time to process round result
                barrier.await();
            }
        }catch (Exception ignored){
        }
    }


}
