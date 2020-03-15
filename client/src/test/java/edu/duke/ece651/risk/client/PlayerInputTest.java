package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.Constant;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class PlayerInputTest {
    static Player player = new Player(0, "A");
    ActionList aL = new ActionList();

    @Test
    void read() throws IOException {
        String s0 = "D\r";
        InputStream stream0 = new ByteArrayInputStream(s0.getBytes(StandardCharsets.UTF_8));
        PlayerInput.read(stream0, player, aL);

        s0 = "M";
        InputStream stream1 = new ByteArrayInputStream(s0.getBytes(StandardCharsets.UTF_8));
        PlayerInput.read(stream1, player, aL);

        s0 = "K";
        InputStream stream2 = new ByteArrayInputStream(s0.getBytes(StandardCharsets.UTF_8));
        PlayerInput.read(stream2, player, aL);
    }


    @Test
    void readInput() throws IOException {
        String s0 = "A";
        InputStream stream0 = new ByteArrayInputStream(s0.getBytes(StandardCharsets.UTF_8));

        System.setIn(stream0);
        assert (PlayerInput.readInput(System.in).equals("A"));
    }

    @Test
    void readAction() throws IOException {
        String s0 = "0";
        InputStream stream0 = new ByteArrayInputStream(s0.getBytes(StandardCharsets.UTF_8));

        System.setIn(stream0);
        PlayerInput.readAction(stream0, 0, Constant.ACTION_ATTACK, aL);
    }
}