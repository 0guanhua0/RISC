package edu.duke.ece651.risk.server;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.TerritoryImpl;
import edu.duke.ece651.risk.shared.network.Client;
import edu.duke.ece651.risk.shared.network.Server;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;

public class GameServerTest {
    //clean mongo db
    @AfterEach
    public void cleanMongo() {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));
        mongoClient.getDatabase(MONGO_DB_NAME).getCollection(MONGO_COLLECTION).drop();
        mongoClient.getDatabase(MONGO_DB_NAME).getCollection(MONGO_USERLIST).drop();
    }

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

    @Test
    public void testRun() throws IOException, InterruptedException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);

        String userName1 = "1";
        String userPassword1 = "1";

        String s11 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + userPassword1 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_SIGN_UP + "\" }";

        when(socket1.getInputStream()).thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s11))));
        when(socket1.getOutputStream()).thenReturn(outputStream);


        String userName2 = "2";
        String userPassword2 = "2";

        String s21 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + userPassword1 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_SIGN_UP + "\" }";
        Socket socket2 = mock(Socket.class);
        when(socket2.getInputStream()).thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s21))));
        when(socket2.getOutputStream()).thenReturn(new ByteArrayOutputStream());

        String userName3 = "3";
        String userPassword3 = "3";

        String s31 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + userPassword1 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_SIGN_UP + "\" }";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.close();
        Socket socketError = mock(Socket.class);
        when(socketError.getInputStream()).thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s31))));
        when(socketError.getOutputStream()).thenReturn(stream);

        Server server = mock(Server.class);

        when(server.accept()).thenReturn(socket1).thenReturn(socket2).thenReturn(socketError).thenReturn(null);

        Thread thread = new Thread(() -> {
            GameServer gameServer = null;
            try {
                gameServer = new GameServer(server);
            } catch (SQLException | ClassNotFoundException | IOException ignored) {
                //unreachable
            }
            gameServer.run();
        });
        thread.start();
        Thread.sleep(5000);
        thread.interrupt();
        thread.join();
        verify(server, atLeast(3)).accept();
    }

    @Test
    public void testHandleIncomeRequest() throws IOException, ClassNotFoundException, SQLException {

        GameServer gameServer = new GameServer(null);
        //1 valid signup
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String user1Name = "1";
        String user1Password = "1";
        assertFalse(gameServer.db.authUser(user1Name, user1Password));

        String s1 = "{\"" + USER_NAME + "\": \"" + user1Name + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + user1Password + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_SIGN_UP + "\" }";

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
                "\"" + USER_PASSWORD + "\": \"" + userPassword2 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_LOGIN + "\" }";

        when(socket2.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s2))));
        when(socket2.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket2);
        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(outputStream));

        //2b login again
        outputStream.reset();
        Socket socket2b = mock(Socket.class);
        String userName2b = "1";
        String userPassword2b = "1";

        String s2b = "{\"" + USER_NAME + "\": \"" + userName2b + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + userPassword2b + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_LOGIN + "\" }";

        when(socket2b.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s2b))));
        when(socket2b.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket2b);
        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(outputStream));

        //3 invalid signup, same name
        outputStream.reset();
        String userName3 = "1";
        String userPassword3 = "3";

        String s3 = "{\"" + USER_NAME + "\": \"" + userName3 + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + userPassword3 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_SIGN_UP + "\" }";

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
                "\"" + USER_PASSWORD + "\": \"" + userPassword4 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_LOGIN + "\" }";

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
                "\"" + USER_PASSWORD + "\": \"" + userPassword5 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + "xxx" + "\" }";

        Socket socket5 = mock(Socket.class);
        when(socket5.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s5))));
        when(socket5.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket5);
        assertEquals(INVALID_ACTION_TYPE, readAllStringFromObjectStream(outputStream));

        //6 login user get available room
        outputStream.reset();

        String s6 = "{\"" + USER_NAME + "\": \"" + userName2 + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + userPassword2 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_GET_ALL_ROOM + "\" }";

        Socket socket6 = mock(Socket.class);
        when(socket6.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s6))));
        when(socket6.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket6);
        assertEquals("", readAllStringFromObjectStream(outputStream));

        // 7 login user get room he is in
        outputStream.reset();

        String s7 = "{\"" + USER_NAME + "\": \"" + userName2 + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + userPassword2 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_GET_IN_ROOM + "\" }";

        Socket socket7 = mock(Socket.class);
        when(socket7.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s7))));
        when(socket7.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket7);
        assertEquals("", readAllStringFromObjectStream(outputStream));

        outputStream.reset();



        // 8 unauthorized user get room he is in
        outputStream.reset();

        String s8 = "{\"" + USER_NAME + "\": \"" + "zxc" + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + "zxc" + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_GET_IN_ROOM + "\" }";

        Socket socket8 = mock(Socket.class);
        when(socket8.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s8))));
        when(socket8.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket8);
        assertEquals(INVALID_USER, readAllStringFromObjectStream(outputStream));

        outputStream.reset();

    }

    @Test
    public void testLongSocket() throws SQLException, ClassNotFoundException, IOException {
        GameServer gameServer = new GameServer(null);

        //1 login user create room
        String userName1 = "1";
        String userPassword1 = "1";
        User user1 = new User(userName1, userPassword1);
        gameServer.db.addUser(userName1, userPassword1);
        gameServer.userList.addUser(user1);

        String userName2 = "2";
        String userPassword2 = "2";
        User user2 = new User(userName2, userPassword2);
        gameServer.db.addUser(userName2, userPassword2);
        gameServer.userList.addUser(user2);

        String userName3 = "3";
        String userPassword3 = "3";
        User user3 = new User(userName3, userPassword3);
        gameServer.db.addUser(userName3, userPassword3);
        gameServer.userList.addUser(user3);

        String s11 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + userPassword1 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_CREATE_GAME + "\" }";

        String rName = "1";

        // room 0 has enough player
        String s13 = "{\"" + MAP_NAME + "\": \"" + "a clash of kings" + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName + "\" }";
        // room 1 doesn't have enough player
        String s14 = "{\"" + MAP_NAME + "\": \"" + "test" + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName + "\" }";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);
        when(socket1.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s11, "-1", s13))));
        when(socket1.getOutputStream()).thenReturn(outputStream);

        gameServer.handleIncomeRequest(socket1);
        assertEquals(1, gameServer.rooms.size());
        assertEquals(1, gameServer.rooms.get(0).players.size());
        assertEquals(0, gameServer.rooms.get(0).roomID);
        assertTrue(user1.isInRoom(0));

        socket1 = mock(Socket.class);
        when(socket1.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s11, "-1", s14))));
        when(socket1.getOutputStream()).thenReturn(outputStream);
        gameServer.handleIncomeRequest(socket1);
        assertEquals(2, gameServer.rooms.size());
        assertEquals(1, gameServer.rooms.get(1).players.size());
        assertEquals(1, gameServer.rooms.get(1).roomID);
        assertTrue(user1.isInRoom(1));

        // 2 login user join existing room

        String s21 = "{\"" + USER_NAME + "\": \"" + userName2 + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + userPassword2 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_JOIN_GAME + "\" }";

        ByteArrayOutputStream o2 = new ByteArrayOutputStream();
        Socket socket2 = mock(Socket.class);
        when(socket2.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s21, "0"))));
        when(socket2.getOutputStream()).thenReturn(o2);

        gameServer.handleIncomeRequest(socket2);
        assertEquals(2, gameServer.rooms.size());
        assertEquals(2, gameServer.rooms.get(0).players.size());
        assertTrue(user2.isInRoom(0));

        //3 login user reconnect to room
        String s31 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + ROOM_ID + "\": \"" + 0 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_RECONNECT_ROOM + "\" }";

        ByteArrayOutputStream o3 = new ByteArrayOutputStream();
        Socket socket3 = mock(Socket.class);
        when(socket3.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s31))));
        when(socket3.getOutputStream()).thenReturn(o3);

        gameServer.handleIncomeRequest(socket3);

        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(o3));

        //4 invalid reconnect
        String s41 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + ROOM_ID + "\": \"" + 5 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_RECONNECT_ROOM + "\" }";

        ByteArrayOutputStream o4 = new ByteArrayOutputStream();
        Socket socket4 = mock(Socket.class);
        when(socket4.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s41))));
        when(socket4.getOutputStream()).thenReturn(o4);

        gameServer.handleIncomeRequest(socket4);
        assertEquals(INVALID_RECONNECT, readAllStringFromObjectStream(o4));

        //5 strange action
        String s51 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + ROOM_ID + "\": \"" + 5 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + "xxx" + "\" }";

        ByteArrayOutputStream o5 = new ByteArrayOutputStream();
        Socket socket5 = mock(Socket.class);
        when(socket5.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s51))));
        when(socket5.getOutputStream()).thenReturn(o5);

        gameServer.handleIncomeRequest(socket5);

        assertEquals(INVALID_ACTION_TYPE, readAllStringFromObjectStream(o5));


        //6 login user connect to the chat
        String s6 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + ROOM_ID + "\": \"" + 0 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_CONNECT_CHAT + "\" }";

        ByteArrayOutputStream o6 = new ByteArrayOutputStream();
        Socket socket6 = mock(Socket.class);
        when(socket6.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s6))));
        when(socket6.getOutputStream()).thenReturn(o6);

        gameServer.handleIncomeRequest(socket6);

        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(o6));

        // 7 login user connect to the chat but no in this room
        String s7 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + ROOM_ID + "\": \"" + 10 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_CONNECT_CHAT + "\" }";

        ByteArrayOutputStream o7 = new ByteArrayOutputStream();
        Socket socket7 = mock(Socket.class);
        when(socket7.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s7))));
        when(socket7.getOutputStream()).thenReturn(o7);

        gameServer.handleIncomeRequest(socket7);

        assertEquals(INVALID_RECONNECT, readAllStringFromObjectStream(o7));

        //8 login user audience game
        String s8 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + ROOM_ID + "\": \"" + 0 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_AUDIENCE_GAME + "\" }";
        String s9 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + ROOM_ID + "\": \"" + 1 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_AUDIENCE_GAME + "\" }";
        String s10 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + ROOM_ID + "\": \"" + 2 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_AUDIENCE_GAME + "\" }";

        ByteArrayOutputStream o8 = new ByteArrayOutputStream();
        Socket socket8 = mock(Socket.class);
        when(socket8.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s8))));
        when(socket8.getOutputStream()).thenReturn(o8);

        o8.reset();
        gameServer.handleIncomeRequest(socket8);
        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(o8));

        socket8 = mock(Socket.class);
        when(socket8.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s9))));
        when(socket8.getOutputStream()).thenReturn(o8);
        o8.reset();
        gameServer.handleIncomeRequest(socket8);
        assertEquals(INVALID_AUDIENCE_NOT_START, readAllStringFromObjectStream(o8));

        Room room = new Room(2);
        room.gameInfo.winnerID = 1;
        gameServer.rooms.put(2, room);
        socket8 = mock(Socket.class);
        when(socket8.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s10))));
        when(socket8.getOutputStream()).thenReturn(o8);
        o8.reset();
        gameServer.handleIncomeRequest(socket8);
        assertEquals(INVALID_AUDIENCE_FINISHED, readAllStringFromObjectStream(o8));
    }

    @Test
    public void testAskValidRoomNum() throws IOException, ClassNotFoundException, SQLException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //p1
        String m1 = "-1";
        String r1 = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + m1 + "\",\n" +
                "\"" + ROOM_NAME + "\": \"" + r1 + "\" }";

        String m2 = "test";
        String r2 = "1";

        String s12 = "{\"" + MAP_NAME + "\": \"" + m2 + "\",\n" +
                "\"" + ROOM_NAME + "\": \"" + r2 + "\" }";


        Player<String> player1 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(s11, s12))), new ByteArrayOutputStream());
        Player<String> player2 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("abc", "10", "0"))), outputStream);
        int roomID = 0;
        GameServer gameServer = new GameServer(null);
        gameServer.rooms.put(roomID, new Room(roomID, player1, new MapDataBase<>()));
        assertEquals(roomID, gameServer.askValidRoomNum(player2));
        assertEquals("Invalid choice, try again.".repeat(2) + SUCCESSFUL, readAllStringFromObjectStream(outputStream));
    }

    @Test
    public void testGetRoomList() throws IOException, ClassNotFoundException, SQLException {

        String r1 = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + MAP_0 + "\",\n" +
                "\"" + ROOM_NAME + "\": \"" + r1 + "\" }";

        Player<String> player = new PlayerV1<>(
                setupMockInput(
                        new ArrayList<>(Arrays.asList(
                                s11, s11, s11
                        ))), new ByteArrayOutputStream());
        player.setName("test");

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
        assertEquals(2, gameServer.getRoomList().size()); // only one room waiting for new player
        assertEquals(2, gameServer.rooms.size()); // the room finished is removed
    }

    @Test
    public void testMain() throws IOException, InterruptedException, ClassNotFoundException {

        Thread th = new Thread(() -> {
            try {
                GameServer.main(null);
            } catch (IOException | SQLException | ClassNotFoundException ignored) {
            }
        });
        th.start();
        Thread.sleep(1000);

        Client client = new Client();
        client.init("localhost", 12345);

        String userName1 = "1";
        String userPassword1 = "1";

        String s11 = "{\"" + USER_NAME + "\": \"" + userName1 + "\",\n" +
                "\"" + USER_PASSWORD + "\": \"" + userPassword1 + "\",\n" +
                "\"" + ACTION_TYPE + "\": \"" + ACTION_SIGN_UP + "\" }";

        client.send(s11);
        Thread.sleep(2000);
        assertEquals(SUCCESSFUL, client.recv());

        th.interrupt();
        th.join();

    }

    @Test
    void testStartGame() throws IOException, SQLException, ClassNotFoundException {

        GameServer gameServer = new GameServer(null);
        assertEquals(0, gameServer.rooms.size());

        //p1
        String m1 = "test";
        String r1 = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + m1 + "\",\n" +
                "\"" + ROOM_NAME + "\": \"" + r1 + "\" }";

        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);
        when(socket1.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("-1", s11))));
        when(socket1.getOutputStream()).thenReturn(outputStream1);
        Player p1 = new PlayerV2(socket1.getInputStream(), socket1.getOutputStream());

        User u1 = new User("1", "1");
        gameServer.userList.addUser(u1);
        p1.setName("1");
        gameServer.startGame(p1);
        assertEquals(1, gameServer.rooms.size());
        assertEquals(1, gameServer.rooms.get(0).players.size());
        assertEquals(0, gameServer.rooms.get(0).roomID);

        //prepare for the second player who joins in this room
        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        Socket socket2 = mock(Socket.class);

        when(socket2.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("0", "0"))));
        when(socket2.getOutputStream()).thenReturn(outputStream2);
        //handle the request for second player
        Player p2 = new PlayerV2(socket2.getInputStream(), socket2.getOutputStream());
        User u2 = new User("2", "2");
        gameServer.userList.addUser(u2);
        p2.setName("2");
        assertEquals(1, gameServer.rooms.size());
        gameServer.startGame(p2);
        assertEquals(1, gameServer.rooms.size());
        assertEquals(SUCCESSFUL + "{\"playerColor\":\"blue\",\"playerID\":2}",
                readAllStringFromObjectStream(outputStream2)
        );
        assertEquals(2, gameServer.rooms.get(0).players.size());
        assertEquals(0, gameServer.rooms.get(0).roomID);

        //prepare for the third player who joins in this room
        ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
        Socket socket3 = mock(Socket.class);
        when(socket3.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("0", "0"))));
        when(socket3.getOutputStream()).thenReturn(outputStream3);
        // handle the request for third player
        Player p3 = new PlayerV2(socket3.getInputStream(), socket3.getOutputStream());
        p3.setName("3");
        User u3 = new User("3", "3");
        gameServer.userList.addUser(u3);
        assertEquals(1, gameServer.rooms.size());
        gameServer.startGame(p3);
        assertEquals(1, gameServer.rooms.size());
        assertEquals(3, gameServer.rooms.get(0).players.size());
        assertEquals(0, gameServer.rooms.get(0).roomID);
    }


    @Test
    void UserRoom() throws IOException, SQLException, ClassNotFoundException {

        String r1 = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + MAP_0 + "\",\n" +
                "\"" + ROOM_NAME + "\": \"" + r1 + "\" }";

        Player<String> player1 = new PlayerV1<>(
                setupMockInput(
                        new ArrayList<>(Arrays.asList(
                                s11, s11, s11, s11
                        ))), new ByteArrayOutputStream());
        player1.setName("1");
        player1.setId(10);
        player1.addTerritory(new TerritoryImpl("test",0,0,0));

        Player<String> player2 = new PlayerV1<>(
                setupMockInput(
                        new ArrayList<>(Arrays.asList(
                                s11, s11
                        ))), new ByteArrayOutputStream());
        player2.setName("2");

        Room room1 = new Room(1, player1, new MapDataBase<>()); // waiting
        Room room2 = new Room(2, player1, new MapDataBase<>()); // running
        Room room3 = new Room(3, player1, new MapDataBase<>()); // finish
        Room room4 = new Room(4, player2, new MapDataBase<>()); // finish and player not in
        Room room5 = new Room(5, player2, new MapDataBase<>()); // running and player not in

        room2.players.add(new PlayerV1<>("Green", 1));
        room3.gameInfo.winnerID = 1;
        room4.gameInfo.winnerID = 2;

        Server server = mock(Server.class);
        GameServer gameServer = new GameServer(server);

        User u1 = new User(player1.getName(), "1");
        gameServer.userList.addUser(u1);
        u1.addRoom(1);
        u1.addRoom(2);
        u1.addRoom(3);

        gameServer.rooms.put(room1.roomID, room1);
        gameServer.rooms.put(room2.roomID, room2);
        gameServer.rooms.put(room3.roomID, room3);
        gameServer.rooms.put(room4.roomID, room4);
        gameServer.rooms.put(room5.roomID, room5);

        assertTrue(room1.hasPlayer("1"));
        assertTrue(room2.hasPlayer("1"));
        assertTrue(room3.hasPlayer("1"));
        assertFalse(room4.hasPlayer("1"));
        gameServer.clearRoom();

        assertTrue(u1.isInRoom(2));
        assertFalse(u1.isInRoom(3));
        // room 1 & 2 should in this list
        assertEquals(2, gameServer.getUserRoom("1").size());
    }

}
