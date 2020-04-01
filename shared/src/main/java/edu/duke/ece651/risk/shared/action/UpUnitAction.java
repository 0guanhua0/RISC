package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.util.Map;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;

/**
 * @program: risk
 * @description: this is action class to upgrade
 * @author: Mr.Wang
 * @create: 2020-03-30 21:35
 **/
public class UpUnitAction implements Action{

    String terr;
    int playerId;

    int srcLevel;
    int targetLevel;
    int unitsNum;


    public UpUnitAction(String terrName, int srcLevel, int targetLevel, int playerId, int unitsNum) {
        this.terr = terrName;
        this.srcLevel = srcLevel;
        this.targetLevel = targetLevel;
        this.playerId = playerId;
        this.unitsNum = unitsNum;
    }

    @Override
    public boolean isValid(WorldState worldState) {
        Player<String> player = worldState.getPlayer();
        WorldMap<String> map = worldState.getMap();

        //check if territory name is valid
        if (!map.hasTerritory(terr)){
            return false;
        }
        Territory srcNode = map.getTerritory(terr);
        if (srcNode.canUpUnit(this.unitsNum,this.srcLevel,this.targetLevel)){
            return false;
        }

        //check ownerId
        if (srcNode.getOwner() != playerId){
            return false;
        }

        //check if the target tech level is valid
        if (!UNIT_BONUS.containsKey(targetLevel)||player.getTechLevel()<targetLevel){
            return false;
        }
        return true;
    }

    @Override
    public boolean perform(WorldState worldState) {
        if (!isValid(worldState)){
            throw new IllegalArgumentException("Invalid input argument");
        }
        Territory territory = worldState.getMap().getTerritory(this.terr);
        territory.upUnit(this.unitsNum,this.srcLevel,this.targetLevel);
        return true;
    }
}
