package edu.duke.ece651.risk.shared.ToServerMsg;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @program: risk
 * @description: This class represents actions for user to choose territories at the beginig of the game
 * @author: Chengda Wu
 * @create: 2020-03-16 17:21
 **/
public class ServerSelect implements Serializable {
    //key is the name of territories this player has just chosen
    //value is the number of units this player wants to assign to the corresponding territory
    Map<String,Integer> chosen;
    public ServerSelect(Map<String,Integer> chosen) {
        this.chosen = chosen;
    }


    public boolean isValid(WorldMap<?> worldMap,int validNum){
        int totalNum = 0;
        for (String territory : chosen.keySet()) {
            if (!worldMap.hasFreeTerritory(territory)||chosen.get(territory)<=0){
                return false;
            }
            totalNum += chosen.get(territory);
        }
        return totalNum==validNum;
    }


    public void perform(WorldMap<?> worldMap, int validNum, Player<?> player) throws IllegalArgumentException {
        if (!this.isValid(worldMap,validNum)){
            throw new IllegalArgumentException("This is not a valid input");
        }
        for (String terrName : chosen.keySet()) {
            Territory territory = worldMap.getTerritory(terrName);
            territory.addNUnits(chosen.get(terrName));
            player.addTerritory(territory);
        }
    }

}
