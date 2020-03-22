package edu.duke.ece651.risk.shared.action;

import java.io.Serializable;

public class Army implements Serializable {
    // the owner of this army(not used in current version)
    int playerID;
    // where this army comes from(e.g. territory name)
    String src;
    // number of units in this army
    int unitNums;

    public Army(int playerID, String src, int unitNums) {
        this.playerID = playerID;
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
