package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;

public class Army implements Serializable {
    // where this army comes from(e.g. territory name)
    String src;
    // number of units in this army
    int unitNums;

    public Army(int playerID, String src, int unitNums) {
        this.src = src;
        this.unitNums = unitNums;
    }

    public String getSrc() {
        return src;
    }

    public int getUnitNums() {
        return unitNums;
    }
}
