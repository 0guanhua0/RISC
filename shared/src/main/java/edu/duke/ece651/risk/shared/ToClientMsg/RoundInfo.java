package edu.duke.ece651.risk.shared.ToClientMsg;

import edu.duke.ece651.risk.shared.map.WorldMap;

import java.io.Serializable;
import java.util.Map;

public class RoundInfo implements Serializable {
    int roundNum;
    WorldMap<String> map;
    // for now we use player color to represent player name
    Map<Integer, String> idToName;

    public RoundInfo(int roundNum, WorldMap<String> map, Map<Integer, String> idToName){
        this.roundNum = roundNum;
        this.map = map;
        this.idToName = idToName;
    }

    public WorldMap<String> getMap() {
        return map;
    }

    public Map<Integer, String> getIdToName() {
        return idToName;
    }

    public int getRoundNum() {
        return roundNum;
    }
}
