package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Scanner;

class PlayerInputTest {
    static Player player = new Player(0, "A");
    ActionList aL = new ActionList();

    @Test
    void read() throws IOException {
        String s0 = "D";
        Scanner sc0 = new Scanner(s0);
        PlayerInput.read(sc0, player, aL);


        s0 = "K\nD";
        sc0 = new Scanner(s0);

        PlayerInput.read(sc0, player, aL);


/*
        s0 = "K";
        InputStream stream2 = new ByteArrayInputStream(s0.getBytes(StandardCharsets.UTF_8));
        PlayerInput.read(stream2, player, aL);

         */
    }


    @Test
    void readAction() {
        String s0 = "A\nB\nA\n";
        Scanner sc0 = new Scanner(s0);
        PlayerInput.readAction(sc0, 0, "A", aL);

        String s1 = "A\nB\n10\n";
        Scanner sc1 = new Scanner(s1);
        PlayerInput.readAction(sc1, 0, "A", aL);

        String s2 = "A\nB\n10\n";
        Scanner sc2 = new Scanner(s2);
        PlayerInput.readAction(sc2, 0, "M", aL);



    }
}