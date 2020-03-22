package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToClientMsg.RoundInfo;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.util.concurrent.CyclicBarrier;

import static edu.duke.ece651.risk.shared.Constant.*;
import static edu.duke.ece651.risk.shared.Constant.INVALID_ACTION;

public class PlayerThread extends Thread {

    Player<String> player;
    RoundInfo roundInfo;
    WorldMap<String> map;
    CyclicBarrier barrier;

    public PlayerThread(Player<String> player, RoundInfo roundInfo, WorldMap<String> map, CyclicBarrier barrier) {
        this.player = player;
        this.roundInfo = roundInfo;
        this.map = map;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            // tell client the round info
            // 1. latest WorldMap
            // 2. mapping between id and color
            // 3. round number
            player.send(roundInfo);
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
        }catch (Exception ignored){
        }

    }
}
