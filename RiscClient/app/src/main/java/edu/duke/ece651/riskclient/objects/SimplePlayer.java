package edu.duke.ece651.riskclient.objects;

import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;

public class SimplePlayer implements Serializable, IUser {
    private int id;
    private String name;
    transient private String password;

    public SimplePlayer(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public SimplePlayer(int id, String name, String password){
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public int getIdInt() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return null;
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
