package edu.duke.ece651.risk.server;

import java.util.ArrayList;
import java.util.List;

public class UserList {
    List<User> userList = new ArrayList<>();

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
}
