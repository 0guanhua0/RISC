package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Army implements Serializable {
    private static final long serialVersionUID = 7L;

    // where this army comes from(e.g. territory name)
    String src;
    Map<Integer,Integer> levelToNum;
    int playerId;



    public Army(int playerID, String src, Map<Integer,Integer> levelToNuml) {
        this.src = src;
        this.levelToNum = levelToNuml;
        this.playerId = playerID;
    }

    public Army(int playerID, String src, int unitNum) {
        this.src = src;
        this.levelToNum = new HashMap<Integer, Integer>(){{
            put(0,unitNum);
        }};
    }

    public String getSrc() {
        return src;
    }

    public int getUnitNums(int level) {
        return levelToNum.get(level);
    }

    public Map<Integer, Integer> getTroops() {
        return levelToNum;
    }

}
