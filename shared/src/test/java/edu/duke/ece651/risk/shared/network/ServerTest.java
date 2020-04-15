package edu.duke.ece651.risk.shared.network;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ServerTest { 

    @Test
    public void testAccept() throws Exception {
        Server s = new Server(8989);
        assertNull(s.accept());
    }

    @Test
    public void testClose() throws Exception {
        Server s = new Server(8989);
        assertFalse(s.serverSocket.isClosed());
        s.close();
        assertTrue(s.serverSocket.isClosed());
    }
    

} 
