package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Army implements Serializable {
    // where this army comes from(e.g. territory name)
    String src;
    Map<Integer,Integer> levelToNum;



    public Army(int playerID, String src, Map<Integer,Integer> levelToNuml) {
        this.src = src;
        this.levelToNum = levelToNuml;
    }
    public Army(int playerID, String src, int unitNum) {
        this.src = src;
        this.levelToNum = new HashMap<>(){{
            put(0,unitNum);
        }};
    }

    public String getSrc() {
        return src;
    }

    public int getUnitNums() {
        return levelToNum.get(0);
    }

    public Map<Integer, Integer> getTroops() {
        return levelToNum;
    }

}
