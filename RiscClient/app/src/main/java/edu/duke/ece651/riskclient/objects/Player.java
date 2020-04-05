package edu.duke.ece651.riskclient.objects;

import java.io.Serializable;

public class Player implements Serializable {
    int id;
    String name;
    String password;

    public Player(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public Player(int id, String name, String password){
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
