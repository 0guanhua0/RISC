package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.Mock;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.duke.ece651.risk.shared.Mock.readAllStringFromObjectStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


class UserTest {

    @Test
    void io() throws IOException, ClassNotFoundException {
        String info1 = "1";
        List list1 = new ArrayList<Object>(Arrays.asList(info1));
        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        User user1 = new User(Mock.setupMockInput(list1), byteArrayOutputStream1);
        assertEquals(info1, user1.recv());

        user1.send(info1);
        assertEquals(info1, readAllStringFromObjectStream(byteArrayOutputStream1));

        user1.setUserName(info1);
        assertEquals(info1, user1.getUserName());

        assertFalse(user1.isInRoom(0));
        user1.addRoom(1);
        assertEquals(Arrays.asList(1), user1.getRoomList());

        user1.rmRoom(1);
        assertEquals(Arrays.asList(), user1.getRoomList());

        User user2 = new User("2", Mock.setupMockInput(list1), byteArrayOutputStream1);
        assertEquals("2", user2.getUserName());

        /*
        //mock exception
        OutputStream outputStream2 = mock(OutputStream.class);
        String info2 = "2";
        //when(outputStream2.write(info2.getBytes())).thenThrow(new IOException("mocking output"));

        InputStream InputStream2 = mock(InputStream.class);
        when(InputStream2.read()).thenThrow(new IOException("mocking input"));



        user2.setIn(InputStream2);
        user2.setOut(outputStream2);

        outputStream2.close();
        //user2.send(info2);
        user2.recv();

         */


    }






}