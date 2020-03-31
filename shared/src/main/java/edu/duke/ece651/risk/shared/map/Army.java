package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;
import java.util.Map;

public class Army implements Serializable {
    // where this army comes from(e.g. territory name)
    String src;
    Map<Integer,Integer> levelToNum;



    public Army(int playerID, String src, Map<Integer,Integer> levelToNuml) {
        this.src = src;
        this.levelToNum = levelToNuml;
    }

    public String getSrc() {
        return src;
    }

    public int getUnitNums() {
        return levelToNum.get(0);
    }

}
