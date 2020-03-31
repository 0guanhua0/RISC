package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

/**
 * @program: risk
 * @description: this is action class to upgrade
 * @author: Mr.Wang
 * @create: 2020-03-30 21:35
 **/
public class UpUnitAction implements Action{

    String terrName;
    int srcLevel;
    int targetLevel;


    public UpUnitAction(String terrName, int srcLevel, int targetLevel) {
        this.terrName = terrName;
        this.srcLevel = srcLevel;
        this.targetLevel = targetLevel;
    }

    @Override
    public boolean isValid(WorldState worldState) {
        Player<String> player = worldState.getPlayer();
        WorldMap<String> map = worldState.getMap();
        return false;
    }

    @Override
    public boolean perform(WorldState worldState) {
        return false;
    }
}
