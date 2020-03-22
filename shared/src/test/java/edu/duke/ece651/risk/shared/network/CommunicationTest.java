package edu.duke.ece651.risk.shared.network;

import edu.duke.ece651.risk.shared.ToClientMsg.RoundInfo;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.duke.ece651.risk.shared.Constant.ACTION_ATTACK;
import static edu.duke.ece651.risk.shared.Constant.ACTION_MOVE;
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
            try {
                Socket socket = server.accept();
                assertNotNull(socket);
                new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException ignored) {
            }
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

                Player<String> player = new PlayerV1<>(socket.getInputStream(), socket.getOutputStream());

                assertEquals(msgCTS, player.recv());
                player.send(msgSTC);
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

        actions.put(ACTION_MOVE, moveActions);
        actions.put(ACTION_ATTACK, attackActions);

        new Thread(() -> {
            try {
                Socket socket = server.accept();
                assertNotNull(socket);

                Player<String> player = new PlayerV1<>(socket.getInputStream(), socket.getOutputStream());

                player.send(actions);
            }catch (IOException ignored){
            }
        }).start();
        Thread.sleep(500);
        Client client = new Client();
        client.init("127.0.0.1", PORT);
        HashMap<?, List<Action>> actionsRec = (HashMap<?, List<Action>>) client.recv();
        assertEquals(actions.size(), actionsRec.size());
        assertEquals(actions.get(ACTION_MOVE).size(), actionsRec.get(ACTION_MOVE).size());
        assertEquals(actions.get(ACTION_ATTACK).size(), actionsRec.get(ACTION_ATTACK).size());
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
    public void testSendRoundInfo() throws IOException, InterruptedException, ClassNotFoundException {
        WorldMap<String> map = new MapDataBase<String>().getMap("a clash of kings");
        String t1 = "the storm kingdom";
        String t2 = "kingdom of the reach";
        String t3 = "kingdom of the rock";
        String t4 = "kingdom of mountain and vale";
        String t5 = "kingdom of the north";
        String t6 = "principality of dorne";
        map.getAtlas().get(t1).setOwner(1);
        map.getAtlas().get(t2).setOwner(1);
        map.getAtlas().get(t3).setOwner(1);
        map.getAtlas().get(t4).setOwner(2);
        map.getAtlas().get(t5).setOwner(2);
        map.getAtlas().get(t6).setOwner(2);

        Map<Integer, String> idToColor = new HashMap<>();
        idToColor.put(1, "green");
        idToColor.put(2, "red");

        RoundInfo roundInfo = new RoundInfo(1, map, idToColor);

        new Thread(() -> {
            try {
                Socket socket = server.accept();
                assertNotNull(socket);

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(roundInfo);
                objectOutputStream.flush();

            }catch (IOException ignored){
            }
        }).start();
        Thread.sleep(500);
        Client client = new Client();
        client.init("127.0.0.1", PORT);
        RoundInfo round1 = (RoundInfo) client.recv();

        WorldMap<String> recMap = round1.getMap();
        assertEquals(1, recMap.getAtlas().get(t1).getOwner());
        assertEquals(1, recMap.getAtlas().get(t2).getOwner());
        assertEquals(1, recMap.getAtlas().get(t3).getOwner());

        assertEquals(2, recMap.getAtlas().get(t4).getOwner());
        assertEquals(2, recMap.getAtlas().get(t5).getOwner());
        assertEquals(2, recMap.getAtlas().get(t6).getOwner());

        assertEquals(map.getAtlas().size(), recMap.getAtlas().size());
    }

    @Test
    public void testGetHostByName() throws UnknownHostException {
        Client client = new Client();
        assertEquals("67.159.88.31", client.getHostByName("vcm-12305.vm.duke.edu"));
        assertThrows(UnknownHostException.class, ()->{client.getHostByName("hello");});
    }
} 
