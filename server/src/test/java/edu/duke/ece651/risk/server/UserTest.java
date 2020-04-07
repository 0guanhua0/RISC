package edu.duke.ece651.risk.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


class UserTest {

    @Test
    void io() throws IOException, ClassNotFoundException {
        String userName = "1";
        String userPassword = "1";

        User user1 = new User(userName, userPassword);

        assertEquals(userName, user1.getUserName());
        assertEquals(userPassword, user1.getUserPassword());

        //test room
        assertFalse(user1.isInRoom(0));
        user1.addRoom(1);
        assertEquals(Arrays.asList(1), user1.getRoomList());
        assertTrue(user1.isInRoom(1));

        user1.rmRoom(1);
        assertFalse(user1.isInRoom(1));




    }






}