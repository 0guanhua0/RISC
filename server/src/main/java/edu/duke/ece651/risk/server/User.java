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

    public User(String userName) {
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

    /**
     * io
     *
     * @param data recv
     */
    public void send(Object data) {
        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException ignored) {

        }
    }

    public Object recv() throws IOException, ClassNotFoundException {

        Object object = new Object();
        try {
            object = in.readObject();
        } catch (IOException ignored) {

        }
        return object;


    }

    public void setIn(InputStream in) throws IOException {
        this.in = new ObjectInputStream(in);
    }

    public void setOut(OutputStream out) throws IOException {
        this.out = new ObjectOutputStream(out);
    }
}
