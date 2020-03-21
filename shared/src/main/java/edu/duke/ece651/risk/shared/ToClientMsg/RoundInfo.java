package edu.duke.ece651.risk.shared.ToClientMsg;

import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundInfo implements Serializable {
    WorldMap<String> map;
    Map<Integer, String> idToColor;

    public RoundInfo(WorldMap<String> map, List<Player<String>> players){
        this.map = map;
        this.idToColor = new HashMap<>(players.size());
        for (Player<String> player : players){
            idToColor.put(player.getId(), player.getColor());
        }
    }

    public WorldMap<String> getMap() {
        return map;
    }

    public Map<Integer, String> getIdToColor() {
        return idToColor;
    }
}
