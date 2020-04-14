package edu.duke.ece651.risk.shared.player;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SMessageTest { 

    @Test
    public void testGetter() {
        SMessage message = new SMessage(1, 1, 2, "x", "hello");
        assertEquals(2, message.getReceiverID());
        assertEquals("hello", message.getMessage());
        assertEquals("x send: hello to 2", message.toString());
    }
} 
