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

    //below the id of the player that you want ally to ally with at this round
    int allyRequest;

    public AllyAction(int allyRequest) {
        this.allyRequest = allyRequest;
    }

    /**
     * it's worth noting that, an ally action is valid doesn't mean it will take effect final
     * for example, it's valid for player A to make an ally action with B as long as A herself doesn't have an ally now
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
        Player<String> targetAlly = worldState.getPlayers().get(allyRequest-1);
        //if both has such a request, accept
        if (targetAlly.canAllyWith(myPlayer)){
            myPlayer.allyWith(targetAlly);
        }
        return true;
    }
}
