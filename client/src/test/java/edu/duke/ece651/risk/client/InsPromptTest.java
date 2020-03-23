package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

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
        String str3 = "1. t1\n" +
                "2. t2\n" +
                "3. t3\n" +
                "input source territory(by index): \n";
        String str4 = "1. t1\n" +
                "2. t2\n" +
                "3. t3\n" +
                "input destination territory(by index): \n";
        String str5 = "input unit number\n";
        Map<Integer, Territory> tOwn = new HashMap<>();
        Territory t1 = new TerritoryV1("t1");
        Territory t2 = new TerritoryV1("t2");
        Territory t3 = new TerritoryV1("t3");
        tOwn.put(1, t1);
        tOwn.put(2, t2);
        tOwn.put(3, t3);

        Player<String> player = new Player<>();
        player.playerColor = "Green";

        InsPrompt.actInfo(player);
        assertEquals(str1, outContent.toString());
        outContent.reset();

        InsPrompt.srcInfo(tOwn);
        assertEquals(str3, outContent.toString());
        outContent.reset();

        InsPrompt.dstInfo(tOwn);
        assertEquals(str4, outContent.toString());
        outContent.reset();

        InsPrompt.unitInfo();
        assertEquals(str5, outContent.toString());
        outContent.reset();
    }
}