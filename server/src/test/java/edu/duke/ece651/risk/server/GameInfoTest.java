package edu.duke.ece651.risk.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GameInfoTest {

    @Test
    public void testWinnerID() {
        GameInfo gameInfo = new GameInfo(-1, 1);
        gameInfo.addPlayer(1, "Green");
        assertFalse(gameInfo.hasFinished());
        gameInfo.setWinner(1);
        assertThrows(IllegalArgumentException.class, ()->{gameInfo.setWinner(4);});
        assertTrue(gameInfo.hasFinished());
        assertEquals(1, gameInfo.getWinnerID());
        assertEquals("Green", gameInfo.getWinnerName());

        //test morphia
        GameInfo g1 = new GameInfo();
        g1.roundNum = 1;
        assertEquals(1, g1.getRoundNum());
    }

    @Test
    public void testIdToName() {
        GameInfo gameInfo = new GameInfo(-1, 1);
        assertEquals(0, gameInfo.getIdToName().size());
        gameInfo.addPlayer(1, "Green");
        assertEquals(1, gameInfo.getIdToName().size());
    }
    
    @Test
    public void testRoundNum() {
        GameInfo gameInfo = new GameInfo(-1, 1);
        assertEquals(1, gameInfo.getRoundNum());
        gameInfo.nextRound();
        assertEquals(2, gameInfo.getRoundNum());
    }

} 
