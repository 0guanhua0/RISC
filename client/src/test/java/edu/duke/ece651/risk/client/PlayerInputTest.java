package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.action.Action;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

class PlayerInputTest {
    static Player player = new Player(0, "A");
    static HashMap<String, List<Action>> actions = new HashMap<>();


    @Test
    void read() throws IOException {
        String s0 = "D";
        InputStream stream0 = new ByteArrayInputStream(s0.getBytes(StandardCharsets.UTF_8));
        PlayerInput.read(stream0, player, actions);

    }


    @Test
    void readAction() {
    }

    @Test
    void isNumeric() {
        System.out.println(PlayerInput.isNumeric("2A"));
    }
}