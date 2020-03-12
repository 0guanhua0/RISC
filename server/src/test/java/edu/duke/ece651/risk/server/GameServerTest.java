package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.network.Client;
import edu.duke.ece651.risk.shared.network.Server;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

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
        Socket socket = mock(Socket.class);
        when(socket.getInputStream())
                .thenReturn(new ByteArrayInputStream("-1".getBytes()));
        when(socket.getOutputStream()).thenReturn(outputStream);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.close();
        Socket socketError = mock(Socket.class);
        when(socketError.getOutputStream()).thenReturn(stream);

        Server server = mock(Server.class);

        when(server.accept()).thenReturn(null).thenReturn(socket).thenReturn(socketError);

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
    public void testHandleIncomeRequest() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);
        when(socket1.getInputStream())
                .thenReturn(new ByteArrayInputStream("-1".getBytes()));
        when(socket1.getOutputStream()).thenReturn(outputStream);

        GameServer gameServer = new GameServer(null);
        assertEquals(0, gameServer.rooms.size());
        gameServer.handleIncomeRequest(socket1);
        assertEquals(1, gameServer.rooms.size());
        assertEquals("Welcome to the fancy RISK game!!!\n", outputStream.toString());

        outputStream.reset();
        Socket socket2 = mock(Socket.class);
        when(socket2.getInputStream())
                .thenReturn(new ByteArrayInputStream("0".getBytes()));
        when(socket2.getOutputStream()).thenReturn(outputStream);

        assertEquals(1, gameServer.rooms.size());
        gameServer.handleIncomeRequest(socket2);
        assertEquals(1, gameServer.rooms.size());
        assertEquals("Welcome to the fancy RISK game!!!\n", outputStream.toString());

        outputStream.reset();
        Socket socket3 = mock(Socket.class);
        when(socket3.getInputStream())
                .thenReturn(new ByteArrayInputStream("0".getBytes()));
        when(socket3.getOutputStream()).thenReturn(outputStream);

        assertEquals(1, gameServer.rooms.size());
        gameServer.handleIncomeRequest(socket3);
        assertEquals(1, gameServer.rooms.size());
        assertEquals("Welcome to the fancy RISK game!!!\n", outputStream.toString());
    }
    
    @Test
    public void testAskValidRoomNum() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Socket socket = mock(Socket.class);
        when(socket.getInputStream())
                .thenReturn(new ByteArrayInputStream("abc".getBytes()))
                .thenReturn(new ByteArrayInputStream("10".getBytes()))
                .thenReturn(new ByteArrayInputStream("0".getBytes()));
        when(socket.getOutputStream()).thenReturn(outputStream);

        int roomID = 0;
        GameServer gameServer = new GameServer(null);
        gameServer.rooms.put(roomID, new RoomController(roomID, null));

        assertEquals(roomID, gameServer.askValidRoomNum(socket));
        assertEquals("Invalid choice, try again\n", outputStream.toString());

        verify(socket, atLeast(3)).getInputStream();
    }
    
    @Test
    public void testMain() throws IOException, InterruptedException {
        Thread th = new Thread(()->{
            try {
                GameServer.main(null);
            } catch (IOException e) {
            }
        });
        th.start();
        Thread.sleep(100);

        Client client = new Client();
        client.init("127.0.0.1", 12345);
        assertEquals("Welcome to the fancy RISK game!!!", client.recvData());
        client.send("-1");

        Thread.sleep(1000);

        th.interrupt();
    }
    

} 
