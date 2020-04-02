package edu.duke.ece651.risk.server;

import java.util.ArrayList;
import java.util.List;

public class UserList {
    List<String> list = new ArrayList<>();

   public void addUser(String userName) {
       list.add(userName);
   }

   public void rmUser(String userName) {
       list.remove(userName);
   }

   public boolean hasUser(String userName) {
       return list.contains(userName);
   }
}
