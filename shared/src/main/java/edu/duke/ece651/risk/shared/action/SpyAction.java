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
        //check if the target is valid
        if (targetId-1<0||targetId-1>=worldState.getPlayers().size()||targetId==myPlayer.getId()){
            return false;
        }
        //check if resource is enough
        if (myPlayer.isSpying()||myPlayer.canAffordSpy()){
            return true;
        }else{
            return false;
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
        List<Action> actions = worldState.getPlayers().get(targetId-1).getActions();

        //TODO use communication to replace the standard output here
        System.out.println(actions);
        return true;
    }
}
