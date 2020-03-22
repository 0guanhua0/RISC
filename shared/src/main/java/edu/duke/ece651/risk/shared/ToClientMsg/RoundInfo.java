package edu.duke.ece651.risk.shared.ToClientMsg;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
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

    //TODO have a better way to deep copy a map and put it into WorldMap class
    void copyMap(WorldMap<String> oldMap){
        this.map = new MapDataBase<String>().getMap(oldMap.getName());
        for (Territory t : oldMap.getAtlas().values()){
            this.map.getTerritory(t.getName()).setOwner(t.getOwner());
            this.map.getTerritory(t.getName()).addNUnits(t.getUnitsNum());
        }
    }
}
