package edu.duke.ece651.risk.shared.player;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SMessageTest { 

    @Test
    public void testGetter() {
        SMessage message = new SMessage(1, 1, 2, "x", "hello");
        assertEquals(1, message.getId());
        assertEquals(1, message.getSenderID());
        assertEquals(2, message.getReceiverID());
        assertEquals("x", message.getSenderName());
        assertEquals("hello", message.getMessage());
        assertEquals("x send: hello to 2", message.toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(simpleDateFormat.format(new Date()), simpleDateFormat.format(message.getDate()));
    }
} 
