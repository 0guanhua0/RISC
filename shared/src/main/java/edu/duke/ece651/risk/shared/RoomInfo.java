package edu.duke.ece651.risk.shared;

import java.io.Serializable;

/**
 * This class stands for one room of the game.(mostly for evolution 2)
 */
public class RoomInfo implements Serializable {
    int roomID;
    String roomName;

    public RoomInfo(int roomID, String roomName) {
        this.roomID = roomID;
        this.roomName = roomName;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }
}
