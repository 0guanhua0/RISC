package edu.duke.ece651.risk.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserListTest {

    @Test
    void addRmUser() throws IOException {
        UserList userList = new UserList();
        List list = new ArrayList();
        assertEquals(userList.getUserList(), list);

        User user1 = new User("1", "1");

        assertFalse(userList.hasUser(user1.userName));
        userList.addUser(user1);
        assertTrue(userList.hasUser(user1.getUserName()));

        User user3 = userList.getUser(user1.getUserName());
        userList.rmUser(user3);
        assertFalse(userList.hasUser(user1.getUserName()));

        assertNull(userList.getUser("4"));

    }


}