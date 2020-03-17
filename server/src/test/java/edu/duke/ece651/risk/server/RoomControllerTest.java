package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static edu.duke.ece651.risk.shared.Mock.readAllStringFromObjectStream;
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
        assertEquals(roomController.map,mapDataBase.getMap("a clash of kings"));
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
        assertEquals(roomController.players.size(),3);
        assertEquals(roomController.players.get(0).getColor(),"red");
        assertEquals(roomController.players.get(1).getColor(),"blue");
        assertEquals(roomController.players.get(2).getColor(),"black");
        assertEquals(roomController.players.size(),roomController.map.getColorList().size());
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
        MapDataBase sendBase = (MapDataBase)objectInputStream.readObject();
        assertTrue(sendBase.containsMap("a clash of kings"));
        assertTrue(sendBase.containsMap("test"));
        WorldMap worldMap = sendBase.getMap("a clash of kings");
        Territory kingdom_of_the_north = worldMap.getTerritory("kingdom of the north");
        Territory kingdom_of_the_rock = worldMap.getTerritory("kingdom of the rock");
        assertTrue(kingdom_of_the_north.getNeigh().contains(kingdom_of_the_rock));
        String errorMsg = (String)objectInputStream.readObject();
        assertEquals(errorMsg,"The map name you select is invalid");
        String errorMsg2 = (String)objectInputStream.readObject();
        assertEquals(errorMsg,"The map name you select is invalid");
        assertThrows(EOFException.class,()->{String res = (String)objectInputStream.readObject();});
    }
    @Test
    void startGame() throws IOException, ClassNotFoundException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();

        //first invalid input objects for p1
        HashMap<String, Integer> p1Chosen1 = new HashMap<>();
        p1Chosen1.put("kingdom of the north",5);
        p1Chosen1.put("kingdom of mountain and vale",5);
        ServerSelect s11 = new ServerSelect(p1Chosen1);

        //valid input objects for p1
        HashMap<String, Integer> p1Chosen2  = new HashMap<>();
        p1Chosen2.put("kingdom of the north",5);
        p1Chosen2.put("kingdom of mountain and vale",5);
        p1Chosen2.put("the storm kingdom",5);
        ServerSelect s12 = new ServerSelect(p1Chosen2);

        // first invalid input objects for p2
        HashMap<String, Integer> p2Chosen1  = new HashMap<>();
        p1Chosen2.put("the storm kingdom",5);
        p2Chosen1.put("kingdom of the reach",5);
        p2Chosen1.put("principality of dorne",5);
        ServerSelect s21 = new ServerSelect(p2Chosen1);

        //second invalid input objects for p2
        HashMap<String, Integer> p2Chosen2  = new HashMap<>();
        p2Chosen2.put("kingdom of the rock",6);
        p2Chosen2.put("kingdom of the reach",5);
        p2Chosen2.put("principality of dorne",5);
        ServerSelect s22 = new ServerSelect(p2Chosen2);

        // valid input objects for p2
        HashMap<String, Integer> p2Chosen3  = new HashMap<>();
        p2Chosen3.put("kingdom of the rock",7);
        p2Chosen3.put("kingdom of the reach",5);
        p2Chosen3.put("principality of dorne",3);
        ServerSelect s23 = new ServerSelect(p2Chosen3);


        Player<String> player1 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("a clash of kings",s11,s12))), stream1);
        Player<String> player2 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(s21,s22,s23))), stream2);
        RoomController roomController = new RoomController(0, player1, mapDataBase);
        roomController.addPlayer(player2);
        roomController.startGame();

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
        objectInputStream.readObject();
        objectInputStream.readObject();
        String msg1 = (String)objectInputStream.readObject();
        assertEquals(msg1,"Your initialization is invalid");

        temp = new ByteArrayInputStream(stream2.toByteArray());
        objectInputStream = new ObjectInputStream(temp);
        objectInputStream.readObject();
        String msg2 = (String)objectInputStream.readObject();
        assertEquals(msg2,"Your initialization is invalid");
        String msg3 = (String)objectInputStream.readObject();
        assertEquals(msg3,"Your initialization is invalid");


    }

    @Test
    void testPlaySingleRoundGame() throws IOException, ClassNotFoundException {
        //set up the game
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        //a map of invalid move actions(under initial map) for player1
        Map<String, List<Action>> actionMap0 = new HashMap<>();
        MoveAction a01 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        MoveAction a02 = new MoveAction("kingdom of the north", "principality of dorne", 1, 1);
        actionMap0.put("move", Arrays.asList(a01,a02));
        actionMap0.put("attack", new ArrayList<Action>());

        //a map of valid move actions(under initial map) for player1
        Map<String, List<Action>> actionMap1 = new HashMap<>();
        MoveAction a11 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        MoveAction a12 = new MoveAction("kingdom of mountain and vale", "kingdom of the rock",1, 1);

        actionMap1.put("move", Arrays.asList(a11,a12));
        actionMap1.put("attack", new ArrayList<Action>());

        //a map of invalid move actions(under initial map) for player2
        Map<String, List<Action>> actionMap2 = new HashMap<>();
        MoveAction a21 = new MoveAction("the storm kingdom","kingdom of the reach",  2, 0);
        MoveAction a22 = new MoveAction("the storm kingdom","kingdom of the reach",  2, 4);
        actionMap2.put("move", Arrays.asList(a21,a22));
        actionMap2.put("attack", new ArrayList<Action>());

        //a map of valid move actions(under initial map) for player2
        Map<String, List<Action>> actionMap3 = new HashMap<>();
        MoveAction a31 = new MoveAction("kingdom of the reach", "the storm kingdom", 2, 1);
        actionMap3.put("move", Arrays.asList(a31));
        actionMap3.put("attack", new ArrayList<Action>());

        Player<String> player1 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("a clash of kings", actionMap0, actionMap1))), new ByteArrayOutputStream());
        Player<String> player2 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList(actionMap2, actionMap3))), new ByteArrayOutputStream());
        RoomController roomController = new RoomController(0, player1, mapDataBase);
        roomController.addPlayer(player2);

        //let each player choose some territories they want
        WorldMap<String> curMap = mapDataBase.getMap("a clash of kings");
        Territory t1 = curMap.getTerritory("kingdom of the north");
        Territory t2 = curMap.getTerritory("kingdom of mountain and vale");
        Territory t3 = curMap.getTerritory("kingdom of the rock");
        Territory t4 = curMap.getTerritory("kingdom of the reach");
        Territory t5 = curMap.getTerritory("the storm kingdom");
        Territory t6 = curMap.getTerritory("principality of dorne");

        roomController.players.get(0).addTerritory(t1);
        roomController.players.get(0).addTerritory(t2);
        roomController.players.get(0).addTerritory(t3);
        roomController.players.get(0).addTerritory(t6);

        roomController.players.get(1).addTerritory(t4);
        roomController.players.get(1).addTerritory(t5);

        //assign some units to each territory, 6 units for each player
        //player 1
        t1.addNUnits(3);
        t2.addNUnits(2);
        t3.addNUnits(1);
        t6.addNUnits(1);
        //player 2
        t4.addNUnits(4);
        t5.addNUnits(2);

        roomController.playSingleRoundGame(1);

        assertEquals(roomController.players.get(0).getTerrNum(),4);
        assertEquals(roomController.players.get(1).getTerrNum(),2);
        assertEquals(t1.getUnitsNum(),2);
        assertEquals(t2.getUnitsNum(),2);
        assertEquals(t3.getUnitsNum(),2);
        assertEquals(t4.getUnitsNum(),3);
        assertEquals(t5.getUnitsNum(),3);
    }

    @Test
    void getWinnerId() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Player<String> player = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("a clash of kings"))), outputStream);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> curMap = mapDataBase.getMap("a clash of kings");
        RoomController roomController = new RoomController(0, player, mapDataBase);
        roomController.addPlayer(new PlayerV1<>(setupMockInput(new ArrayList<>()), new ByteArrayOutputStream()));
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
        assertEquals(roomController.getWinnerId(),1);

        player1.loseTerritory(t1);
        player2.addTerritory(t1);
        assertEquals(roomController.getWinnerId(),-1);

        player2.loseTerritory(t1);
        player1.addTerritory(t1);
        assertEquals(roomController.getWinnerId(),1);

        Territory test = new TerritoryV1("some name");
        player1.addTerritory(test);
        assertThrows(IllegalStateException.class, roomController::getWinnerId);
    }

    @Test
    public void testEndGame() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream p1OutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream p2OutStream = new ByteArrayOutputStream();

        Player<String> player1 = new PlayerV1<>(setupMockInput(new ArrayList<>(Arrays.asList("a clash of kings"))), p1OutStream);
        Player<String> player2 = new PlayerV1<>(setupMockInput(new ArrayList<>()), p2OutStream);

        RoomController roomController = new RoomController(0, player1, new MapDataBase<>());
        roomController.addPlayer(player2);
        assertThrows(IllegalArgumentException.class,()->{roomController.endGame(-1);});

        roomController.endGame(1);
    }
    @Test
    public void testRunGame() {

    }

}
