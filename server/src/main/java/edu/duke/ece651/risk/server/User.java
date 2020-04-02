package edu.duke.ece651.risk.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * each validate user has:
 * room has join
 */
public class User {
    ObjectInputStream in;
    ObjectOutputStream out;

    //list store the room user join
    List<Integer> roomList = new ArrayList<>();
    String userName;

    public User(InputStream in, OutputStream out) throws IOException {
        this.in = new ObjectInputStream(in);
        this.out = new ObjectOutputStream(out);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User(String userName, InputStream in, OutputStream out) throws IOException {
        this.in = new ObjectInputStream(in);
        this.out = new ObjectOutputStream(out);
        this.userName = userName;
    }


    //update the room info
    public void addRoom(Integer roomID) {
        roomList.add(roomID);

    }

    //rm room info
    public void rmRoom(Integer roomID) {
        roomList.add(roomID);
    }

    public List<Integer> getRoomList() {
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

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public boolean isInRoom(int roomID) {
        return roomList.contains(roomID);
    }
}
