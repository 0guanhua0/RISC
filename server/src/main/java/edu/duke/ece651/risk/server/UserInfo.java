package edu.duke.ece651.risk.server;

import java.util.concurrent.ConcurrentHashMap;

public class UserInfo {
    //added room
    //class that store the online validated player
    private ConcurrentHashMap<String, RoomPlayerJoined> userInfo= new ConcurrentHashMap<>();


    //update the room info
    public void addRoom(String userName, Room room) {
        RoomPlayerJoined roomPlayerJoined = userInfo.get(userName);
        roomPlayerJoined.addRoom(room);
        userInfo.replace(userName, roomPlayerJoined);


    }

    //rm room info
    public void rmRoom(String userName, Room room) {
        RoomPlayerJoined roomPlayerJoined = userInfo.get(userName);
        roomPlayerJoined.rmRoom(room);
        userInfo.replace(userName, roomPlayerJoined);

    }

    //return room info
    public ConcurrentHashMap<String, RoomPlayerJoined> getUserInfo() {
        return userInfo;
    }
}
