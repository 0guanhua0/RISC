package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class InsPromptTest {
    static private ByteArrayOutputStream outContent;

    @AfterAll
    static void afterAll() {
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @BeforeEach
    public void beforeEach() {
        // empty the stdout before each function call
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void Info() {
        String str1 = "You are the Green player, what would you like to do?\n" +
                " (M)ove\n" +
                " (A)ttack\n" +
                " (D)one\n";
        String str2 = "A player:\n" +
                "-------------\n";
        String str3 = "input source territory\n";
        String str4 = "input destination territory\n";
        String str5 = "input unit number\n";

        Player<String> player = new Player<>();
        player.playerColor = "Green";

        InsPrompt.actInfo(player);
        assertEquals(str1, outContent.toString());
        outContent.reset();

        InsPrompt.selfInfo("A");
        assertEquals(str2, outContent.toString());
        outContent.reset();

        InsPrompt.srcInfo();
        assertEquals(str3, outContent.toString());
        outContent.reset();

        InsPrompt.dstInfo();
        assertEquals(str4, outContent.toString());
        outContent.reset();

        InsPrompt.unitInfo();
        assertEquals(str5, outContent.toString());
        outContent.reset();
    }
}