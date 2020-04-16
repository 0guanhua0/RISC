package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;
import static edu.duke.ece651.risk.shared.Utils.getUnitUpCost;

//have such a design for future use
public class Unit implements Serializable {
    private static final long serialVersionUID = 13L;

    int level;

    public Unit(int level) {
        if (!UNIT_BONUS.containsKey(level)){
            throw new IllegalArgumentException("Invalid argument");
        }
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
