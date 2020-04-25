package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.Unit;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.util.HashMap;
import java.util.Map;

import static edu.duke.ece651.risk.shared.Constant.UNIT_NAME;

public class MoveAction implements Action {
    private static final long serialVersionUID = 4L;

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
        this.levelToNum = new HashMap<Integer, Integer>(){{
            put(0,unitsNum);
        }};
        this.unitsNum = unitsNum;
    }

    //TODO make sure that client part use this moveAction instead of previous one
    public MoveAction(String src, String dest,
                      int playerId, Map<Integer,Integer> levelToNum) {
        this.src = src.toLowerCase();
        this.dest = dest.toLowerCase();
        this.playerId = playerId;
        this.levelToNum = levelToNum;
        this.unitsNum = levelToNum.values().stream().mapToInt(a -> a).sum();
    }

    @Override
    public boolean isValid(WorldState worldState) {
        WorldMap<String> map = worldState.getMap();
        Player<String> player = worldState.getMyPlayer();

        //check if two input names are valid
        if (!map.hasTerritory(src) || !map.hasTerritory(dest)){
            return false;
        }
        Territory srcNode = map.getTerritory(src);

        int dist = map.getMinCtrlDist(src,dest);
        //check if such territory is owned by current player or the ally
        if (srcNode.getOwner()!= player.getId()&&srcNode.getAllyId()!=player.getId()){
            return false;
        }
        //check whether there is such a path under the control of current user
        if (Integer.MAX_VALUE==dist){
            return false;
        }

        if (player.getFoodNum()<dist*unitsNum){
            return false;
        }

        if (player.getId()==srcNode.getOwner()){
            for (Map.Entry<Integer, Integer> entry : this.levelToNum.entrySet()) {
                if (!srcNode.canLoseUnits(entry.getValue(),entry.getKey())) {
                    return false;
                }
            }
        }else{
            for (Map.Entry<Integer, Integer> entry : this.levelToNum.entrySet()) {
                if (!srcNode.canLoseAllyUnits(entry.getValue(),entry.getKey())) {
                    return false;
                }
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
        Player<String> player = worldState.getMyPlayer();
        //update the state of src and target territory
        Territory srcNode = map.getTerritory(src);
        Territory destNode = map.getTerritory(dest);
        for (Map.Entry<Integer, Integer> entry : levelToNum.entrySet()) {
            int num = entry.getValue();
            int level = entry.getKey();
            assert(destNode.getOwner()==player.getId()||destNode.getAllyId()==player.getId());
            if (srcNode.getOwner()==player.getId()){
                srcNode.loseUnits(num, level);
            }else {
                srcNode.loseAllyUnits(num, level);
            }
            if (destNode.getOwner()==player.getId()){
                destNode.addUnits(num, level);
            }else{
                for (int i=0;i<num;i++){
                    destNode.addAllyUnit(new Unit(level));
                }
            }
        }
        //update the food storage
        int foodCost = map.getMinCtrlDist(src,dest)*unitsNum;
        player.useFood(foodCost);
        player.addAction(this);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Move(").append(this.src).append("---->").append(this.dest);
        for (Map.Entry<Integer, Integer> entry : this.levelToNum.entrySet()) {
            String name = UNIT_NAME.get(entry.getKey());
            int number = entry.getValue();
            sb.append(", ").append(number).append(" ").append(name);
        }
        sb.append(")");
        return sb.toString();
    }
}
