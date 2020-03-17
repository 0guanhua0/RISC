package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.network.Client;
import edu.duke.ece651.risk.shared.network.Server;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.duke.ece651.risk.shared.Mock.readAllStringFromObjectStream;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GameServerTest {

    @Test
    public void testConstructor() throws IOException {
        GameServer gameServer = new GameServer(new Server(8000));
        assertEquals(gameServer.rooms.size(), 0);
        assertNotNull(gameServer.threadPool);
        assertNotNull(gameServer.server);
        new Thread(()->{
            Socket socket = gameServer.server.accept();
            assertNotNull(socket);
        }).start();
        Client client = new Client();
        client.init("127.0.0.1", 8000);
    }

    @Test
    public void testRun() throws IOException, InterruptedException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);
        when(socket1.getInputStream()).thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("-1", "-1"))));
        when(socket1.getOutputStream()).thenReturn(outputStream);

        Socket socket2 = mock(Socket.class);
        when(socket2.getInputStream()).thenReturn(setupMockInput(new ArrayList<>()));
        when(socket2.getOutputStream()).thenReturn(new ByteArrayOutputStream());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.close();
        Socket socketError = mock(Socket.class);
        when(socketError.getInputStream()).thenReturn(setupMockInput(new ArrayList<>()));
        when(socketError.getOutputStream()).thenReturn(stream);

        Server server = mock(Server.class);

        when(server.accept()).thenReturn(socket1).thenReturn(socket2).thenReturn(socketError);

        Thread thread = new Thread(()->{
            GameServer gameServer = new GameServer(server);
            gameServer.run();
        });
        thread.start();
        Thread.sleep(100);
        thread.interrupt();
        thread.join();
        verify(server, atLeast(3)).accept();
    }

    @Test
    public void testHandleIncomeRequest() throws IOException, ClassNotFoundException {
        //prepare for the first player who creates a new room
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);
        when(socket1.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("-1", "test"))));
        when(socket1.getOutputStream()).thenReturn(outputStream);

        //handle the request for first player
        GameServer gameServer = new GameServer(null);
        assertEquals(0, gameServer.rooms.size());
        gameServer.handleIncomeRequest(socket1);
        assertEquals(1, gameServer.rooms.size());
        assertEquals(1,gameServer.rooms.get(0).players.size());
        assertEquals(0,gameServer.rooms.get(0).roomID);


        //prepare for the second player who joins in this room
        outputStream.reset();
        Socket socket2 = mock(Socket.class);
        when(socket2.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("0", "0"))));
        when(socket2.getOutputStream()).thenReturn(outputStream);
        //handle the request for second player
        assertEquals(1, gameServer.rooms.size());
        gameServer.handleIncomeRequest(socket2);
        assertEquals(1, gameServer.rooms.size());
        assertEquals("Welcome to the fancy RISK game!!!", readAllStringFromObjectStream(outputStream));
        assertEquals(2,gameServer.rooms.get(0).players.size());
        assertEquals(0,gameServer.rooms.get(0).roomID);

        //prepare for the third player who joins in this room
        outputStream.reset();
        Socket socket3 = mock(Socket.class);
        when(socket3.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("0", "0"))));
        when(socket3.getOutputStream()).thenReturn(outputStream);
        //handle the request for third player
        assertEquals(1, gameServer.rooms.size());
        gameServer.handleIncomeRequest(socket3);
        assertEquals(1, gameServer.rooms.size());
        assertEquals("Welcome to the fancy RISK game!!!", readAllStringFromObjectStream(outputStream));
        assertEquals(3,gameServer.rooms.get(0).players.size());
        assertEquals(0,gameServer.rooms.get(0).roomID);
    }
    
    @Test
    public void testAskValidRoomNum() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Player<String> player1 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("-1", "test"))), new ByteArrayOutputStream());
        Player<String> player2 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("abc", "10", "0"))), outputStream);
        int roomID = 0;
        GameServer gameServer = new GameServer(null);
        gameServer.rooms.put(roomID, new RoomController(roomID, player1, new MapDataBase<String>()));
        assertEquals(roomID, gameServer.askValidRoomNum(player2));
        assertEquals("Invalid choice, try again".repeat(2), readAllStringFromObjectStream(outputStream));
    }
    
    @Test
    public void testMain() throws IOException, InterruptedException, ClassNotFoundException {
        Thread th = new Thread(()->{
            try {
                GameServer.main(null);
            } catch (IOException ignored) {
            }
        });
        th.start();
        Thread.sleep(100);

        Client client = new Client();
        client.init("127.0.0.1", 12345);
        assertEquals("Welcome to the fancy RISK game!!!", client.recv());
        client.send("-1");

        Thread.sleep(2000);

        th.interrupt();
        th.join();
    }
    

} 
