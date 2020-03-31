package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;

public class Army implements Serializable {
    // where this army comes from(e.g. territory name)
    String src;
    // number of units in this army
    int unitNums;
    //technology level of these units
    int level;




    public Army(int playerID, String src, int unitNums,int level) {
        this.src = src;
        this.unitNums = unitNums;
        this.level = level;
    }

    public String getSrc() {
        return src;
    }

    public int getUnitNums() {
        return unitNums;
    }
}
