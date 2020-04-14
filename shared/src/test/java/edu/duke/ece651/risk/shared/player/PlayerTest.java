package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryImpl;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static edu.duke.ece651.risk.shared.Mock.readAllStringFromObjectStream;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {
    @Test
    void constructor() throws IOException {
        Player<String> p1 = new PlayerV1<String>("Red", 1);
        assertTrue(p1.territories.isEmpty());
        assertEquals("Red", p1.color);
        assertEquals(1, p1.id);
        assertThrows(IllegalArgumentException.class, () -> {
            new PlayerV1<String>("Red", 0);
        });

        Player<String> p2 = new PlayerV1<String>(setupMockInput(new ArrayList<Object>()), new ByteArrayOutputStream());
        assertTrue(p2.territories.isEmpty());
        assertThrows(IllegalArgumentException.class, () -> p2.setId(0));
        p2.setId(2);
        p2.setColor("Green");
        assertEquals(2, p2.getId());
        assertEquals("Green", p2.getColor());

        Player<String> p3 = new PlayerV1<String>(3, setupMockInput(new ArrayList<Object>()), new ByteArrayOutputStream());
        assertTrue(p3.territories.isEmpty());
        assertEquals(3, p3.getId());

        Player<String> p4 = new PlayerV1<String>("Blue", 4, setupMockInput(new ArrayList<Object>()), new ByteArrayOutputStream());
        assertTrue(p4.territories.isEmpty());
        assertEquals(4, p4.getId());
        assertEquals("Blue", p4.getColor());

        assertThrows(IllegalArgumentException.class, () -> new PlayerV1<String>(0, setupMockInput(new ArrayList<Object>()), new ByteArrayOutputStream()));
    }

    @Test
    void addTerritory() throws IOException {
        Player<String> p1 = new PlayerV1<String>("Red", 1);
        Territory n1 = new TerritoryImpl("n1",0,0,0);
        Territory n2 = new TerritoryImpl("n2",0,0,0);
        HashSet<Territory> n1Neigh = new HashSet<Territory>() {{
            add(n2);
        }};
        p1.addTerritory(n1);
        assertThrows(IllegalArgumentException.class, () -> {
            p1.addTerritory(n1);
        });
        p1.addTerritory(n2);
        assertEquals(2, p1.getTerrNum());
        assertTrue(p1.territories.contains(n1));
        assertTrue(p1.territories.contains(n2));
        assertEquals(1, n1.getOwner());
        assertEquals(1, n2.getOwner());
    }

    @Test
    void loseTerritory() throws IOException {
        PlayerV1<String> p1 = new PlayerV1<String>("Red", 1);
        Territory n1 = new TerritoryImpl("n1",0,0,0);
        int owner = n1.getOwner();
        Territory n2 = new TerritoryImpl("n2",0,0,0);
        HashSet<Territory> n1Neigh = new HashSet<Territory>() {{
            add(n2);
        }};
        p1.addTerritory(n1);
        p1.addTerritory(n2);
        assertEquals(2, p1.getTerrNum());
        p1.loseTerritory(n1);
        assertEquals(1, p1.getTerrNum());
        Territory n3 = new TerritoryImpl("n3",0,0,0);
        assertThrows(IllegalArgumentException.class, () -> p1.loseTerritory(n3));

        assertFalse(p1.territories.contains(n1));
        assertTrue(p1.territories.contains(n2));
        assertTrue(n1.isFree());
        assertFalse(n2.isFree());
    }


    @Test
    void testSendRecv() throws IOException, ClassNotFoundException {
        String str1 = "hello";
        String str2 = "over";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Player<String> player = new PlayerV1<String>(setupMockInput(new ArrayList<Object>(Arrays.asList(str1, str2))), outputStream);
        assertEquals(str1, player.recv());
        assertEquals(str2, player.recv());
        player.send(str1);
        assertEquals(str1, readAllStringFromObjectStream(outputStream));
    }

    @Test
    void testSendIO() throws IOException {
        ByteArrayOutputStream out = mock(ByteArrayOutputStream.class);
        Player<String> player = new PlayerV1<String>(setupMockInput(new ArrayList<Object>(Arrays.asList("str1"))), out);
        doThrow(new IOException())
                .when(out)
                .flush();
        assertTrue(player.isConnect);
        player.send("1");
        assertFalse(player.isConnect);
    }

    @Test
    void testRecvIO() throws IOException, ClassNotFoundException {
        // NOTE: EOFException extends IOException, so we only need to produce an EOF(which a empty stream can achieve)
        Player<String> player = new PlayerV1<String>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream());
        assertTrue(player.isConnect);
        player.recv();
        assertFalse(player.isConnect);
    }

    @Test
    void testSendInfo() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Player<String> player = new PlayerV1<String>("Green", 1, setupMockInput(new ArrayList<Object>(Arrays.asList())), outputStream);
        player.sendPlayerInfo();
        assertEquals(
                "{\"playerColor\":\"Green\",\"playerID\":1}",
                readAllStringFromObjectStream(outputStream)
        );
    }

    @Test
    void connect() throws IOException, ClassNotFoundException {
        String s1 = "1`";
        String s2 = "2";

        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);
        when(socket1.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s1, s2))));
        when(socket1.getOutputStream()).thenReturn(outputStream1);
        Player<String> p1 = new PlayerV2<String>(socket1.getInputStream(), socket1.getOutputStream());

        p1.setName("a");
        assertEquals("a", p1.getName());

        assertTrue(p1.isConnect());
        p1.setConnect(false);
        assertFalse(p1.isConnect());


        ByteArrayOutputStream o2 = new ByteArrayOutputStream();
        Socket socket2 = mock(Socket.class);
        when(socket2.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList(s1, s2))));
        when(socket2.getOutputStream()).thenReturn(o2);
        Player<String> p2 = new PlayerV2<String>(socket2.getInputStream(), socket2.getOutputStream());

        p2.setIn(p1.getIn());
        p2.setOut(p1.getOut());
        assertEquals(s1, p2.recv());

        p2.send(s2);
        assertEquals(s2, readAllStringFromObjectStream(outputStream1));
    }

    @Test
    void setAllyRequest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlayerV2<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        player.setAllyRequest(1);
        assertEquals(1,player.allyRequest);
        assertThrows(IllegalArgumentException.class,()->{player.setAllyRequest(2);});
    }


    @Test
    void hasRecvAlly() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlayerV2<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        player.setAllyRequest(1);
        assertTrue(player.hasRecvAlly());
    }

    @Test
    void canAllyWith() throws IOException {
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        assertFalse(player1.canAllyWith(player2));
        player1.setAllyRequest(2);
        assertFalse(player1.canAllyWith(player2));
        assertFalse(player2.canAllyWith(player1));

        player2.setAllyRequest(1);
        assertTrue(player1.canAllyWith(player2));
        assertTrue(player2.canAllyWith(player1));
    }

    @Test
    void allyWith() throws IOException {
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        assertThrows(IllegalArgumentException.class,()->{player1.allyWith(player2);});
        player1.setAllyRequest(2);
        player2.setAllyRequest(1);
        assertDoesNotThrow(()->{player1.allyWith(player2);});
        assertTrue(player1.ally==player2);
        assertTrue(player2.ally==player1);
        for (Territory territory : player1.territories) {
            assertEquals(2,territory.getAllyId());
        }
        for (Territory territory : player2.territories) {
            assertEquals(1,territory.getAllyId());
        }
    }

    @Test
    void hasAlly() throws IOException {
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        player1.setAllyRequest(2);
        player2.setAllyRequest(1);
        player1.allyWith(player2);
        assertTrue(player1.hasAlly());
        assertTrue(player2.hasAlly());
    }

    @Test
    void getAlly() throws IOException {
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        player1.setAllyRequest(2);
        player2.setAllyRequest(1);
        player1.allyWith(player2);
        assertEquals(player1,player2.getAlly());
        assertEquals(player2,player1.getAlly());
    }

    @Test
    void isAllyWith() throws IOException {
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);

        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        player1.addTerritory(test);

        player1.setAllyRequest(2);
        player2.setAllyRequest(1);
        player1.allyWith(player2);

        assertTrue(player1.isAllyWith(player2));
        assertTrue(player2.isAllyWith(player1));
        player1.updateState();
        player2.updateState();
        assertTrue(player1.isAllyWith(player2));
        assertTrue(player2.isAllyWith(player1));
    }

    @Test
    void ruputureAlly() throws IOException {

        Player<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        Player<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        assertThrows(IllegalStateException.class,()->{player1.ruptureAlly();});
        player1.setId(1);
        player2.setId(2);

        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        player1.addTerritory(test);

        player1.setAllyRequest(2);
        player2.setAllyRequest(1);
        player1.allyWith(player2);

        assertEquals(2,test.getAllyId());
        assertDoesNotThrow(()->{player1.ruptureAlly();});
        assertEquals(null,player1.ally);
        assertEquals(-1,player1.allyRequest);
        assertEquals(null,player2.ally);
        assertEquals(-1,player2.allyRequest);
        assertEquals(-1,test.getAllyId());

    }

    @Test
    void setTerrAlly() throws IOException {
        Player<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        Player<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        assertThrows(IllegalStateException.class,()->{player1.ruptureAlly();});
        player1.setId(1);
        player2.setId(2);

        TerritoryImpl test = new TerritoryImpl("test", 3, 20, 20);
        player1.addTerritory(test);
        TerritoryImpl test2 = new TerritoryImpl("test2", 3, 20, 20);
        player1.addTerritory(test2);


        player1.setAllyRequest(2);
        player2.setAllyRequest(1);
        player1.allyWith(player2);

        player1.setTerrAlly();
        assertEquals(2,test.getAllyId());
        assertEquals(2,test2.getAllyId());




    }


    /*
    @Test
    void IOexption() throws IOException, InterruptedException {
        new Thread(new Server()).start();

        Thread.sleep(1000);

        Socket socket1 = new Socket("127.0.0.1", 5555);

        Socket socket2 = mock(Socket.class);

        when(socket2.getInputStream())
                .thenReturn(setupMockInput(new ArrayList<>(Arrays.asList("0", "0"))));

        ObjectOutputStream o = new ObjectOutputStream(socket1.getOutputStream());
        Player p1 = new PlayerV2(socket2.getInputStream(), socket1.getOutputStream());

        assertTrue(p1.isConnect());
        p1.send("hello");
        p1.send("hello");
        p1.send("hello");


    }


}

//client thread for testing purpose
class ServerThread implements Runnable {
    private Socket client = null;

    public ServerThread(Socket s) {
        this.client = s;
    }

    @Override
    public void run() {
        try {
            PrintStream out = new PrintStream(client.getOutputStream());
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String str = buf.readLine();
            System.out.println(str);
            out.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class Server implements Runnable {

    @Override
    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(5555);
        } catch (IOException ignored) {
        }

        Socket client = null;
        try {
            client = server.accept();
        } catch (IOException ignored) {
        }
        new Thread(new ServerThread(client)).start();

        try {
            server.close();
        } catch (IOException ignored) {
        }

    }

     */
}

