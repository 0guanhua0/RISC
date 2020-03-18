package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.io.Serializable;
import java.util.Objects;

public class MoveAction implements Action, Serializable {
    String src;
    String dest;
    int playerId;
    int unitsNum;
    public MoveAction(String src, String dest,
                      int playerId, int unitsNum) {
        this.src = src;
        this.dest = dest;
        this.playerId = playerId;
        this.unitsNum = unitsNum;
    }


    @Override
    public boolean isValid(WorldMap<?> map) {
        //check if two input names are valid
        if (!map.hasTerritory(src)||!map.hasTerritory(dest)){
            return false;
        }
        Territory srcNode = map.getTerritory(src);
        Territory destNode = map.getTerritory(dest);
        if (srcNode.getOwner()!=playerId){
            return false;
        }else if (!srcNode.hasPathTo(destNode)){
            return false;
        }else if (srcNode.getUnitsNum()<=unitsNum||unitsNum<0){
            return false;
        }else{
            return true;
        }
    }
    @Override
    public boolean perform(WorldMap<?> map) {
        //perform the real action
        if (!isValid(map)){
            throw new IllegalArgumentException("Invalid move action!");
        }
        //update the state of src and target territory
        Territory srcNode = map.getTerritory(src);
        Territory destNode = map.getTerritory(dest);
        srcNode.lossNUnits(unitsNum);
        destNode.addNUnits(unitsNum);
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
