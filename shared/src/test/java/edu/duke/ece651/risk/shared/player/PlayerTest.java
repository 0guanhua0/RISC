package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static edu.duke.ece651.risk.shared.Mock.readAllStringFromObjectStream;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerTest {
    @Test
    void constructor() throws IOException {
        Player<String> p1 = new PlayerV1<String>("Red", 1);
        assertTrue(p1.territories.isEmpty());
        assertTrue(p1.color.equals("Red"));
        assertTrue(1==p1.id);
        assertThrows(IllegalArgumentException.class, ()->{new PlayerV1<String>("Red",0);});

        Player<String> p2 = new PlayerV1<String>(setupMockInput(new ArrayList<Object>()), new ByteArrayOutputStream());
        assertTrue(p2.territories.isEmpty());
        assertThrows(IllegalArgumentException.class, ()->p2.setId(0));
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

        assertThrows(IllegalArgumentException.class, ()->new PlayerV1<String>(0, setupMockInput(new ArrayList<Object>()), new ByteArrayOutputStream()));
    }

    @Test
    void addTerritory() throws IOException {
        Player<String> p1 = new PlayerV1<String>("Red",1);
        TerritoryV1 n1 = new TerritoryV1("n1");
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> n1Neigh = new HashSet<Territory>(){{
            add(n2);
        }};
        p1.addTerritory(n1);
        assertThrows(IllegalArgumentException.class,()->{p1.addTerritory(n1);});
        p1.addTerritory(n2);
        assertEquals(2, p1.getTerrNum());
        assertTrue(p1.territories.contains(n1));
        assertTrue(p1.territories.contains(n2));
        assertTrue(1==n1.getOwner());
        assertTrue(1==n2.getOwner());
    }

    @Test
    void loseTerritory() throws IOException {
        PlayerV1<String> p1 = new PlayerV1<String>("Red",1);
        TerritoryV1 n1 = new TerritoryV1("n1");
        int owner = n1.getOwner();
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> n1Neigh = new HashSet<Territory>(){{
            add(n2);
        }};
        p1.addTerritory(n1);
        p1.addTerritory(n2);
        assertEquals(2, p1.getTerrNum());
        p1.loseTerritory(n1);
        assertEquals(1, p1.getTerrNum());
        TerritoryV1 n3 = new TerritoryV1("n3");
        assertThrows(IllegalArgumentException.class, ()-> p1.loseTerritory(n3));

        assertTrue(!p1.territories.contains(n1));
        assertTrue(p1.territories.contains(n2));
        assertTrue(n1.isFree());
        assertTrue(!n2.isFree());
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
        Player p1 = new PlayerV2(socket1.getInputStream(), socket1.getOutputStream());

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
        Player p2 = new PlayerV2(socket2.getInputStream(), socket2.getOutputStream());

        p2.setIn(p1.getIn());
        p2.setOut(p1.getOut());
        assertEquals(s1, p2.recv());

        p2.send(s2);
        assertEquals(s2, readAllStringFromObjectStream(outputStream1));
    }

    /*
    @Test
    void IOexption() throws IOException {
        ServerSocket s = new ServerSocket(12345);
        Socket server =  s.accept();

        Socket socket1 = new Socket("127.0.0.1", 12345);

        Player p1 = new PlayerV2(socket1.getInputStream(), socket1.getOutputStream());
        p1.send("hello");

        DataInputStream in = new DataInputStream(server.getInputStream());

        System.out.println(in.readUTF());


    }

     */

}