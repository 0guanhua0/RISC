package edu.duke.ece651.risk.server;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
public class UserList {
    @Id
    int id;
    @Embedded
    List<User> userList;

    public UserList() {
        this.id = 0;
        this.userList = new ArrayList<>();
    }

    public void addUser(User user) {
        userList.add(user);
    }

    public void rmUser(User user) {
        userList.remove(user);
    }


    public boolean hasUser(String userName) {
        for (User u : userList) {
            if (u.getUserName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public User getUser(String userName) {
        for (User u : userList) {
            if (u.getUserName().equals(userName)) {
                return u;
            }
        }

        return null;
    }

    public List<User> getUserList() {
        return userList;
    }
}
