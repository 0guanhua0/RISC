package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.network.Client;
import edu.duke.ece651.risk.shared.network.Server;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static edu.duke.ece651.risk.shared.Constant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GameClientTest {

    static private ByteArrayOutputStream outContent;
    static Thread serverThread;

    static final String PLAYER_COLOR = "Green";
    static final int PLAYER_ID = 1;

    @BeforeAll
    static void beforeAll() {
        // mock data to be sent
        List<Integer> fakeRooms = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        MapDataBase<String> mapDB = new MapDataBase<>();
        WorldMap<String> map = mapDB.getMap("test");

        outContent = new ByteArrayOutputStream();
        // setup "game server" at hte beginning
        serverThread = new Thread(() -> {
            try {
                Server server = new Server(12345);
                while (!Thread.currentThread().isInterrupted()){
                    // make a spy server(seems useless....)
                    Server spyServer = spy(server);

                    Socket socket = spyServer.accept();
                    if (socket == null){
                        continue;
                    }
                    /* =============== stage 1(initialize the game) =============== */
                    // 1) accept and initialize a new player object
                    Player<String> player = new PlayerV1<>(PLAYER_COLOR, PLAYER_ID, socket.getInputStream(), socket.getOutputStream());
                    player.send("Hello");
                    // 2) ask room number
                    player.send(fakeRooms);
                    String choice = (String) player.recv();
                    player.send(SUCCESSFUL);
                    // 3) ask map(if create a new room)
                    if (choice.equals("-1")){
                        player.send(mapDB);
                        player.recv(); // just assume all input map name is valid
                        player.send(SUCCESSFUL);
                    }
                    // 4) send initial message
                    player.sendPlayerInfo();

                    /* =============== stage 2(choose territory) =============== */

                    /* =============== stage 3(playing the game) =============== */
                    player.send(map);
                    // interact with player to ask all actions
                    while (true){
                        Object object = player.recv(); // receive the action list
                        if (object instanceof String){
                            // action done
                            break;
                        }else {
                            // ask for next action
                            player.send(SUCCESSFUL);
                        }
                    }
                    /// perform action and send out the result of each attack action
                    player.send("attack 1");
                    player.send(ROUND_OVER); // next round

                    player.send(GAME_OVER); // game over

                    /* =============== stage 4(game end) =============== */
                    player.send("you win");
                }
            } catch (IOException | ClassNotFoundException ignored) {
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
    public void testRun() throws IOException, ClassNotFoundException {
        // j, 3 --- join room 3
        // a --- attack
        // a, b, 10 --- from a to b, 10 units
        // d --- done
        String input = "j\n3\n" + "a\na\nb\n10\nd\n";
        GameClient gameClient = new GameClient();
        gameClient.run(new Scanner(input));
    }
    
    @Test
    public void testPlayGame() throws IOException, ClassNotFoundException {
        WorldMap<String> map = new MapDataBase<String>().getMap("test");

        Client client = mock(Client.class);
        when(client.recv())
                .thenReturn(map)
                .thenReturn(SUCCESSFUL) // action valid
                .thenReturn(SUCCESSFUL)
                .thenReturn("attack 1") // send out the attack result
                .thenReturn("attack 2")
                .thenReturn(ROUND_OVER) // round over
                .thenReturn(GAME_OVER); // game over

        GameClient gameClient = new GameClient();
        gameClient.client = client;
        // r --- invalid action
        // a --- attack action(a, b, 10)
        // m --- move action(c, d, 5)
        // d --- done
        gameClient.playGame(new Scanner("r\n" + "a\na\nb\n10\n" + "m\nc\nd\n5\nd\n"));
    }
    
    @Test
    public void testEndGame() { 
        
    }

    @Test
    public void testChooseRoom() throws IOException, ClassNotFoundException {
        List<Integer> rooms = new ArrayList<>(Arrays.asList(1, 2, 3));
        Client client = mock(Client.class);
        when(client.recv())
                .thenReturn(rooms)
                .thenReturn("Invalid choice, try again.")
                .thenReturn(SUCCESSFUL);

        GameClient gameClient = new GameClient();
        gameClient.client = client;
        // a --- invalid choice
        // j, 10 --- valid action choice but invalid room number
        // c --- valid choice but client will receive a error(for testing purpose)
        // j, 1 --- valid action choice and valid room number
        gameClient.chooseRoom(new Scanner("a\nj\n10\nc\nj\n1\n"));
    }

    @Test
    public void testChooseMap() throws IOException, ClassNotFoundException {
        Client client = mock(Client.class);
        when(client.recv())
                .thenReturn(new MapDataBase<>())
                .thenReturn("Invalid choice, try again.")
                .thenReturn(SUCCESSFUL);

        GameClient gameClient = new GameClient();
        gameClient.client = client;
        // 0 --- invalid map number(number should start from 1)
        // 1 --- valid map number(but to test the error message, client will receive an error at the first time)
        // 1 --- valid map number(also client will receive successful)
        gameClient.chooseMap(new Scanner("0\n1\n1\n"));
    }
    
    @Test
    public void testReadConfigFile() throws IOException {
        GameClient gameClient = new GameClient();
        JSONObject jsonObject = new JSONObject(gameClient.readConfigFile());
        assertEquals("localhost", jsonObject.getString("host"));
        assertEquals(12345, jsonObject.getInt("port"));
    }
    
    @Test
    public void testMain() throws IOException, ClassNotFoundException {
        // user input: invalid + attack(a->b, 10) + move(c->d, 5) + done
        String input = "c\n1\n" + "a\na\nb\n10\n" + "m\nc\nd\n5\n" + "d\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        GameClient.main(null);
    }

}
