package edu.duke.ece651.risk.shared;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RoomTest { 

    @Test
    public void testGetter() {
        Room room = new Room(1, "test");
        assertEquals(1, room.getRoomID());
        assertEquals("test", room.getRoomName());
    }

} 
