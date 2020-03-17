package edu.duke.ece651.risk.shared.network;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static edu.duke.ece651.risk.shared.Mock.recv;
import static edu.duke.ece651.risk.shared.Mock.send;
import static org.junit.jupiter.api.Assertions.*;

public class CommunicationTest {
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
            } catch (IOException ignored) {
            }
        }).start();
        // pause to give the server some time to setup
        Thread.sleep(SLEEP_TIME);
    }

    @Test
    public void testAccept() throws IOException {
        new Thread(() -> {
            Socket socket = server.accept();
            assertNotNull(socket);
        }).start();
        new Client("localhost", PORT);
    }
    
    @Test
    public void testAcceptNull() throws IOException {
        Server s = new Server();
        s.serverSocket.close();
        assertNull(s.accept());
    }
    
    @Test
    public void testSendRecvStr() throws IOException, ClassNotFoundException {
        String msgCTS = "Hello server";
        String msgSTC = "Hi client";

        new Thread(() -> {
            try {
                Socket socket = server.accept();
                assertNotNull(socket);

                assertEquals(msgCTS, recv(socket.getInputStream()));
                send(socket.getOutputStream(), msgSTC);
            }catch (IOException | ClassNotFoundException ignored){
            }
        }).start();
        Client client = new Client();
        client.init("127.0.0.1", PORT);
        client.send(msgCTS);
        assertEquals(msgSTC, client.recv());
    }

    @Test
    public void testSendActions() throws IOException, InterruptedException, ClassNotFoundException {
        HashMap<String, List<Action>> actions = new HashMap<>();
        List<Action> moveActions = new ArrayList<>();
        List<Action> attackActions = new ArrayList<>();

        moveActions.add(new MoveAction("A", "B", 1, 1));

        actions.put("move", moveActions);
        actions.put("attack", attackActions);

        new Thread(() -> {
            try {
                Socket socket = server.accept();
                assertNotNull(socket);

                send(socket.getOutputStream(), actions);
            }catch (IOException ignored){
            }
        }).start();
        Thread.sleep(500);
        Client client = new Client();
        client.init("127.0.0.1", PORT);
        HashMap<?, List<Action>> actionsRec = (HashMap<?, List<Action>>) client.recv();
        assertEquals(actions.size(), actionsRec.size());
        assertEquals(actions.get("move").size(), actionsRec.get("move").size());
    }

    @Test
    public void testSendWorldMap() throws IOException, InterruptedException, ClassNotFoundException {
        WorldMap<String> map = new MapDataBase<String>().getMap("a clash of kings");

        new Thread(() -> {
            try {
                Socket socket = server.accept();
                assertNotNull(socket);

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(map);
                objectOutputStream.flush();

                socket.shutdownOutput();
                send(socket.getOutputStream(), "hello");
            }catch (IOException ignored){
            }
        }).start();
        Thread.sleep(500);
        Client client = new Client();
        client.init("127.0.0.1", PORT);
        WorldMap<?> map1 = (WorldMap<?>) client.recv();

        assertEquals(map.getAtlas().size(), map1.getAtlas().size());
    }

    @Test
    public void testGetHostByName() throws UnknownHostException {
        Client client = new Client();
        assertEquals("67.159.88.31", client.getHostByName("vcm-12305.vm.duke.edu"));
        assertThrows(UnknownHostException.class, ()->{client.getHostByName("hello");});
    }
} 
