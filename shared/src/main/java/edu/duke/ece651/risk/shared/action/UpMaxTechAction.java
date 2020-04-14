package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.Serializable;

/**
 * @program: risk
 * @description: this class is used to represent up maximum technology level action
 * @author: Chengda Wu
 * @create: 2020-03-30 17:33
 **/
public class UpMaxTechAction implements Action, Serializable {
    private static final long serialVersionUID = 3L;

    public UpMaxTechAction(){}
    /**
     * Check the validation of current action.
     * @return true if valid
     */
    public boolean isValid(WorldState worldState){
        Player<String> player = worldState.getMyPlayer();
        return player.canUpMaxTech();
    }

    /**
     * Perform the action.
     * @return true if perform successful
     */
    public boolean perform(WorldState worldState){
        if (!isValid(worldState)){
            throw new IllegalArgumentException("Invalid action");
        }
        Player<String> player = worldState.getMyPlayer();
        player.upMaxTech();
        return true;
    }

    @Override
    public String toString() {
        return "You successfully upgrade your maximum technology level!";
    }
}
