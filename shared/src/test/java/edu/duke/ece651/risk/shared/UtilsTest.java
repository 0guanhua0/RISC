package edu.duke.ece651.risk.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class UtilsTest { 

    @Test
    public void testReadFileToString() throws IOException {
        JSONObject jsonObject = new JSONObject(Utils.readFileToString("../config_file/client_config.txt"));
        assertEquals("localhost", jsonObject.getString("host"));
        assertEquals(12345, jsonObject.getInt("port"));
    }
    //TODO add a deep copy function for map
} 
