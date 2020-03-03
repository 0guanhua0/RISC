package edu.duke.ece651.client;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.duke.ece651.Action;
import edu.duke.ece651.AttackAction;
import edu.duke.ece651.MoveAction;
import edu.duke.ece651.server.Server;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientTest {
    private static final int SLEEP_TIME = 500;
    // different with sever test, avoid port conflicts
    private static final int PORT = 8081;
    static Server server;

    @BeforeAll
    static void beforeAll() throws InterruptedException {
        // initialize the server
        new Thread(() -> {
            try {
                server = new Server(PORT);
                // this should throw an IO exception(address already in use)
                Server s1 = new Server(PORT);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }).start();
        // pause to give the server some time to setup
        Thread.sleep(SLEEP_TIME);
    }

    @Test
    public void testSendRecvData() throws IOException, InterruptedException {
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
        Thread.sleep(500);
        Client client = new Client();
        client.init("127.0.0.1", PORT);
        client.sendData(msgCTS);
        assertEquals(msgSTC, client.recvData());
    }

    @Test
    public void testSendActions() throws IOException, InterruptedException {
        HashMap<String, List<Action>> actions = new HashMap<>();
        List<Action> moveActions = new ArrayList<>();
        List<Action> attackActions = new ArrayList<>();

        moveActions.add(new MoveAction("A", "B"));
        attackActions.add(new AttackAction("A", "B"));

        actions.put("move", moveActions);
        actions.put("attack", attackActions);

        new Thread(() -> {
            try {
                Socket socket = server.waitBeginner();
                assertNotNull(socket);

                assertEquals(actions, Server.recvActions(socket));
                socket.shutdownOutput();
                Server.sendData(socket, "hello");
            }catch (IOException e){
                System.out.println(e.toString());
            }
        }).start();
        Thread.sleep(500);
        Client client = new Client();
        client.init("127.0.0.1", PORT);
        client.sendActions(actions);
    }

    @Test
    public void testGetHostByName() throws UnknownHostException {
        Client client = new Client();
        assertEquals("67.159.88.31", client.getHostByName("vcm-12305.vm.duke.edu"));
        assertThrows(UnknownHostException.class, ()->{client.getHostByName("hello");});
    }

} 
