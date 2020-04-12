package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;
import static edu.duke.ece651.risk.shared.Utils.getUnitUpCost;

//have such a design for future use
public class Unit implements Serializable {

    int level;

    //0 represent this unit doesn't have an owner yet
    int ownerId;

    public Unit(int level, int ownerId) {
        if (ownerId<0||!UNIT_BONUS.containsKey(level)){
            throw new IllegalArgumentException("Invalid argument");
        }
        this.level = level;
        this.ownerId = ownerId;
    }

    public int getLevel() {
        return level;
    }

    public int getOwnerId() {
        return ownerId;
    }
}
