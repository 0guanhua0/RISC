package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

class PlayerTest {
    Player p = new Player(0, "A");
    @Test
    void getPlayerName() {
        assert ( p.getPlayerName().equals("A") );
        assert ( p.getPlayerId().equals(0) );
    }
}