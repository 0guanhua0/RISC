package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.network.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SceneCLITest {
    private static final String t1 = "The Storm Kingdom";
    private static final String t2 = "Kingdom of the Reach";
    private static final String t3 = "Kingdom of the Rock";
    private static final String t4 = "Kingdom of Mountain and Vale";
    private static final String t5 = "Kingdom of the North";
    private static final String t6 = "Principality of Dorne";

    static ByteArrayOutputStream outContent;

    static WorldMap<String> map;
    static Map<Integer, String> idToColor;

    @BeforeAll
    static void beforeAll() throws IOException {
        outContent = new ByteArrayOutputStream();

        map = new MapDataBase<String>().getMap("a clash of kings");
        Territory territory1 = map.getTerritory(t1);
        Territory territory2 = map.getTerritory(t2);
        Territory territory3 = map.getTerritory(t3);
        Territory territory4 = map.getTerritory(t4);
        Territory territory5 = map.getTerritory(t5);
        Territory territory6 = map.getTerritory(t6);

        territory1.addNUnits(1);
        territory2.addNUnits(2);
        territory3.addNUnits(3);
        territory4.addNUnits(4);
        territory5.addNUnits(5);
        territory6.addNUnits(6);

        territory1.setOwner(1);
        territory2.setOwner(1);
        territory3.setOwner(2);
        territory4.setOwner(2);
        territory5.setOwner(3);

        idToColor = new HashMap<>();
        idToColor.put(1, "Green");
        idToColor.put(2, "Blue");
        idToColor.put(3, "Red");
    }

    @AfterAll
    static void afterAll() {
        System.setOut(System.out);
        System.setIn(System.in);
    }

    @BeforeEach
    public void beforeEach() {
        // empty the stdout before each function call
        outContent.reset();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void showMap1() {
        // hard to test, the set is unordered, so the result is undetermined
        SceneCLI.showMap(map);
    }

    @Test
    void showMap2() {
        // hard to test, the set is unordered, so the result is undetermined
        SceneCLI.showMap(map, idToColor);
    }

    @Test
    void showTerritory() {
        String expected = "10 units in t1 (next to: t2)\n";
        Territory t1 = new TerritoryV1("t1");
        Territory t2 = new TerritoryV1("t2");
        t1.addNUnits(10);
        Set<Territory> s = new HashSet<>();
        s.add(t2);
        t1.setNeigh(s);
        SceneCLI.showTerritory(t1);
        assertEquals(expected, outContent.toString());
    }
}