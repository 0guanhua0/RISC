package edu.duke.ece651.risk.server;

import java.util.ArrayList;
import java.util.List;

public class RoomPlayerJoined {
    List<Room> list = new ArrayList<>();

    public void addRoom(Room room) {
        list.add(room);
    }


    public void rmRoom(Room room) {
        list.remove(room);
    }

    public List<Room> getList() {
        return list;
    }
}
