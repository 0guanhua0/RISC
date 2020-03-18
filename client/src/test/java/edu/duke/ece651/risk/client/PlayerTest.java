package edu.duke.ece651.risk.client;

import static edu.duke.ece651.risk.shared.Constant.PLAYER_COLOR;
import static edu.duke.ece651.risk.shared.Constant.PLAYER_ID;
import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

class PlayerTest {
    Player<String> p = new Player<>();

    @Test
    void testGetterSetter() {
        p.setPlayerName("A");
        assertEquals("A", p.getPlayerName());
    }

    @Test
    public void testInit() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PLAYER_ID, 1);
        jsonObject.put(PLAYER_COLOR, "Green");
        p.init(jsonObject.toString());
        assertEquals(1, p.playerId);
        assertEquals("Green", p.playerColor);
    }


}
