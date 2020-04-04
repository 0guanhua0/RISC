package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.network.Client;
import edu.duke.ece651.risk.shared.network.Server;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.duke.ece651.risk.shared.Constant.*;
import static edu.duke.ece651.risk.shared.Mock.readAllStringFromObjectStream;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameServerTest {

    @Test
    public void testConstructor() throws IOException, SQLException, ClassNotFoundException {
        GameServer gameServer = new GameServer(new Server(8000));
        assertEquals(gameServer.rooms.size(), 0);
        assertNotNull(gameServer.threadPool);
        assertNotNull(gameServer.server);
        new Thread(() -> {
            Socket socket = gameServer.server.accept();
            assertNotNull(socket);
            try {
                new PlayerV1<>(socket.getInputStream(), socket.getOutputStream());
            } catch (IOException ignored) {
            }
        }).start();
        Client client = new Client();
        client.init("127.0.0.1", 8000);
    }

    /*
    @Test
    public void testRun() throws IOException, InterruptedException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);
        when(socket1.getInputStream()).thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("{\"userName\": \"name1\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\" }"))));
        when(socket1.getOutputStream()).thenReturn(outputStream);


        Socket socket2 = mock(Socket.class);
        when(socket2.getInputStream()).thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("{\"userName\": \"name2\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\" }"))));
        when(socket2.getOutputStream()).thenReturn(new ByteArrayOutputStream());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.close();
        Socket socketError = mock(Socket.class);
        when(socketError.getInputStream()).thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("{\"userName\": \"name3\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\" }"))));
        when(socketError.getOutputStream()).thenReturn(stream);

        Server server = mock(Server.class);

        when(server.accept()).thenReturn(socket1).thenReturn(socket2).thenReturn(socketError);

        Thread thread = new Thread(() -> {
            GameServer gameServer = null;
            try {
                gameServer = new GameServer(server);
            } catch (SQLException | ClassNotFoundException ignored) {
                //unreachable
            }
            gameServer.run();
        });
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
        thread.join();
        verify(server, atLeast(3)).accept();
    }

     */

    /**
     * short socket
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testHandleIncomeRequest() throws IOException, ClassNotFoundException, SQLException {
        GameServer gameServer = new GameServer(null);

        //1 valid signup
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String user1Name = "1";
        String user1Password = "1";
        assertFalse(gameServer.db.authUser(user1Name, user1Password));

        String s1 = "{\"" + USER_NAME + "\": \"" + user1Name + "\",\n" +
                "\"" + USER_PASSWORD +"\": \"" + user1Password + "\",\n" +
                "\"" + ACTION + "\": \"" + SIGNUP + "\" }";

        Socket socket1 = mock(Socket.class);
        when(socket1.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s1))));
        when(socket1.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket1);
        assertTrue(gameServer.db.authUser(user1Name, user1Password));
        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(outputStream));


        //2 valid login
        outputStream.reset();
        Socket socket2 = mock(Socket.class);
        String userName2 = "1";
        String userPassword2 = "1";

        String s2 = "{\"" + USER_NAME + "\": \"" + userName2 + "\",\n" +
                "\"" + USER_PASSWORD +"\": \"" + userPassword2 + "\",\n" +
                "\"" + ACTION + "\": \"" + LOGIN + "\" }";

        when(socket2.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s2))));
        when(socket2.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket2);
        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(outputStream));

        //3 invalid signup, same name
        outputStream.reset();
        String userName3 = "1";
        String userPassword3 = "3";

        String s3 = "{\"" + USER_NAME + "\": \"" + userName3 + "\",\n" +
                "\"" + USER_PASSWORD +"\": \"" + userPassword3 + "\",\n" +
                "\"" + ACTION + "\": \"" + SIGNUP + "\" }";

        Socket socket3 = mock(Socket.class);
        when(socket3.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s3))));
        when(socket3.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket3);
        assertTrue(gameServer.db.authUser(user1Name, user1Password));
        assertEquals(INVALID_SIGNUP, readAllStringFromObjectStream(outputStream));

        //4 invalid login
        outputStream.reset();
        String userName4 = "4";
        String userPassword4 = "4";

        String s4 = "{\"" + USER_NAME + "\": \"" + userName4 + "\",\n" +
                "\"" + USER_PASSWORD +"\": \"" + userPassword4 + "\",\n" +
                "\"" + ACTION + "\": \"" + LOGIN + "\" }";

        Socket socket4 = mock(Socket.class);
        when(socket4.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s4))));
        when(socket4.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket4);
        assertEquals(INVALID_LOGIN, readAllStringFromObjectStream(outputStream));

        //5 invalid user with strange action
        outputStream.reset();
        String userName5 = "5";
        String userPassword5 = "5";

        String s5 = "{\"" + USER_NAME + "\": \"" + userName5 + "\",\n" +
                "\"" + USER_PASSWORD +"\": \"" + userPassword5 + "\",\n" +
                "\"" + ACTION + "\": \"" + "xxx" + "\" }";

        Socket socket5 = mock(Socket.class);
        when(socket5.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s5))));
        when(socket5.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket5);
        assertEquals(INVALID_USER, readAllStringFromObjectStream(outputStream));


        //6 login user get available room


        //7 login user get room he is in


    }

    /**
     * long socket
     */
    @Test
    public void testLongSocket() {
        //1 login user create room


        //2 login user join existing room

        //3 login user reconnect to room

    }

    @Test
    public void testAskValidRoomNum() throws IOException, ClassNotFoundException, SQLException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Player<String> player1 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("-1", "test"))), new ByteArrayOutputStream());
        Player<String> player2 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("abc", "10", "0"))), outputStream);
        int roomID = 0;
        GameServer gameServer = new GameServer(null);
        gameServer.rooms.put(roomID, new Room(roomID, player1, new MapDataBase<String>()));
        assertEquals(roomID, gameServer.askValidRoomNum(player2));
        assertEquals("Invalid choice, try again.".repeat(2) + SUCCESSFUL, readAllStringFromObjectStream(outputStream));
    }

    @Test
    public void testGetRoomList() throws IOException, ClassNotFoundException, SQLException {
        Player<String> player = new PlayerV1<>(
                setupMockInput(
                        new ArrayList<>(Arrays.asList(
                                "a clash of kings",
                                "a clash of kings",
                                "a clash of kings"
                        ))), new ByteArrayOutputStream());

        Room room1 = new Room(1, player, new MapDataBase<>()); // waiting
        Room room2 = new Room(2, player, new MapDataBase<>()); // running
        Room room3 = new Room(3, player, new MapDataBase<>()); // finish

        room2.players.add(new PlayerV1<>("Green", 1));
        room3.gameInfo.winnerID = 1;

        Server server = mock(Server.class);
        GameServer gameServer = new GameServer(server);

        gameServer.rooms.put(room1.roomID, room1);
        gameServer.rooms.put(room2.roomID, room2);
        gameServer.rooms.put(room3.roomID, room3);

        assertEquals(3, gameServer.rooms.size());
        assertEquals(1, gameServer.getRoomList().size()); // only one room waiting for new player
        assertEquals(2, gameServer.rooms.size()); // the room finished is removed
    }

    /*
    @Test
    public void testMain() throws IOException, InterruptedException, ClassNotFoundException {
        Thread th = new Thread(() -> {
            try {
                GameServer.main(null);
            } catch (IOException | SQLException | ClassNotFoundException ignored) {
            }
        });
        th.start();
        Thread.sleep(100);

        Client client = new Client();
        client.init("localhost", 12345);
        assertEquals("Welcome to the fancy RISK game!!!", client.recv());
        client.send("{\"userName\": \"name3\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\" }");

        Thread.sleep(2000);

        th.interrupt();
        th.join();
    }

     */

    @Test
    void createPlayer() throws IOException, SQLException, ClassNotFoundException {
        GameServer gameServer = new GameServer(null);
        assertEquals(0, gameServer.rooms.size());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);
        when(socket1.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("-1", "test"))));
        when(socket1.getOutputStream()).thenReturn(outputStream);
        User user1 = new User("1");

        gameServer.createPlayer(socket1, user1);
        assertEquals(1, gameServer.rooms.size());
        assertEquals(1, gameServer.rooms.get(0).players.size());
        assertEquals(0, gameServer.rooms.get(0).roomID);
        assertTrue(user1.isInRoom(0));

        //prepare for the second player who joins in this room
        outputStream.reset();
        Socket socket2 = mock(Socket.class);
        User user2 = new User("2");
        when(socket2.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("0", "0"))));
        when(socket2.getOutputStream()).thenReturn(outputStream);
        //handle the request for second player
        assertEquals(1, gameServer.rooms.size());
        gameServer.createPlayer(socket2, user2);
        assertEquals(1, gameServer.rooms.size());
        assertEquals(SUCCESSFUL + "{\"playerColor\":\"blue\",\"playerID\":2}" + "Please wait other players to join th game(need 3, joined 2)",
                readAllStringFromObjectStream(outputStream)
        );
        assertEquals(2, gameServer.rooms.get(0).players.size());
        assertEquals(0, gameServer.rooms.get(0).roomID);
        assertTrue(user2.isInRoom(0));

        //prepare for the third player who joins in this room
        outputStream.reset();
        Socket socket3 = mock(Socket.class);
        User user3 = new User("3");
        when(socket3.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("0", "0"))));
        when(socket3.getOutputStream()).thenReturn(outputStream);
        // handle the request for third player
        assertEquals(1, gameServer.rooms.size());
        gameServer.createPlayer(socket3, user3);
        assertEquals(1, gameServer.rooms.size());
        assertEquals(3, gameServer.rooms.get(0).players.size());
        assertEquals(0, gameServer.rooms.get(0).roomID);
        assertTrue(user3.isInRoom(0));
    }
}
