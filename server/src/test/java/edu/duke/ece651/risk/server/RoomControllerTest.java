package edu.duke.ece651.risk.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class RoomControllerTest {

    @Test
    void testConstructor() throws IOException {

        assertThrows(IllegalArgumentException.class,()->{new RoomController(-1,null,new MapDataBase<String>());});

        Socket playerSocket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(playerSocket.getInputStream()).
                thenReturn(new ByteArrayInputStream("hogwarts".getBytes())).
                thenReturn(new ByteArrayInputStream("".getBytes())).
                thenReturn(new ByteArrayInputStream("a clash of kings".getBytes()));
        when(playerSocket.getOutputStream()).thenReturn(outputStream);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        RoomController roomController = new RoomController(0,playerSocket, mapDataBase);
        assertEquals(roomController.roomID,0);
        assertEquals(roomController.players.size(),1);
        assertEquals(roomController.map,mapDataBase.getMap("a clash of kings"));
        verify(playerSocket, times(3)).getInputStream();
        verify(playerSocket, times(3)).getOutputStream();
    }

    @Test
    public void testAddPlayer() throws IOException {
        //perpare the DataBase
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        Socket p1Socket = mock(Socket.class);
        when(p1Socket.getInputStream()).
                thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(p1Socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        RoomController roomController = new RoomController(0,p1Socket, mapDataBase);
        roomController.addPlayer(null);
        roomController.addPlayer(null);
        assertEquals(roomController.players.size(),3);
        assertEquals(roomController.players.get(0).getColor(),"red");
        assertEquals(roomController.players.get(1).getColor(),"blue");
        assertEquals(roomController.players.get(2).getColor(),"black");
        assertEquals(roomController.players.size(),roomController.map.getColorList().size());
    }

    @Test
    public void testAskForMap() throws IOException {
        assertThrows(IllegalArgumentException.class,()->{new RoomController(-1,null,new MapDataBase<String>());});

        Socket playerSocket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(playerSocket.getInputStream()).
                thenReturn(new ByteArrayInputStream("hogwarts".getBytes())).
                thenReturn(new ByteArrayInputStream("".getBytes())).
                thenReturn(new ByteArrayInputStream("a clash of kings".getBytes()));
        when(playerSocket.getOutputStream()).thenReturn(outputStream);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        RoomController roomController = new RoomController(0,playerSocket, mapDataBase);
        assertEquals(roomController.roomID,0);
        assertEquals(roomController.players.size(),1);
        assertEquals(roomController.map,mapDataBase.getMap("a clash of kings"));
        verify(playerSocket, times(3)).getInputStream();
        verify(playerSocket, times(3)).getOutputStream();
    }


    @Test
    void testPlaySingleRoundGame() throws IOException {

        //set up the game
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        Socket p1Socket = mock(Socket.class);
        Socket p2Socket = mock(Socket.class);
        when(p1Socket.getInputStream()).
                thenReturn(new ByteArrayInputStream("a clash of kings".getBytes()));
        when(p1Socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        RoomController roomController = new RoomController(0,p1Socket, mapDataBase);
        roomController.addPlayer(p2Socket);

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

        //a map of invalid move actions(under initial map) for player1
        Map<String, List<Action>> actionMap0 = new HashMap<>();
        MoveAction a01 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        MoveAction a02 = new MoveAction("kingdom of the north", "principality of dorne", 1, 1);
        actionMap0.put("move", Arrays.asList(a01,a02));
        actionMap0.put("attack", new ArrayList<Action>());
        String action0Str = new Gson().toJson(actionMap0);

        //a map of valid move actions(under initial map) for player1
        Map<String, List<Action>> actionMap1 = new HashMap<>();
        MoveAction a11 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        MoveAction a12 = new MoveAction("kingdom of mountain and vale", "kingdom of the rock",1, 1);

        actionMap1.put("move", Arrays.asList(a11,a12));
        actionMap1.put("attack", new ArrayList<Action>());
        String action1Str = new Gson().toJson(actionMap1);

        //a map of invalid move actions(under initial map) for player2
        Map<String, List<Action>> actionMap2 = new HashMap<>();
        MoveAction a21 = new MoveAction("the storm kingdom","kingdom of the reach",  2, 0);
        MoveAction a22 = new MoveAction("the storm kingdom","kingdom of the reach",  2, 4);
        actionMap2.put("move", Arrays.asList(a21,a22));
        actionMap2.put("attack", new ArrayList<Action>());
        String action2Str = new Gson().toJson(actionMap2);

        //a map of valid move actions(under initial map) for player2
        Map<String, List<Action>> actionMap3 = new HashMap<>();
        MoveAction a31 = new MoveAction("kingdom of the reach", "the storm kingdom", 2, 1);
        actionMap3.put("move", Arrays.asList(a31));
        actionMap3.put("attack", new ArrayList<Action>());
        String action3Str = new Gson().toJson(actionMap3);


        //mock interaction with player1
        when(p1Socket.getOutputStream()).thenReturn(new ByteArrayOutputStream())
                                         .thenReturn(new ByteArrayOutputStream());
        when(p1Socket.getInputStream()).thenReturn(new ByteArrayInputStream(action0Str.getBytes()))
                                        .thenReturn(new ByteArrayInputStream(action1Str.getBytes()));

        //mock receive a list of valid actions from player2
        when(p2Socket.getOutputStream()).thenReturn(new ByteArrayOutputStream())
                                        .thenReturn(new ByteArrayOutputStream());
        when(p2Socket.getInputStream()).thenReturn(new ByteArrayInputStream(action2Str.getBytes()))
                                       .thenReturn(new ByteArrayInputStream(action3Str.getBytes()));

        roomController.playSingleRoundGame(1);

        verify(p1Socket, times(2+1)).getInputStream();
        verify(p1Socket, times(3+1)).getOutputStream();
        verify(p2Socket, times(2)).getInputStream();
        verify(p2Socket, times(3)).getOutputStream();
        assertEquals(roomController.players.get(0).getTerrNum(),4);
        assertEquals(roomController.players.get(1).getTerrNum(),2);
        assertEquals(t1.getUnitsNum(),2);
        assertEquals(t2.getUnitsNum(),2);
        assertEquals(t3.getUnitsNum(),2);
        assertEquals(t4.getUnitsNum(),3);
        assertEquals(t5.getUnitsNum(),3);
    }
    @Test
    void getWinnerId() throws IOException {
        Socket p1Socket = mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(p1Socket.getInputStream()).thenReturn(new ByteArrayInputStream("a clash of kings".getBytes()));
        when(p1Socket.getOutputStream()).thenReturn(outputStream);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap curMap = mapDataBase.getMap("a clash of kings");
        RoomController roomController = new RoomController(0,p1Socket, mapDataBase);
        roomController.addPlayer(null);
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
        assertThrows(IllegalStateException.class,()->{roomController.getWinnerId();});
    }

    @Test
    public void testEndGame() throws IOException {
        Socket p1Socket = mock(Socket.class);
        Socket p2Socket = mock(Socket.class);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(p1Socket.getInputStream()).thenReturn(new ByteArrayInputStream("a clash of kings".getBytes()));
        when(p1Socket.getOutputStream()).thenReturn(outputStream);
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap curMap = mapDataBase.getMap("a clash of kings");
        RoomController roomController = new RoomController(0,p1Socket, mapDataBase);
        roomController.addPlayer(p2Socket);
        assertThrows(IllegalArgumentException.class,()->{roomController.endGame(-1);});
        ByteArrayOutputStream p1OutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream p2OutStream = new ByteArrayOutputStream();
        when(p1Socket.getOutputStream()).thenReturn(p1OutStream);
        when(p2Socket.getOutputStream()).thenReturn(p2OutStream);
        roomController.endGame(1);
        assertEquals(p1OutStream.toString().strip(),"Game has finished, you are the winner!".strip());
        assertEquals(p2OutStream.toString().strip(),"Game has finished, Player1 is the winner!".strip());
        verify(p1Socket, times(1)).getInputStream();
        verify(p1Socket, times(2)).getOutputStream();
        verify(p2Socket, times(0)).getInputStream();
        verify(p2Socket, times(1)).getOutputStream();

    }
    @Test
    public void testRunGame() {

    }

}
