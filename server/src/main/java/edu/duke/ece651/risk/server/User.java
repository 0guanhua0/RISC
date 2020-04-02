package edu.duke.ece651.risk.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * each validate user has certain room he has joined
 */
public class User {
    ObjectInputStream in;
    ObjectOutputStream out;

    //class that store the online validated user
    List<Room> roomList = new ArrayList<>();
    String userName;

    public User(String userName, InputStream in, OutputStream out) throws IOException {
        this.userName = userName;
        this.in = new ObjectInputStream(in);
        this.out = new ObjectOutputStream(out);
    }

    //update the room info
    public void addRoom(Room room) {
        roomList.add(room);

    }

    //rm room info
    public void rmRoom(Room room) {
        roomList.add(room);
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public String getUserName() {
        return userName;
    }

    public void send(Object data) throws IOException {
        out.writeObject(data);
        out.flush();
    }

    public Object recv() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

}
