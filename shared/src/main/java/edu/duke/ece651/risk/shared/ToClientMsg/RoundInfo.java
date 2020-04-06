package edu.duke.ece651.risk.shared.ToClientMsg;

import edu.duke.ece651.risk.shared.Utils;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public class RoundInfo implements Serializable {

    private static final long serialVersionUID = 15L;

    int roundNum;
    WorldMap<String> map;
    // for now we use player color to represent player name
    Map<Integer, String> idToName;
    // current player object(record the resource information)
    Player<String> player;

    public RoundInfo(int roundNum, WorldMap<String> map, Map<Integer, String> idToName, Player<String> player) throws IOException, ClassNotFoundException {
        this.roundNum = roundNum;
        this.idToName = idToName;
        this.player = player;
        this.map = Utils.clone(map);
        this.player = Utils.clone(player);
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

    public Player<String> getPlayer(){
        return player;
    }

}
