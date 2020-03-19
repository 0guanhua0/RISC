package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.io.Serializable;

public class AttackAction implements Action, Serializable {
    String src;
    String dest;
    int player_id;
    int unitsNum;

    public AttackAction(String src, String dest, int player_id, int unitsNum) {
        this.src = src;
        this.dest = dest;
        this.player_id = player_id;
        this.unitsNum = unitsNum;
    }

    /**
     * validate the attack move
     *
     * @param worldMap
     * @return true if valid, false if invalid
     */

    @Override
    public boolean isValid(WorldMap<?> worldMap) {
        //validate src & dst & unitnum
        if (!worldMap.hasTerritory(this.src) || !worldMap.hasTerritory(this.dest) || this.unitsNum <= 0) {
            return false;
        }

        //validate src own by player
        Territory src = worldMap.getTerritory(this.src);
        if (src.getOwner() != this.player_id) {
            return false;
        }

        //validate src has enough unit
        if (src.getUnitsNum() < this.unitsNum) {
            return false;
        }
        //validate dst owns by opponent

        Territory dst = worldMap.getTerritory(this.dest);
        if (dst.getOwner() == this.player_id) {
            return false;
        }

        return true;
    }

    /**
     * following function perform the actual attack update
     *
     * @param worldMap
     * @return true, if valid
     */
    @Override
    public boolean perform(WorldMap<?> worldMap) {
        //validate
        if (!isValid(worldMap)) {
            throw new IllegalArgumentException("Invalid attack action!");
        }

        //perform actual action

        //reduce src unit num
        Territory src = worldMap.getTerritory(this.src);
        src.lossNUnits(this.unitsNum);

        //check dst will change owner or not
        Territory dst = worldMap.getTerritory(this.dest);

        //dst has less unit
        if (dst.getUnitsNum() <= this.unitsNum ) {
            int newUnit = this.unitsNum - dst.getUnitsNum();

            //switch owner & reset unit
            dst.setOwner(this.player_id);
            dst.lossNUnits(dst.getUnitsNum());
            dst.addNUnits(newUnit);
        }

        //dst has more/equal unit
        else {
            dst.lossNUnits(this.unitsNum);
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AttackAction) {
            AttackAction attackAction = (AttackAction) obj;
            return attackAction.src.equals(this.src) && attackAction.dest.equals(this.dest) && attackAction.unitsNum == this.unitsNum && attackAction.player_id == this.player_id;
        }
        return false;
    }
}
