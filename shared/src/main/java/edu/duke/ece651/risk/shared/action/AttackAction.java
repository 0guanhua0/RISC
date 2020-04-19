package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.Army;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.duke.ece651.risk.shared.Constant.UNIT_NAME;


public class AttackAction implements Action, Serializable {
    private static final long serialVersionUID = 2L;

    String src;
    String dest;
    int playerId;
    //key is technology level of corresponding units, value is number of units
    Map<Integer,Integer> levelToNum;
    int unitsNum;


    public AttackAction(String src, String dest, int playerId, Map<Integer,Integer> levelToNum) {
        this.src = src;
        this.dest = dest;
        this.playerId = playerId;
        this.levelToNum = levelToNum;
        this.unitsNum = levelToNum.values().stream().mapToInt(a -> a).sum();
    }

    public AttackAction(String src, String dest, int playerId, int unitsNum) {
        this.src = src.toLowerCase();
        this.dest = dest.toLowerCase();
        this.playerId = playerId;
        this.levelToNum = new HashMap<Integer, Integer>(){{
            put(0,unitsNum);
        }};
        this.unitsNum = unitsNum;
    }


    /**
     * validate the attack move
     * @param worldState WorldState object
     * @return true if valid, false if invalid
     */

    @Override
    public boolean isValid(WorldState worldState) {
        WorldMap<String> worldMap = worldState.getMap();
        Player<String> player = worldState.getMyPlayer();

        //validate src & dst & unit num
        if (!worldMap.hasTerritory(this.src) || !worldMap.hasTerritory(this.dest)) {
            return false;
        }

        //validate src own by player
        Territory src = worldMap.getTerritory(this.src);
        if (src.getOwner() != this.playerId) {
            return false;
        }

        //validate dst owns by opponent
        Territory dst = worldMap.getTerritory(this.dest);
        if (dst.getOwner() == this.playerId) {
            return false;
        }

        for (Map.Entry<Integer, Integer> entry : this.levelToNum.entrySet()) {
            //validate src has enough unit
            if (!src.canLoseUnits(entry.getValue(),entry.getKey())) {
                return false;
            }
        }

        //validate food storage
        int foodStorage = player.getFoodNum();
        //An attack order now costs 1 food per unit attacking.
        if (foodStorage<unitsNum){
            return false;
        }

        //validate connection
        return src.getNeigh().contains(dst);
    }


    /**
     * following function perform single attack update, add update to territory map
     * @param worldState WorldState object
     * @return true, if valid
     */
    @Override
    public boolean perform(WorldState worldState) {
        if (!isValid(worldState)) {
            throw new IllegalArgumentException("Invalid attack action!");
        }
        WorldMap<String> worldMap = worldState.getMap();
        Player<String> myPlayer = worldState.getMyPlayer();


        //use some food to finish this attack operation
        int foodCost = unitsNum;
        myPlayer.useFood(unitsNum);

        int destOwner = worldMap.getTerritory(dest).getOwner();
        //break the alliance if trying to attack an ally
        List<Player<String>> players = worldState.getPlayers();
        if (myPlayer.hasAlly()&&destOwner==myPlayer.getAlly().getId()){
            myPlayer.ruptureAlly();
        }

        for (Map.Entry<Integer, Integer> entry : levelToNum.entrySet()) {
            // reduce src units
            worldMap.getTerritory(src).loseUnits(entry.getValue(),entry.getKey());
        }

        // add attack units to target territory's attack buffer
        worldMap.getTerritory(dest).addAttack(myPlayer, new Army(playerId, src,levelToNum));

        myPlayer.addAction(this);
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AttackAction) {
            AttackAction attackAction1 = (AttackAction) obj;
            return attackAction1.src.equals(this.src) && attackAction1.dest.equals(this.dest) && attackAction1.playerId == this.playerId;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Attack(").append(this.src).append("---->").append(this.dest);
        for (Map.Entry<Integer, Integer> entry : this.levelToNum.entrySet()) {
            String name = UNIT_NAME.get(entry.getKey());
            int number = entry.getValue();
            sb.append(", ").append(number).append(" ").append(name);
        }
        sb.append(")");
        return sb.toString();
    }
}
