package edu.duke.ece651.server;

import edu.duke.ece651.client.Client;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {
    private static final int SLEEP_TIME = 500;
    private static final int PORT = 8080;
    static Server server;

    @BeforeAll
    static void beforeAll() throws InterruptedException {
        // initialize the server
        new Thread(() -> {
            try {
                server = new Server(PORT);
                // this should throw an IO exception
                Server s1 = new Server(PORT);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }).start();
        // pause to give the server some time to setup
        Thread.sleep(SLEEP_TIME);
    }

    @Test
    public void testWaitBeginner() throws IOException {
        new Thread(() -> {
            Socket socket = server.waitBeginner();
            assertNotNull(socket);
        }).start();
        new Client("localhost", PORT);
    }
    
    @Test
    public void testWaitAllPlayers() throws IOException {
        int cnt = 2;
        new Thread(() -> {
            List<Socket> players = server.waitAllPlayers(cnt);
            assertEquals(players.size(), cnt);
        }).start();
        for (int i = 0; i < cnt; i++){
            new Client().init("127.0.0.1", PORT);
        }
    }

    @Test
    public void testWaitAllPlayersNull() throws IOException, InterruptedException {
        int cnt = 2;
        int port = 12345;
        Server s1 = new Server(port);
        s1.serverSocket.setSoTimeout(100);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                for (int i = 0; i < cnt; i++){
                    new Client().init("127.0.0.1", port);
                    //Thread.sleep(2000);
                }
                new Client().init("hello", port);
            }catch (IOException | InterruptedException e){
                System.out.println(e.toString());
            }
        }).start();
        List<Socket> players = s1.waitAllPlayers(cnt);
        assertEquals(players.size(), cnt);
    }
    
    @Test
    public void testAcceptOrNull() throws IOException {
        Server s = new Server();
        s.serverSocket.close();
        assertNull(s.acceptOrNull());
    }
    
    @Test
    public void testSendRecvData() throws IOException {
        String msgCTS = "Hello server";
        String msgSTC = "Hi client";

        new Thread(() -> {
            try {
                Socket socket = server.waitBeginner();
                assertNotNull(socket);
                assertEquals(msgCTS, Server.recvData(socket));
                Server.sendData(socket, msgSTC);
                socket.shutdownOutput();
                Server.sendData(socket, msgSTC);
            }catch (IOException e){
                System.out.println(e.toString());
            }
        }).start();
        Client client = new Client();
        client.init("127.0.0.1", PORT);
        client.sendData(msgCTS);
        assertEquals(msgSTC, client.recvData());
    }

} 
