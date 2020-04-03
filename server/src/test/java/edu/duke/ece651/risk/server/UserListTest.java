package edu.duke.ece651.risk.server;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static edu.duke.ece651.risk.shared.Mock.*;
import static org.junit.jupiter.api.Assertions.*;

class UserListTest {

    @Test
    void addRmUser() throws IOException {
        UserList userList = new UserList();
        User user1 = new User("user1", setupMockInput(new ArrayList<Object>()), new ByteArrayOutputStream());

        assertFalse(userList.hasUser(user1.userName));
        userList.addUser(user1);
        assertTrue(userList.hasUser(user1.getUserName()));

        User user3 = userList.findUser(user1.getUserName());
        userList.rmUser(user3);
        assertFalse(userList.hasUser(user1.getUserName()));

        User user4 = userList.findUser(user1.getUserName());
        assertNull(user4);

    }


}