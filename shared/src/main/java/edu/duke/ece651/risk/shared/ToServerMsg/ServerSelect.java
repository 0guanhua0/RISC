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
    private static final long serialVersionUID = 23L;

    //key is the name of territories this player has just chosen
    //value is the number of units this player wants to assign to the corresponding territory
    Map<String,Integer> chosen;
    public ServerSelect(Map<String,Integer> chosen) {
        this.chosen = chosen;
    }

    //this method returns number of units inside a territory
    public int getUnitsNum(String terrName){
        if (!chosen.containsKey(terrName)){
            throw new IllegalArgumentException("Input key doesn't exist ");
        }
        return chosen.get(terrName);
    }

    public Set<String> getAllName(){
        return chosen.keySet();
    }

    public boolean isValid(WorldMap<?> worldMap, int validUnitsNum, int validTerrNum){
        if (chosen.size() != validTerrNum){
            return false;
        }
        int totalNum = 0;
        for (String territory : chosen.keySet()) {
            if (!worldMap.hasFreeTerritory(territory) || chosen.get(territory) <= 0){
                return false;
            }
            totalNum += chosen.get(territory);
        }
        return totalNum == validUnitsNum;
    }


    public void perform(WorldMap<?> worldMap, int validUnitsNum,int validTerrNum, Player<?> player) throws IllegalArgumentException {
        if (!isValid(worldMap,validUnitsNum,validTerrNum)){
            throw new IllegalArgumentException("This is not a valid input.");
        }
        for (String terrName : chosen.keySet()) {
            Territory territory = worldMap.getTerritory(terrName);
            territory.addBasicUnits(chosen.get(terrName));
            player.addTerritory(territory);
        }
    }

}
