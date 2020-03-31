package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.Army;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.Serializable;

public class AttackAction implements Action, Serializable {
    String src;
    String dest;
    int playerId;
    int unitsNum;
    int unitsLevel;

    public AttackAction(String src, String dest, int playerId, int unitsNum, int unitsLevel) {
        this.src = src;
        this.dest = dest;
        this.playerId = playerId;
        this.unitsNum = unitsNum;
        this.unitsLevel = unitsLevel;
    }

    public AttackAction(String src, String dest, int playerId, int unitsNum) {
        this.src = src.toLowerCase();
        this.dest = dest.toLowerCase();
        this.playerId = playerId;
        this.unitsNum = unitsNum;
        this.unitsLevel = 0;
    }


    /**
     * validate the attack move
     * @param worldState WorldState object
     * @return true if valid, false if invalid
     */

    @Override
    public boolean isValid(WorldState worldState) {
        WorldMap<String> worldMap = worldState.getMap();
        Player<String> player = worldState.getPlayer();

        //validate src & dst & unit num
        if (!worldMap.hasTerritory(this.src) || !worldMap.hasTerritory(this.dest) || this.unitsNum <= 0) {
            return false;
        }

        //validate src own by player
        Territory src = worldMap.getTerritory(this.src);
        if (src.getOwner() != this.playerId) {
            return false;
        }

        //validate src has enough unit
        if (!src.canLoseUnits(this.unitsNum,this.unitsLevel)) {
            return false;
        }

        //validate dst owns by opponent
        Territory dst = worldMap.getTerritory(this.dest);
        if (dst.getOwner() == this.playerId) {
            return false;
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
        Player<String> player = worldState.getPlayer();

        //use some food to finish this attack operation
        int foodCost = unitsNum;
        player.useFood(unitsNum);

        // reduce src units
        worldMap.getTerritory(src).loseUnits(unitsNum,unitsLevel);
        // add attack units to target territory's attack buffer
        //TODO note that this part of attack action should be changed
        worldMap.getTerritory(dest).addAttack(playerId, new Army(playerId, src, unitsNum));

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AttackAction) {
            AttackAction attackAction1 = (AttackAction) obj;
            return attackAction1.src.equals(this.src) && attackAction1.dest.equals(this.dest) && attackAction1.unitsNum == this.unitsNum && attackAction1.playerId == this.playerId;
        }
        return false;
    }
}
