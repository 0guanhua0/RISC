package edu.duke.ece651.risk.shared;

import static edu.duke.ece651.risk.shared.Mock.*;
import static org.junit.jupiter.api.Assertions.*;

import edu.duke.ece651.risk.shared.player.SMessage;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockTest { 

    @Test
    public void testSetupMockInput() throws IOException, ClassNotFoundException {
        InputStream in = setupMockInput(new ArrayList<>(Arrays.asList("1", "2", "3")));
        ObjectInputStream o = new ObjectInputStream(in);
        assertEquals("1", o.readObject());
        assertEquals("2", o.readObject());
        assertEquals("3", o.readObject());
    }
    
    @Test
    public void testReadAllStringFromObjectStream() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        for (Object o : new ArrayList<>(Arrays.asList("1", "2", new ArrayList<>(), "3"))){
            objectOutputStream.writeObject(o);
        }
        objectOutputStream.flush();
        assertEquals("123", readAllStringFromObjectStream(out));
    }

    @Test
    public void testReadAllChatFromObjectStream() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        SMessage message1 = new SMessage(1, 1, 2, "x", "hello1");
        SMessage message2 = new SMessage(2, 2, -1, "x", "hello2");
        SMessage message3 = new SMessage(3, 3, 1, "x", "hello3");
        for (Object o : new ArrayList<>(Arrays.asList(message1, message2, new ArrayList<>(), message3, "test"))){
            objectOutputStream.writeObject(o);
        }
        objectOutputStream.flush();
        List<SMessage> messages = readAllChatFromObjectStream(out);
        assertEquals(3, messages.size());
        assertEquals("hello1", messages.get(0).getMessage());
        assertEquals("hello2", messages.get(1).getMessage());
        assertEquals("hello3", messages.get(2).getMessage());
    }

} 
