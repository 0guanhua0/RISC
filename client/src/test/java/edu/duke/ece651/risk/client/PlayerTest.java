package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

class PlayerTest {
    Player p = new Player();
    @Test
    void getPlayerName() {
        p.setPlayerId(0);
        p.setPlayerName("A");
        assert ( p.getPlayerName().equals("A") );
        assert ( p.getPlayerId().equals(0) );
    }
}