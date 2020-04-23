package edu.duke.ece651.risk.shared;

import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class stands for one room of the game.(mostly for evolution 2)
 */
public class RoomInfo implements Serializable {
    private static final long serialVersionUID = 24L;

    // ID of the room
    int roomID;
    // ID of current player
    int playerID;
    String roomName;
    // all players inside
    List<String> players;
    // map this room playing
    WorldMap<String> map;

    public RoomInfo(int roomID, String roomName) {
        this.roomID = roomID;
        this.roomName = roomName;
    }

    public RoomInfo(int roomID, String roomName, WorldMap<String> map, List<Player<String>> players) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.map = map;
        this.players = new ArrayList<>();
        for (Player<String> player : players){
            this.players.add(player.getName());
        }
    }

    public int getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getPlayerCnt() {
        return players.size();
    }

    public int getPlayerNeedTotal(){
        return map.getColorList().size();
    }

    public List<String> getPlayerNames(){
        return players;
    }

    public String getMapName(){
        return map.getName();
    }

    public String getDetailInfo(){
        return String.format("(%d players needed, %d players inside, playing map \"%s\")", getPlayerNeedTotal(), getPlayerCnt(), getMapName());
    }

    public boolean hasStarted(){
        return map.getColorList().size() == players.size();
    }
}
