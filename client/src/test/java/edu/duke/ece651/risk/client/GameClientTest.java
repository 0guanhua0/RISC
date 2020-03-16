package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.network.Server;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

public class GameClientTest {

    static private ByteArrayOutputStream outContent;
    static Thread serverThread;

    @BeforeAll
    static void beforeAll(){
        outContent = new ByteArrayOutputStream();
        // setup "game server" at hte beginning
        serverThread = new Thread(() -> {
            try {
                Server server = new Server(12345);
                // make a spy server
                Server spyServer = spy(server);
                // TODO: mock what an real server will do
                // 1) accept
                // 2) ask room number
                // 3) ask map
                // 4) send initial message
            } catch (IOException ignored) {
            }
        });
        serverThread.start();
    }

    @AfterAll
    static void afterAll() throws InterruptedException {
        System.setOut(System.out);
        System.setIn(System.in);
        serverThread.interrupt();
        serverThread.join();
    }

    @BeforeEach
    public void beforeEach() {
        // empty the stdout before each function call
        outContent.reset();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testRun() { 
        
    }
    
    @Test
    public void testInitGame() throws IOException {
        GameClient gameClient = new GameClient();
        gameClient.initGame(new Scanner(""));
    }
    
    @Test
    public void testPlayGame() { 
        
    }
    
    @Test
    public void testEndGame() { 
        
    }
    
    @Test
    public void testReadConfigFile() throws IOException {
        GameClient gameClient = new GameClient();
        JSONObject jsonObject = new JSONObject(gameClient.readConfigFile());
        assertEquals("localhost", jsonObject.getString("host"));
        assertEquals(12345, jsonObject.getInt("port"));
    }
    
    @Test
    public void testMain() throws IOException {
        // user input: invalid + attack(a->b, 10) + move(c->d, 5) + done + quit
        String input = "c\n" + "a\na\nb\n10" + "m\nc\nd\n5\n" + "d\n" + "q\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        GameClient.main(null);
    }

} 
