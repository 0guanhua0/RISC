package edu.duke.ece651;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;

public class ClientTest { 

    @Test
    public void testGetHostByName() throws UnknownHostException {
        Client client = new Client();
        assertEquals("67.159.88.31", client.getHostByName("vcm-12305.vm.duke.edu"));
    }
    

} 
