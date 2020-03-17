package edu.duke.ece651.risk.shared;

import static edu.duke.ece651.risk.shared.Mock.readAllStringFromObjectStream;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

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
        for (Object o : new ArrayList<>(Arrays.asList("1", "2", "3"))){
            objectOutputStream.writeObject(o);
        }
        objectOutputStream.flush();
        assertEquals("123", readAllStringFromObjectStream(out));
    }
    
    @Test
    public void testMain() { 
        
    }
    

} 
