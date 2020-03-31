package edu.duke.ece651.riskclient.objects;

public class Room {
    private int roomID;
    private String roomName;

    public Room() {
    }

    public Room(String name){
        this.roomName = name;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
