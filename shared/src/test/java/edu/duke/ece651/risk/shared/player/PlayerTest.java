package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static edu.duke.ece651.risk.shared.Mock.readAllStringFromObjectStream;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    @Test
    void constructor() throws IOException {
        Player<String> p1 = new PlayerV1<>("Red", 1);
        assert (p1.territories.isEmpty());
        assert (p1.color.equals("Red"));
        assert (1==p1.id);
        assertThrows(IllegalArgumentException.class, ()->{new PlayerV1<String>("Red",0);});

        Player<String> p2 = new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream());
        assertTrue(p2.territories.isEmpty());
        assertThrows(IllegalArgumentException.class, ()->p2.setId(0));
        p2.setId(2);
        p2.setColor("Green");
        assertEquals(2, p2.getId());
        assertEquals("Green", p2.getColor());

        Player<String> p3 = new PlayerV1<>(3, setupMockInput(new ArrayList<>()), new ByteArrayOutputStream());
        assertTrue(p3.territories.isEmpty());
        assertEquals(3, p3.getId());

        Player<String> p4 = new PlayerV1<>("Blue", 4, setupMockInput(new ArrayList<>()), new ByteArrayOutputStream());
        assertTrue(p4.territories.isEmpty());
        assertEquals(4, p4.getId());
        assertEquals("Blue", p4.getColor());

        assertThrows(IllegalArgumentException.class, ()->new PlayerV1<>(0, setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
    }

    @Test
    void addTerritory() throws IOException {
        Player<String> p1 = new PlayerV1<String>("Red",1);
        TerritoryV1 n1 = new TerritoryV1("n1");
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> n1Neigh = new HashSet<>(){{
            add(n2);
        }};
        p1.addTerritory(n1);
        assertThrows(IllegalArgumentException.class,()->{p1.addTerritory(n1);});
        p1.addTerritory(n2);
        assertEquals(2, p1.getTerrNum());
        assert (p1.territories.contains(n1));
        assert (p1.territories.contains(n2));
        assert (1==n1.getOwner());
        assert (1==n2.getOwner());
    }

    @Test
    void loseTerritory() throws IOException {
        PlayerV1<String> p1 = new PlayerV1<String>("Red",1);
        TerritoryV1 n1 = new TerritoryV1("n1");
        int owner = n1.getOwner();
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> n1Neigh = new HashSet<>(){{
            add(n2);
        }};
        p1.addTerritory(n1);
        p1.addTerritory(n2);
        assertEquals(2, p1.getTerrNum());
        p1.loseTerritory(n1);
        assertEquals(1, p1.getTerrNum());
        TerritoryV1 n3 = new TerritoryV1("n3");
        assertThrows(IllegalArgumentException.class, ()-> p1.loseTerritory(n3));

        assert (!p1.territories.contains(n1));
        assert (p1.territories.contains(n2));
        assert (n1.isFree());
        assert (!n2.isFree());
    }


    @Test
    void testSendRecv() throws IOException, ClassNotFoundException {
        String str1 = "hello";
        String str2 = "over";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(str1, str2))), outputStream);
        assertEquals(str1, player.recv());
        assertEquals(str2, player.recv());
        player.send(str1);
        assertEquals(str1, readAllStringFromObjectStream(outputStream));
    }

    @Test
    void testSendInfo() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Player<String> player = new PlayerV1<>("Green", 1, setupMockInput(new ArrayList<>(Arrays.asList())), outputStream);
        player.sendPlayerInfo();
        assertEquals(
                "{\"playerColor\":\"Green\",\"playerID\":1}",
                readAllStringFromObjectStream(outputStream)
        );
    }
}