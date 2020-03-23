package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.Army;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static edu.duke.ece651.risk.shared.Constant.*;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;

public class RoomControllerTest {

    @Test
    void testConstructor() throws IOException, ClassNotFoundException {
        assertThrows(IllegalArgumentException.class,()->{new RoomController(-1,null, new MapDataBase<String>());});

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("hogwarts", "", "a clash of kings"))), outputStream);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        RoomController roomController = new RoomController(0, player, mapDataBase);
        assertEquals(roomController.roomID,0);
        assertEquals(roomController.players.size(),1);
        assertEquals(roomController.players.get(0).getId(),1);
        assertEquals(roomController.players.get(0).getColor(),"red");
        assertEquals(roomController.map.getAtlas().size(), mapDataBase.getMap("a clash of kings").getAtlas().size());
    }

    @Test
    public void testAddPlayer() throws IOException, ClassNotFoundException {
        // prepare the DataBase
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("test"))), stream);
        RoomController roomController = new RoomController(0, player, mapDataBase);
        roomController.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
        roomController.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
        // already have enough players, will not add
        roomController.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
        roomController.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));

        assertEquals(roomController.players.size(),3);
        assertEquals(roomController.players.get(0).getColor(),"red");
        assertEquals(roomController.players.get(1).getColor(),"blue");
        assertEquals(roomController.players.get(2).getColor(),"black");
        assertEquals(roomController.players.size(), roomController.map.getColorList().size());
    }

    @Test
    public void testAskForMap() throws IOException, ClassNotFoundException {
        assertThrows(IllegalArgumentException.class,()->{new RoomController(-1,null, new MapDataBase<String>());});

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("hogwarts", "","a clash of kings"))), stream);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        RoomController roomController = new RoomController(0, player, mapDataBase);
        assertEquals(roomController.roomID,0);
        assertEquals(roomController.players.size(),1);
        assertEquals(roomController.map,mapDataBase.getMap("a clash of kings"));

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
        objectInputStream.readObject(); // this is for wait info
        assertThrows(EOFException.class,()->{String res = (String)objectInputStream.readObject();});
    }

    @Test
    void getWinnerId() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("a clash of kings"))), outputStream);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> curMap = mapDataBase.getMap("a clash of kings");
        RoomController roomController = new RoomController(0, player, mapDataBase);
        try {
            roomController.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
        }catch (EOFException ignored){

        }
        Territory t1 = curMap.getTerritory("kingdom of the north");
        Territory t2 = curMap.getTerritory("kingdom of mountain and vale");
        Territory t3 = curMap.getTerritory("kingdom of the rock");
        Territory t4 = curMap.getTerritory("kingdom of the reach");
        Territory t5 = curMap.getTerritory("the storm kingdom");
        Territory t6 = curMap.getTerritory("principality of dorne");

        Player<String> player1 = roomController.players.get(0);
        Player<String> player2 = roomController.players.get(1);
        player1.addTerritory(t1);
        player1.addTerritory(t2);
        player1.addTerritory(t3);
        player1.addTerritory(t4);
        player1.addTerritory(t5);
        player1.addTerritory(t6);
        roomController.gameInfo.winnerID = -1;
        roomController.checkWinner();
        assertEquals(roomController.gameInfo.winnerID,1);

        player1.loseTerritory(t1);
        player2.addTerritory(t1);
        roomController.gameInfo.winnerID = -1;
        roomController.checkWinner();
        assertEquals(roomController.gameInfo.winnerID,-1);

        player2.loseTerritory(t1);
        player1.addTerritory(t1);
        roomController.gameInfo.winnerID = -1;
        roomController.checkWinner();
        assertEquals(roomController.gameInfo.winnerID,1);

        Territory test = new TerritoryV1("some name");
        player1.addTerritory(test);
        assertThrows(IllegalStateException.class, roomController::checkWinner);
    }

    @Test
    void testResolveCombat() throws IOException, ClassNotFoundException {
        Player<String> player1 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("hogwarts", "", "a clash of kings"))), new ByteArrayOutputStream());
        Player<String> player2 = new PlayerV1<>("Green", 2);

        RoomController roomController = new RoomController(0, player1, new MapDataBase<>());
        roomController.players.add(player2);

        String t1 = "the storm kingdom";
        String t2 = "kingdom of the reach";
        String t3 = "kingdom of the rock";
        String t4 = "kingdom of mountain and vale";
        String t5 = "principality of dorne";
        String t6 = "kingdom of the north";

        roomController.map.getTerritory(t1).addNUnits(8);
        roomController.map.getTerritory(t2).addNUnits(10);
        roomController.map.getTerritory(t3).addNUnits(15);
        roomController.map.getTerritory(t5).addNUnits(2);

        player1.addTerritory(roomController.map.getTerritory(t2));
        player1.addTerritory(roomController.map.getTerritory(t3));
        player2.addTerritory(roomController.map.getTerritory(t1));
        player2.addTerritory(roomController.map.getTerritory(t5));

        // attacker lose
        roomController.map.getTerritory(t1).addAttack(1, new Army(1, t3, 1));
        // attacker win
        roomController.map.getTerritory(t5).addAttack(1, new Army(1, t2, 10));

        assertEquals(2, player1.getTerrNum());
        assertEquals(2, player2.getTerrNum());
        roomController.resolveCombats();
        assertEquals(2, player1.getTerrNum());
        assertEquals(2, player2.getTerrNum());
    }

    @Test
    public void testEndGame() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream p1OutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream p2OutStream = new ByteArrayOutputStream();

        Player<String> player1 = new PlayerV1<>("Red", 1, setupMockInput(new ArrayList<>(Arrays.asList("a clash of kings"))), p1OutStream);
        Player<String> player2 = new PlayerV1<>("Blue", 2, setupMockInput(new ArrayList<>()), p2OutStream);

        RoomController roomController = new RoomController(0, player1, new MapDataBase<>());
        roomController.players.add(player2);
        roomController.gameInfo.winnerID = 1;
        roomController.endGame();
        ObjectInputStream s1 = new ObjectInputStream(new ByteArrayInputStream(p1OutStream.toByteArray()));
        ObjectInputStream s2 = new ObjectInputStream(new ByteArrayInputStream(p2OutStream.toByteArray()));
        s1.readObject(); // mapDataBase
        s1.readObject(); // successful
        s1.readObject(); // player info
        s1.readObject(); // wait info
        assertEquals(YOU_WINS, s1.readObject());
        assertEquals("Winner is the red player.", s2.readObject());
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

        Player<String> player1 = new PlayerV1<>(
                setupMockInput(
                        new ArrayList<>(Arrays.asList(
                                "a clash of kings",
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

        RoomController roomController = new RoomController(1, player1, new MapDataBase<>());
        roomController.addPlayer(player2);
        // player 1 should win the game
        Thread.sleep(500); // because the game will run in a separate thread, we will need to wait some time and then check the result
        assertEquals(1, roomController.gameInfo.winnerID);
    }
}
