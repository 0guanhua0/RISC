package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
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
        try {
            roomController.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
            roomController.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
        }catch (EOFException ignored){
            // we only want to test if we can add
        }
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
    void testSelectTerritory() throws IOException, ClassNotFoundException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();

        // first invalid input objects for p1
        HashMap<String, Integer> p1Chosen1 = new HashMap<>();
        p1Chosen1.put("kingdom of the north", 5);
        p1Chosen1.put("kingdom of mountain and vale", 5);
        ServerSelect s11 = new ServerSelect(p1Chosen1);

        // valid input objects for p1
        HashMap<String, Integer> p1Chosen2  = new HashMap<>();
        p1Chosen2.put("kingdom of the north", 5);
        p1Chosen2.put("kingdom of mountain and vale", 5);
        p1Chosen2.put("the storm kingdom", 5);
        ServerSelect s12 = new ServerSelect(p1Chosen2);

        // first invalid input objects for p2
        HashMap<String, Integer> p2Chosen1  = new HashMap<>();
        p1Chosen2.put("the storm kingdom", 5);
        p2Chosen1.put("kingdom of the reach", 5);
        p2Chosen1.put("principality of dorne", 5);
        ServerSelect s21 = new ServerSelect(p2Chosen1);

        // second invalid input objects for p2
        HashMap<String, Integer> p2Chosen2  = new HashMap<>();
        p2Chosen2.put("kingdom of the rock", 6);
        p2Chosen2.put("kingdom of the reach", 5);
        p2Chosen2.put("principality of dorne", 5);
        ServerSelect s22 = new ServerSelect(p2Chosen2);

        // valid input objects for p2
        HashMap<String, Integer> p2Chosen3  = new HashMap<>();
        p2Chosen3.put("kingdom of the rock", 7);
        p2Chosen3.put("kingdom of the reach", 5);
        p2Chosen3.put("principality of dorne", 3);
        ServerSelect s23 = new ServerSelect(p2Chosen3);

        Player<String> player1 = new PlayerV1<>(
                setupMockInput(new ArrayList<>(Arrays.asList("a clash of kings", s11, s12))), stream1);
        Player<String> player2 = new PlayerV1<>("Green", 2,
                setupMockInput(new ArrayList<>(Arrays.asList(s21, s22, s23))), stream2);
        RoomController roomController = new RoomController(0, player1, mapDataBase);
        roomController.players.add(player2);
        roomController.selectTerritory();

        //test state of the system is correct
        assertEquals(player1.getId(),
                mapDataBase.getMap("a clash of kings").getTerritory("kingdom of the north").getOwner());
        assertEquals(5,
                mapDataBase.getMap("a clash of kings").getTerritory("kingdom of the north").getUnitsNum());
        assertEquals(player2.getId(),
                mapDataBase.getMap("a clash of kings").getTerritory("principality of dorne").getOwner());
        assertEquals(3,
                mapDataBase.getMap("a clash of kings").getTerritory("principality of dorne").getUnitsNum());

        //test output is correct
        ByteArrayInputStream temp = new ByteArrayInputStream(stream1.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(temp);
        objectInputStream.readObject(); // mapDataBase
        objectInputStream.readObject(); // successful
        objectInputStream.readObject(); // player info
        objectInputStream.readObject(); // wait info
        objectInputStream.readObject(); // client select
        assertEquals(SELECT_TERR_ERROR, objectInputStream.readObject());

        temp = new ByteArrayInputStream(stream2.toByteArray());
        objectInputStream = new ObjectInputStream(temp);
        objectInputStream.readObject(); // client select
        assertEquals(SELECT_TERR_ERROR, objectInputStream.readObject());
        assertEquals(SELECT_TERR_ERROR, objectInputStream.readObject());
    }

    @Test
    void testPlaySingleRound() throws IOException, ClassNotFoundException {
        // set up the game
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        // invalid move actions(under initial map) for player1 --- don't has path
        MoveAction a01 = new MoveAction("kingdom of the north", "principality of dorne", 1, 1);
        // valid move actions(under initial map) for player1
        MoveAction a11 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        MoveAction a12 = new MoveAction("kingdom of mountain and vale", "kingdom of the rock",1, 1);

        // invalid move actions(under initial map) for player2 --- move 0 makes no sense
        MoveAction a21 = new MoveAction("the storm kingdom","kingdom of the reach",  2, 0);
        // valid move actions(under initial map) for player2
        MoveAction a31 = new MoveAction("kingdom of the reach", "the storm kingdom", 2, 1);

        // valid action for player1
        AttackAction a41 = new AttackAction("principality of dorne", "kingdom of the reach", 1, 1);
        // invalid action for player2 (territory doesn't belong to he)
        AttackAction a42 = new AttackAction("principality of dorne", "kingdom of the reach", 2, 1);

        //build the room
        Player<String> player1 = new PlayerV1<>(
                1,
                setupMockInput(
                        new ArrayList<>(
                                Arrays.asList(
                                        "a clash of kings",
                                        a01,
                                        "invalid",
                                        1,
                                        a11,
                                        a12,
                                        ACTION_DONE,
                                        a41,
                                        ACTION_DONE
                                ))), new ByteArrayOutputStream());

        Player<String> player2 = new PlayerV1<>(
                2,
                setupMockInput(
                        new ArrayList<>(
                                Arrays.asList(
                                        a21,
                                        "invalid",
                                        a31,
                                        ACTION_DONE,
                                        a42,
                                        ACTION_DONE
                                ))), new ByteArrayOutputStream());

        RoomController roomController = new RoomController(0, player1, mapDataBase);
        roomController.players.add(player2);
        //let each player choose some territories they want
        WorldMap<String> curMap = mapDataBase.getMap("a clash of kings");
        Territory t1 = curMap.getTerritory("kingdom of the north");
        Territory t2 = curMap.getTerritory("kingdom of mountain and vale");
        Territory t3 = curMap.getTerritory("kingdom of the rock");
        Territory t4 = curMap.getTerritory("kingdom of the reach");
        Territory t5 = curMap.getTerritory("the storm kingdom");
        Territory t6 = curMap.getTerritory("principality of dorne");

        roomController.players.get(0).addTerritory(t1);//kingdom of the north
        roomController.players.get(0).addTerritory(t2);//kingdom of mountain and vale
        roomController.players.get(0).addTerritory(t3);//kingdom of the rock
        roomController.players.get(0).addTerritory(t6);//principality of dorne

        roomController.players.get(1).addTerritory(t4);//kingdom of the reach
        roomController.players.get(1).addTerritory(t5);//the storm kingdom

        //assign some units to each territory, 6 units for each player
        //player 1
        t1.addNUnits(3);
        t2.addNUnits(2);
        t3.addNUnits(1);
        t6.addNUnits(3);
        //player 2
        t4.addNUnits(5);
        t5.addNUnits(2);

        roomController.playSingleRound(1);

        assertEquals(roomController.players.get(0).getTerrNum(),4);
        assertEquals(roomController.players.get(1).getTerrNum(),2);
        assertEquals(t1.getUnitsNum(),2);
        assertEquals(t2.getUnitsNum(),2);
        assertEquals(t3.getUnitsNum(),2);
        assertEquals(t4.getUnitsNum(),4); // reach
        assertEquals(t5.getUnitsNum(),3);
        assertEquals(t6.getUnitsNum(),3); // dorne

        roomController.playSingleRound(2);
        // attacker will lose
        assertEquals(3, roomController.players.get(0).getTerrNum());
        assertEquals(3, roomController.players.get(1).getTerrNum());
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
        roomController.winnerID = -1;
        roomController.checkWinner();
        assertEquals(roomController.winnerID,1);

        player1.loseTerritory(t1);
        player2.addTerritory(t1);
        roomController.winnerID = -1;
        roomController.checkWinner();
        assertEquals(roomController.winnerID,-1);

        player2.loseTerritory(t1);
        player1.addTerritory(t1);
        roomController.winnerID = -1;
        roomController.checkWinner();
        assertEquals(roomController.winnerID,1);

        Territory test = new TerritoryV1("some name");
        player1.addTerritory(test);
        assertThrows(IllegalStateException.class, roomController::checkWinner);
    }

    @Test
    public void testEndGame() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream p1OutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream p2OutStream = new ByteArrayOutputStream();

        Player<String> player1 = new PlayerV1<>("Red", 1, setupMockInput(new ArrayList<>(Arrays.asList("a clash of kings"))), p1OutStream);
        Player<String> player2 = new PlayerV1<>("Blue", 2, setupMockInput(new ArrayList<>()), p2OutStream);

        RoomController roomController = new RoomController(0, player1, new MapDataBase<>());
        roomController.players.add(player2);
        assertThrows(IllegalArgumentException.class, roomController::endGame);
        roomController.winnerID = 1;
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
    public void testRunGame() throws IOException, ClassNotFoundException {
        //valid input objects for p1
        HashMap<String, Integer> p1Chosen1  = new HashMap<>();
        p1Chosen1.put("kingdom of the north", 5);
        p1Chosen1.put("kingdom of mountain and vale", 5);
        p1Chosen1.put("the storm kingdom", 5);

        ServerSelect s1 = new ServerSelect(p1Chosen1);

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
                                s2,
                                ACTION_DONE,
                                ACTION_DONE
                        ))), new ByteArrayOutputStream());

        RoomController roomController = new RoomController(1, player1, new MapDataBase<>());
        roomController.addPlayer(player2);
        // player 1 should win the game
        // can't use assert here, cause we will run game in a separate thread, the variable is different
//        assertEquals(1, roomController.winnerID);
    }
}
