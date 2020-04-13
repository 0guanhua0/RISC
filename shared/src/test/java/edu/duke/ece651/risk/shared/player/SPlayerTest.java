package edu.duke.ece651.risk.shared.player;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SPlayerTest { 

    @Test
    public void testGetter() {
        SPlayer player = new SPlayer(1, "x");
        assertEquals(1, player.getId());
        assertEquals("x", player.getName());
    }
} 
