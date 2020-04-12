package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.player.Player;

import java.util.List;

/**
 * @program: risk
 * @description: this is action required for evolution3
 * @author: Chengda Wu(cw402)
 * @create: 2020-04-10 17:13
 **/
public class AllyAction implements Action{
    int allyRequest;

    public AllyAction(int allyRequest) {
        this.allyRequest = allyRequest;
    }

    /**
     * @param worldState: current state of the world
     * @return if this the current player can make a AllyAction, note that it doesn't mean such an ally action will be successful
     */
    @Override
    public boolean isValid(WorldState worldState) {
        Player<String> myPlayer = worldState.getMyPlayer();
        List<Player<String>> players = worldState.getPlayers();

        //check if this player can still make ally during this evolution
        if (myPlayer.hasRecvAlly()){
            return false;
        }
        //check if this player already has ally
        if (myPlayer.hasAlly()){
            return false;
        }

        return true;
    }

    @Override
    public boolean perform(WorldState worldState) {
        if (!this.isValid(worldState)){
            throw new IllegalArgumentException("Invalid input!");
        }
        Player<String> myPlayer = worldState.getMyPlayer();
        myPlayer.setAllyRequest(this.allyRequest);
        Player<String> targetAlly = worldState.getPlayers().get(allyRequest);
        //use the allyRequest as a field inside player class
        myPlayer.setAllyRequest(this.allyRequest);
        //if both has such a request, accept
        if (targetAlly.canAllyWith(myPlayer)){
            myPlayer.allyWith(targetAlly);
        }
        return true;
    }
}
