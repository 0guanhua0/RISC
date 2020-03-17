package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class SceneCLITest {
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
    void showMap() {
        SceneCLI.showMap(null);
        assertEquals("showing example map\n10 units in Narnia (next to: Elantris, Midkemia)\n", outContent.toString());
    }
}