package edu.duke.ece651.risk.server;

import java.util.ArrayList;
import java.util.List;

/**
 * each validate user has:
 * room has join
 */
public class User {
    //list store the room user join
    List<Integer> roomList = new ArrayList<>();
    String userName;
    String userPassword;

    public User(String userName, String userPassword) {
        this.userPassword = userPassword;
        this.userName = userName;
    }

    //update the room info
    public void addRoom(Integer roomID) {
        roomList.add(roomID);

    }

    //rm room info
    public void rmRoom(Integer roomID) {
        roomList.remove(roomID);
    }

    public List<Integer> getRoomList() {
        return roomList;
    }

    public boolean isInRoom(int roomID) {
        return roomList.contains(roomID);
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }
}
