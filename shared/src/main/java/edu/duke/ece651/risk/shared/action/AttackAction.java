package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.io.Serializable;

public class AttackAction implements Action, Serializable {
    String src;
    String dest;
    int playerId;
    int unitsNum;

    public AttackAction(String src, String dest, int playerId, int unitsNum) {
        this.src = src.toLowerCase();
        this.dest = dest.toLowerCase();
        this.playerId = playerId;
        this.unitsNum = unitsNum;
    }

    /**
     * validate the attack move
     *
     * @param worldState WorldState object
     * @return true if valid, false if invalid
     */

    @Override
    public boolean isValid(WorldState worldState) {
        WorldMap<String> worldMap = worldState.getMap();
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
        if (src.getUnitsNum() < this.unitsNum) {
            return false;
        }

        //validate dst owns by opponent
        Territory dst = worldMap.getTerritory(this.dest);
        if (dst.getOwner() == this.playerId) {
            return false;
        }

        //validate connection
        return src.getNeigh().contains(dst);
    }


    /**
     * following function perform single attack update, add update to territory map
     *
     * @param worldState WorldState object
     * @return true, if valid
     */
    @Override
    public boolean perform(WorldState worldState) {
        if (!isValid(worldState)) {
            throw new IllegalArgumentException("Invalid attack action!");
        }
        WorldMap<String> worldMap = worldState.getMap();
        // reduce src units
        worldMap.getTerritory(src).lossNUnits(unitsNum);
        // add attack units to target territory's attack buffer
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
