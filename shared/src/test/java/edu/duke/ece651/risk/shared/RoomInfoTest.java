package edu.duke.ece651.risk.shared;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RoomInfoTest {

    @Test
    public void testGetter() {
        RoomInfo roomInfo = new RoomInfo(1, "test");
        assertEquals(1, roomInfo.getRoomID());
        assertEquals("test", roomInfo.getRoomName());
    }

} 
