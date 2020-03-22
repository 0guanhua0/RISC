package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.ToClientMsg.ClientSelect;
import edu.duke.ece651.risk.shared.ToClientMsg.RoundInfo;
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
import java.util.*;

import static edu.duke.ece651.risk.shared.Constant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameClientTest {

    static private ByteArrayOutputStream outContent;
    static Thread serverThread;

    static final String PLAYER_COLOR = "Green";
    static final int PLAYER_ID = 1;

    // mock data
    static String mapName = "a clash of kings";
    static MapDataBase<String> mapDB = new MapDataBase<>();
    static WorldMap<String> map = mapDB.getMap(mapName);
//    static Set<String> groups = new HashSet<>(){
//        {
//            add();
//        }
//    };
    static ClientSelect clientSelect = new ClientSelect(
            10,
            2,
            mapName
    );
    static Map<Integer, String> idToColor = new HashMap<>();

    @BeforeAll
    static void beforeAll() {
        String t1 = "the storm kingdom";
        String t2 = "kingdom of the reach";
        String t3 = "kingdom of the rock";
        String t4 = "kingdom of mountain and vale";
        String t5 = "kingdom of the north";
        String t6 = "principality of dorne";
        map.getTerritory(t1).addNUnits(1);
        map.getTerritory(t2).addNUnits(2);
        map.getTerritory(t3).addNUnits(3);
        map.getTerritory(t4).addNUnits(4);
        map.getTerritory(t5).addNUnits(5);
        map.getTerritory(t6).addNUnits(6);

        map.getTerritory(t1).setOwner(1);
        map.getTerritory(t2).setOwner(1);
        map.getTerritory(t3).setOwner(2);
        map.getTerritory(t4).setOwner(2);
        map.getTerritory(t5).setOwner(3);
        map.getTerritory(t6).setOwner(3);

        // mock data to be sent
        List<Integer> fakeRooms = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        idToColor.put(1, "Green");
        idToColor.put(2, "Blue");
        idToColor.put(3, "Red");

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
                    // 5) send wait info
                    player.send("Please wait");

                    /* =============== stage 2(choose territory) =============== */
                    player.send(clientSelect);
                    //confirm the correctness of each territory
                    player.recv();
                    player.send(SUCCESSFUL);

                    /* =============== stage 3(playing the game) =============== */
                    player.send(new RoundInfo(1, map, idToColor));
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
                    // perform action and send out the result of each attack action
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
        // 1, 1, 10 --- select territory group 1, assign all 10 units to territory 1
        // a --- attack
        // a, b, 10 --- from a to b, 10 units
        // d --- done
        String input = "j\n3\n" + "1\n1\n10\n" + "a\na\nb\n10\nd\n";

        GameClient gameClient = new GameClient();
        gameClient.run(new Scanner(input));
    }
    
    @Test
    public void testPlayGame() throws IOException, ClassNotFoundException {
        Client client = mock(Client.class);
        when(client.recv())
                .thenReturn(clientSelect) // select territory & assign units
                .thenReturn(SUCCESSFUL)//select group valid
                .thenReturn(SUCCESSFUL) // assign units valid
                .thenReturn(new RoundInfo(1, map, idToColor))  // round info, map
                .thenReturn(SUCCESSFUL) // action1 valid
                .thenReturn(SUCCESSFUL) // action2 valid
                .thenReturn("attack 1") // send out the attack result
                .thenReturn("attack 2")
                .thenReturn(ROUND_OVER) // round over
                .thenReturn(GAME_OVER); // game over

        GameClient gameClient = new GameClient();
        gameClient.client = client;
        // 1 --- territory group
        // 1, 5; 2, 5 --- assign 5 units to territory 1, 5 units to territory 2
        // r --- invalid action
        // a --- attack action(a, b, 10)
        // m --- move action(c, d, 5)
        // d --- done
        gameClient.playGame(new Scanner("1\n1\n5\n2\n5\n" + "r\n" + "a\na\nb\n10\n" + "m\nc\nd\n5\nd\n"));
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

        verify(client, times(3)).recv();
    }

    @Test
    public void testCheckResult() throws IOException, ClassNotFoundException {
        Client client = mock(Client.class);
        when(client.recv())
                .thenReturn("Invalid choice, try again.")
                .thenReturn(SUCCESSFUL);

        GameClient gameClient = new GameClient();
        gameClient.client = client;

        assertFalse(gameClient.checkResult());
        assertTrue(gameClient.checkResult());
    }

    @Test
    public void testSelectTerritory() throws IOException, ClassNotFoundException {

        Client client = mock(Client.class);
        when(client.recv())
                .thenReturn(clientSelect)
                .thenReturn(SELECT_TERR_ERROR)
                .thenReturn(SUCCESSFUL);

        GameClient gameClient = new GameClient();
        gameClient.client = client;
        // 3, 1, 10 --- select group 3, assign all 10 units to territory 1
        // then receive error message at the first time
        // 0, 4, 3 --- choose territory group(both 0 & 4 are invalid)
        // 1, 3 --- assign 3 units to the first territory
        // 2, 10, 5 --- assign 5 units to the second territory(10 is invalid)
        // 1, 2 --- assign 2 more units to the first territory
        gameClient.selectTerritory(new Scanner("3\n1\n10\n" + "0\n4\n3\n" + "1\n3\n" + "2\n10\n5\n" + "1\n2\n"));

        verify(client, times(3)).recv();
    }

    @Test
    public void testReceiveAttackResult() throws IOException, ClassNotFoundException {
        Client client = mock(Client.class);
        when(client.recv())
                .thenReturn("attack 1")
                .thenReturn("attack 2")
                .thenReturn("attack 3")
                .thenReturn("attack 4")
                .thenReturn(ROUND_OVER);

        GameClient gameClient = new GameClient();
        gameClient.client = client;
        gameClient.receiveAttackResult();

        verify(client, times(5)).recv();
    }
    
    @Test
    public void testMain() throws IOException, ClassNotFoundException {
        // user input: invalid + select territory & assign units + attack(a->b, 10) + move(c->d, 5) + done
        String input = "c\n1\n" + "1\n1\n6\n2\n4\n" + "a\na\nb\n10\n" + "m\nc\nd\n5\n" + "d\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        GameClient.main(null);
    }

}
