package edu.duke.ece651.risk.shared.ToClientMsg;

import edu.duke.ece651.risk.shared.Utils;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public class RoundInfo implements Serializable {
    int roundNum;
    WorldMap<String> map;
    // for now we use player color to represent player name
    Map<Integer, String> idToName;

    public RoundInfo(int roundNum, WorldMap<String> map, Map<Integer, String> idToName) throws IOException, ClassNotFoundException {
        this.roundNum = roundNum;
        this.idToName = idToName;
        copyMap(map);
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

    //TODO based on our previous experience, I am not sure whether using ObjectStream will bring some unexpected result
    //but it's necessary to have an automatic deep copy method, otherwise, every time we change the field of WorldMap,
    //we'll need to go back and change this method
    void copyMap(WorldMap<String> oldMap) throws IOException, ClassNotFoundException {
        WorldMap<String> clone = Utils.clone(oldMap);
        this.map = clone;
    }


}
