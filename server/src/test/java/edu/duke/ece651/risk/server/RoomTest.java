package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.*;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static edu.duke.ece651.risk.shared.Constant.*;
import static edu.duke.ece651.risk.shared.Mock.readAllStringFromObjectStream;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {

    @Test
    void testConstructor() throws IOException, ClassNotFoundException {
        assertThrows(IllegalArgumentException.class,()->{new Room(-3,null, new MapDataBase<String>());});
        assertThrows(IllegalArgumentException.class,()->{new Room(-3, new MapDataBase<String>());});

        String m1 = "hogwarts";
        String r1 = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + m1 + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + r1 + "\" }";

        String m2 = "";
        String r2 = "1";

        String s12 = "{\"" + MAP_NAME + "\": \"" + m2 + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + r2 + "\" }";

        String m3 = "a clash of kings";
        String r3 = "1";

        String s13 = "{\"" + MAP_NAME + "\": \"" + m3 + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + r3 + "\" }";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(s11, s12, s13))), outputStream);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        Room room = new Room(0, player, mapDataBase);
        assertEquals(room.roomID,0);
        assertEquals(room.players.size(),1);
        assertEquals(room.players.get(0).getId(),1);
        assertEquals(room.map.getAtlas().size(), mapDataBase.getMap("a clash of kings").getAtlas().size());
    }

    @Test
    public void testAddPlayer() throws IOException, ClassNotFoundException {
        // prepare the DataBase
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        String m1 = "test";
        String r1 = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + m1 + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + r1 + "\" }";

        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(s11))), stream);
        Room room = new Room(0, player, mapDataBase);
        room.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
        room.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
        // already have enough players, will not add
        room.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
        room.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));

        assertEquals(room.players.size(),3);
        assertEquals(room.players.size(), room.map.getColorList().size());
    }

    @Test
    public void testSendAll() throws IOException, ClassNotFoundException {
        // prepare the DataBase
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        String m1 = "test";
        String r1 = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + m1 + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + r1 + "\" }";

        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(s11))), stream);
        Room room = new Room(0, player, mapDataBase);
        Player<String> player1 = new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream());
        room.addPlayer(player1);
        room.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));

        // make player1 disconnect
        assertTrue(player1.isConnect());
        player1.recv();
        assertFalse(player1.isConnect());

        // sendAll
        room.sendAll("hello");
        assertEquals(room.players.size(),3);
        assertEquals(room.players.size(), room.map.getColorList().size());
    }

    @Test
    public void testAskForMap() throws IOException, ClassNotFoundException {
        assertThrows(IllegalArgumentException.class,()->{new Room(-1,null, new MapDataBase<String>());});

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        String m1 = "hogwarts";
        String r1 = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + m1 + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + r1 + "\" }";

        String m2 = "";
        String r2 = "1";

        String s12 = "{\"" + MAP_NAME + "\": \"" + m2 + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + r2 + "\" }";

        String m3 = "a clash of kings";
        String r3 = "1";

        String s13 = "{\"" + MAP_NAME + "\": \"" + m3 + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + r3 + "\" }";

        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(s11, s12, s13))), stream);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        Room room = new Room(0, player, mapDataBase);
        assertEquals(room.roomID,0);
        assertEquals(room.players.size(),1);
        assertEquals(room.map,mapDataBase.getMap("a clash of kings"));

        ByteArrayInputStream temp = new ByteArrayInputStream(stream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(temp);
        MapDataBase<String> sendBase = (MapDataBase<String>)objectInputStream.readObject();
        assertTrue(sendBase.containsMap("a clash of kings"));
        assertTrue(sendBase.containsMap("test"));
        WorldMap<String> worldMap = sendBase.getMap("a clash of kings");
        Territory kingdom_of_the_north = worldMap.getTerritory("kingdom of the north");
        Territory kingdom_of_the_rock = worldMap.getTerritory("kingdom of the rock");
        assertTrue(kingdom_of_the_north.getNeigh().contains(kingdom_of_the_rock));
        String errorMsg = (String)objectInputStream.readObject();
        assertEquals(errorMsg,SELECT_MAP_ERROR);
        String errorMsg2 = (String)objectInputStream.readObject();
        assertEquals(errorMsg,SELECT_MAP_ERROR);
        assertEquals(SUCCESSFUL, (String)objectInputStream.readObject());
        objectInputStream.readObject(); // this is for player initial message
        assertThrows(ClassCastException.class,()->{String res = (String)objectInputStream.readObject();});
    }

    @Test
    void getWinnerId() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String map = "a clash of kings";
        String rName = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + map + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName + "\" }";


        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(s11))), outputStream);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> curMap = mapDataBase.getMap("a clash of kings");
        Room room = new Room(0, player, mapDataBase);
        try {
            room.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
        }catch (EOFException ignored){

        }
        Territory t1 = curMap.getTerritory("kingdom of the north");
        Territory t2 = curMap.getTerritory("kingdom of mountain and vale");
        Territory t3 = curMap.getTerritory("kingdom of the rock");
        Territory t4 = curMap.getTerritory("kingdom of the reach");
        Territory t5 = curMap.getTerritory("the storm kingdom");
        Territory t6 = curMap.getTerritory("principality of dorne");

        Player<String> player1 = room.players.get(0);
        Player<String> player2 = room.players.get(1);
        player1.addTerritory(t1);
        player1.addTerritory(t2);
        player1.addTerritory(t3);
        player1.addTerritory(t4);
        player1.addTerritory(t5);
        player1.addTerritory(t6);
        room.gameInfo.winnerID = -1;
        room.checkWinner();
        assertEquals(room.gameInfo.winnerID,1);

        player1.loseTerritory(t1);
        player2.addTerritory(t1);
        room.gameInfo.winnerID = -1;
        room.checkWinner();
        assertEquals(room.gameInfo.winnerID,-1);

        player2.loseTerritory(t1);
        player1.addTerritory(t1);
        room.gameInfo.winnerID = -1;
        room.checkWinner();
        assertEquals(room.gameInfo.winnerID,1);

        Territory test = new TerritoryImpl("some name",0,0,0);
        player1.addTerritory(test);
        assertThrows(IllegalStateException.class, room::checkWinner);
    }

    @Test
    void testResolveCombat() throws IOException, ClassNotFoundException {
        String map = "a clash of kings";
        String rName = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + map + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName + "\" }";


        Player<String> player1 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(s11))), new ByteArrayOutputStream());
        Player<String> player2 = new PlayerV1<>("Green", 2);

        Room room = new Room(0, player1, new MapDataBase<>());
        room.players.add(player2);

        String t1 = "the storm kingdom";
        String t2 = "kingdom of the reach";
        String t3 = "kingdom of the rock";
        String t4 = "kingdom of mountain and vale";
        String t5 = "principality of dorne";
        String t6 = "kingdom of the north";

        room.map.getTerritory(t1).addBasicUnits(8);
        room.map.getTerritory(t2).addBasicUnits(10);
        room.map.getTerritory(t3).addBasicUnits(15);
        room.map.getTerritory(t5).addBasicUnits(2);

        player1.addTerritory(room.map.getTerritory(t2));
        player1.addTerritory(room.map.getTerritory(t3));
        player2.addTerritory(room.map.getTerritory(t1));
        player2.addTerritory(room.map.getTerritory(t5));

        // attacker lose
        room.map.getTerritory(t1).addAttack(player1, new Army(1, t3, 1));
        // attacker win
        room.map.getTerritory(t5).addAttack(player1, new Army(1, t2, 10));

        assertEquals(2, player1.getTerrNum());
        assertEquals(2, player2.getTerrNum());
        room.resolveCombats();
        //TODO double  check the legality of this action
        assertEquals(3, player1.getTerrNum());
        assertEquals(1, player2.getTerrNum());
    }

    @Test
    public void testEndGame() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream p1OutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream p2OutStream = new ByteArrayOutputStream();

        String map = "a clash of kings";
        String rName = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + map + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName + "\" }";


        Player<String> player1 = new PlayerV1<>("Red", 1, setupMockInput(new ArrayList<>(Arrays.asList(s11))), p1OutStream);
        Player<String> player2 = new PlayerV1<>("Blue", 2, setupMockInput(new ArrayList<>()), p2OutStream);

        Room room = new Room(0, player1, new MapDataBase<>());
        room.players.add(player2);
        room.gameInfo.winnerID = 1;
        room.endGame();
        ObjectInputStream s1 = new ObjectInputStream(new ByteArrayInputStream(p1OutStream.toByteArray()));
        ObjectInputStream s2 = new ObjectInputStream(new ByteArrayInputStream(p2OutStream.toByteArray()));
        s1.readObject(); // mapDataBase
        s1.readObject(); // successful
        s1.readObject(); // player info
        s1.readObject();
        assertEquals(YOU_WINS, s1.readObject());
        assertEquals("Winner is the null player.", s2.readObject());
    }

    @Test
    public void testRunGame() throws IOException, ClassNotFoundException, InterruptedException {
        //valid select group of objects for p1
        Set<String> p1Group = new HashSet<>();
        p1Group.add("kingdom of the north");
        p1Group.add("kingdom of mountain and vale");
        p1Group.add("the storm kingdom");


        //valid input objects for p1
        HashMap<String, Integer> p1Chosen1  = new HashMap<>();
        p1Chosen1.put("kingdom of the north", 5);
        p1Chosen1.put("kingdom of mountain and vale", 5);
        p1Chosen1.put("the storm kingdom", 5);

        ServerSelect s1 = new ServerSelect(p1Chosen1);

        //valid select group of objects for p2
        Set<String> p2Group = new HashSet<>();
        p2Group.add("kingdom of the rock");
        p2Group.add("kingdom of the reach");
        p2Group.add("principality of dorne");

        // valid input objects for p2
        HashMap<String, Integer> p2Chosen1  = new HashMap<>();

        p2Chosen1.put("principality of dorne", 1);
        p2Chosen1.put("kingdom of the rock", 1);
        p2Chosen1.put("kingdom of the reach", 13);
        ServerSelect s2 = new ServerSelect(p2Chosen1);

        // round 1, attack two territory
        AttackAction a11 = new AttackAction("kingdom of mountain and vale", "kingdom of the rock", 1, 5);
        AttackAction a12 = new AttackAction("the storm kingdom", "principality of dorne", 1, 5);

        // round 2, gather all units, conquer the final territory
        MoveAction m21 = new MoveAction("kingdom of the north", "kingdom of the rock", 1, 6);
        MoveAction m22 = new MoveAction("kingdom of mountain and vale", "kingdom of the rock", 1, 1);

        AttackAction a21 = new AttackAction("kingdom of the rock", "kingdom of the reach", 1, 11);
        AttackAction a22 = new AttackAction("the storm kingdom", "kingdom of the reach", 1, 1);
        AttackAction a23 = new AttackAction("principality of dorne", "kingdom of the reach", 1, 4);

        String map = "a clash of kings";
        String rName = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + map + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName + "\" }";
        Player<String> player1 = new PlayerV1<>(
                setupMockInput(
                        new ArrayList<>(Arrays.asList(
                                s11,
                                p1Group,
                                s1,
                                a11,
                                a12,
                                ACTION_DONE,
                                m21,
                                m22,
                                a21,
                                a22,
                                a23,
                                ACTION_DONE

                        ))), new ByteArrayOutputStream());

        Player<String> player2 = new PlayerV1<>(
                setupMockInput(
                        new ArrayList<>(Arrays.asList(
                                p2Group,
                                s2,
                                ACTION_DONE,
                                ACTION_DONE
                        ))), new ByteArrayOutputStream());

        Room room = new Room(1, player1, new MapDataBase<>());
        room.addPlayer(player2);
        // player 1 should win the game
        Thread.sleep(500); // because the game will run in a separate thread, we will need to wait some time and then check the result
        assertEquals(1, room.gameInfo.winnerID);
    }

    @Test
    void initGame() throws IOException, ClassNotFoundException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();

        //room 0
        Room room = new Room(0, mapDataBase);

        ByteArrayOutputStream o1 = new ByteArrayOutputStream();
        String map = "a clash of kings";
        String rName = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + map + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName + "\" }";
        Player<String> p1 = new PlayerV2<>(setupMockInput(new ArrayList<>(Arrays.asList(s11))), o1);

        room.getPlayers().add(p1);
        room.initGame(mapDataBase);

        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(o1));

        //room 2
        Room room2 = new Room(0, mapDataBase);

        String map2 = "a clash of kings";
        String rName2 = "2";

        String s21 = "{\"" + MAP_NAME + "\": \"" + "xxx" + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName + "\" }";

        String s22 = "{\"" + MAP_NAME + "\": \"" + map2 + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName2 + "\" }";
        ByteArrayOutputStream o2 = new ByteArrayOutputStream();

        Player<String> p2 = new PlayerV2<>(setupMockInput(new ArrayList<>(Arrays.asList(s21, s22))), o2);

        room2.getPlayers().add(p2);
        room2.initGame(mapDataBase);

        assertEquals(SELECT_MAP_ERROR + SUCCESSFUL, readAllStringFromObjectStream(o2));
    }

    @Test
    void player() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();

        //room 0
        Room room = new Room(0, mapDataBase);
        assertNull(room.getPlayer("1"));
        assertFalse(room.hasPlayer("1"));
        assertFalse(room.isPlayerLose("1"));

        ByteArrayOutputStream o1 = new ByteArrayOutputStream();
        String map = "a clash of kings";
        String rName = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + map + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName + "\" }";
        Player<String> p1 = new PlayerV2<>(setupMockInput(new ArrayList<>(Arrays.asList(s11))), o1);

        p1.setName("1");

        //player 2
        ByteArrayOutputStream o2 = new ByteArrayOutputStream();
        String map2 = "a clash of kings";
        String rName2 = "2";

        String s21 = "{\"" + MAP_NAME + "\": \"" + map + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName + "\" }";
        Player<String> p2 = new PlayerV2<>(setupMockInput(new ArrayList<>(Arrays.asList(s21))), o2);

        p2.setName("2");


        room.getPlayers().add(p1);
        room.getPlayers().add(p2);


        assertEquals(p1, room.getPlayer("1"));
        assertNull(room.getPlayer("4"));
        assertTrue(room.hasPlayer("1"));
        assertFalse(room.hasPlayer("4"));
        assertFalse(room.isPlayerLose("4"));
        assertTrue(room.isPlayerLose("1"));

    }
}
