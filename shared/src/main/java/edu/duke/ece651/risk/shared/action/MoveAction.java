package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MoveAction implements Action, Serializable {
    String src;
    String dest;
    int playerId;
    int unitsNum;

    //key is technology level of corresponding units, value is number of units
    Map<Integer,Integer> levelToNum;


    public MoveAction(String src, String dest,
                      int playerId, int unitsNum) {
        this.src = src.toLowerCase();
        this.dest = dest.toLowerCase();
        this.playerId = playerId;
        this.levelToNum = new HashMap<>(){{
            put(0,unitsNum);
        }};
        this.unitsNum = unitsNum;
    }

    //TODO make sure that client part use this moveAction instead of previous one
    public MoveAction(String src, String dest,
                      int playerId,Map<Integer,Integer> levelToNum) {
        this.src = src.toLowerCase();
        this.dest = dest.toLowerCase();
        this.playerId = playerId;
        this.levelToNum = levelToNum;
        this.unitsNum = levelToNum.values().stream().mapToInt(a -> a).sum();

    }


    @Override
    public boolean isValid(WorldState worldState) {
        WorldMap<String> map = worldState.getMap();
        Player<String> player = worldState.getPlayer();

        //check if two input names are valid
        if (!map.hasTerritory(src) || !map.hasTerritory(dest)){
            return false;
        }
        Territory srcNode = map.getTerritory(src);

        int dist = map.getMinCtrlDist(src,dest);

        //check ownerId
        if (srcNode.getOwner() != playerId){
            return false;
        }
        //check whether there is no such path under the control of current user
        if (Integer.MAX_VALUE==dist){
            return false;
        }

        if (player.getFoodNum()<dist*unitsNum){
            return false;
        }
        for (Map.Entry<Integer, Integer> entry : this.levelToNum.entrySet()) {
            if (!srcNode.canLoseUnits(entry.getValue(),entry.getKey())) {
                return false;
            }
        }
        return true;

    }
    @Override
    public boolean perform(WorldState worldState) {
        //perform the real action
        if (!isValid(worldState)){
            throw new IllegalArgumentException("Invalid move action!");
        }
        WorldMap<String> map = worldState.getMap();
        Player<String> player = worldState.getPlayer();
        //update the state of src and target territory
        Territory srcNode = map.getTerritory(src);
        Territory destNode = map.getTerritory(dest);
        for (Map.Entry<Integer, Integer> entry : levelToNum.entrySet()) {
            srcNode.loseUnits(entry.getValue(),entry.getKey());
            destNode.addUnits(entry.getValue(),entry.getKey());
        }
        //update the food storage
        int foodCost = map.getMinCtrlDist(src,dest)*unitsNum;
        player.useFood(foodCost);

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }else if (!(o instanceof MoveAction)){
            return false;
        }else{
            MoveAction that = (MoveAction) o;
            return playerId == that.playerId &&
                    unitsNum == that.unitsNum &&
                    src.equals(that.src) &&
                    dest.equals(that.dest);
        }
    }
}
