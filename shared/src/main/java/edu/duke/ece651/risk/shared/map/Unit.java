package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;
import static edu.duke.ece651.risk.shared.Utils.getUnitUpCost;

public class Unit implements Serializable {

    int level;

    public Unit() {
        this.level = 0;
    }

    public int getLevel() {
        return level;
    }

    /**
     * this method represent logically whether a unit can be upgraded to target level
     * note that it doesn't take resource into consideration
     * @param targetLevel: the target technology level that we want to upgrade the current unit to
     * @return whether such an update is feasible
     */
    public boolean canUpTo(int targetLevel){
        if(!UNIT_BONUS.containsKey(targetLevel)){
            return false;
        }else if(targetLevel<=level){
            return false;
        }else{
            return true;
        }
    }

    /**
     * @param targetLevel: target level for this unit
     */
    public void upToLevel(int targetLevel){
        if (this.canUpTo(targetLevel)){
            throw new IllegalArgumentException("Input argument is invalid");
        }
        this.level = targetLevel;
    }
}
