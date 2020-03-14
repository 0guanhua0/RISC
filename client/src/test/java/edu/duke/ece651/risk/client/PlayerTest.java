package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player p = new Player(0, "A");
    @Test
    void getPlayerName() {
        p.getPlayerName();
        p.getPlayerId();
    }
}