package edu.duke.ece651.risk.shared.action;


import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.Utils;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SpyAction implements Action {
    private static final long serialVersionUID = 26L;

    int targetId;
    String targetName;

    public SpyAction(int targetId) {
        this.targetId = targetId;
        this.targetName = "";
    }

    public SpyAction(int targetId, String targetName){
        this.targetId = targetId;
        this.targetName = targetName;
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

        // pure magic, the same magic of round info, we need to clone a new object
        // otherwise client will always receive 0
        ArrayList<Action> a = new ArrayList<>(actions);
        try {
            myPlayer.send(Utils.clone(a));
        } catch (IOException | ClassNotFoundException ignored) {
        }
        //TODO use communication to replace the standard output here
        System.out.println(actions);

        return true;
    }

    @Override
    public String toString() {
        if (targetName.isEmpty()){
            return String.format("You choose to spy the Player %d", targetId);
        }else {
            return String.format("You choose to spy the Player %s", targetName);
        }
    }
}
