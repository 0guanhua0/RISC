package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class PlayerInputTest {
    static Player player = new Player(0, "A");

    @Test
    void read() throws IOException {
        String s0 = "D";
        InputStream stream0 = new ByteArrayInputStream(s0.getBytes(StandardCharsets.UTF_8));
        PlayerInput.readInput(stream0);

    }


}