package edu.duke.ece651.risk.shared.action;


import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.player.Player;

import java.util.List;

public class SpyAction implements Action{

    int targetId;

    public SpyAction(int targetId) {
        this.targetId = targetId;
    }

    @Override
    public boolean isValid(WorldState worldState) {
        List<Player<String>> players = worldState.getPlayers();
        Player<String> myPlayer = worldState.getMyPlayer();
        //check if is currently spying(assume the second spy action will be rendered invalid)
        if (myPlayer.isSpying()){
            return true;
        }
        //if not spying check if it has enough resources
        if (!myPlayer.canAffordSpy()||targetId<0||targetId>=worldState.getPlayers().size()){
            return false;
        } else{
            return true;
        }
    }

    @Override
    public boolean perform(WorldState worldState) {
        if (!this.isValid(worldState)){
            throw new IllegalArgumentException("Invalid action!");
        }
        Player<String> myPlayer = worldState.getMyPlayer();
        //lose resource and update state only at the first time
        if (!myPlayer.isSpying()){
            //subtract the tech resource
            myPlayer.useTech(Constant.SPY_COST);
            //set isSpying to be true
            myPlayer.setIsSpying();
        }
        List<Action> actions = worldState.getPlayers().get(targetId).getActions();
        //TODO communication logic here
        return true;
    }
}
