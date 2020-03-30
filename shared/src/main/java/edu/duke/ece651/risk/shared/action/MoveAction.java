package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.Serializable;

public class MoveAction implements Action, Serializable {
    String src;
    String dest;
    int playerId;
    int unitsNum;


    public MoveAction(String src, String dest,
                      int playerId, int unitsNum) {
        this.src = src.toLowerCase();
        this.dest = dest.toLowerCase();
        this.playerId = playerId;
        this.unitsNum = unitsNum;
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

        //TODO note that I should test for whether the player is able to move to an uncontrolled area
        if (srcNode.getOwner() != playerId){
            System.out.println(1);
            return false;
        }else if (srcNode.getUnitsNum() < unitsNum || unitsNum <= 0){
            System.out.println(2);
            return false;
        }else if (Integer.MAX_VALUE==dist){//when there is no such path under the control of current user
            System.out.println(3);
            return false;
        }else if (player.getFoodNum()<dist*unitsNum){
            System.out.println(4);
            return false;
        }else {
            return true;
        }
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
        srcNode.lossNUnits(unitsNum);
        destNode.addNUnits(unitsNum);
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
