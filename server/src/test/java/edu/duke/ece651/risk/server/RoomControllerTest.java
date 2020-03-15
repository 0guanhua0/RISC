package edu.duke.ece651.risk.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.network.Deserializer;
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
                thenReturn(new ByteArrayInputStream("a clash of kings".getBytes()));
        when(p1Socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        RoomController roomController = new RoomController(0,p1Socket, mapDataBase);
        roomController.addPlayer(null);
        assertEquals(roomController.players.size(),2);
        assertEquals(roomController.players.get(0).getColor(),"red");
        assertEquals(roomController.players.get(1).getColor(),"blue");
        assertEquals(roomController.players.size(),roomController.map.getColorList().size());
    }




    @Test
//    note that below is unit testing for testPlaySingleRoundGame, so here I don't test whether action can work here,
//    I just test the funcionality of playSingleRoundGame
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
        Territory t2 = curMap.getTerritory("kigngdom of mountain and vale");
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

        //assign some units to each territory, 5 units for each player
        //player 1
        t1.addNUnits(2);
        t2.addNUnits(2);
        t3.addNUnits(1);
        t6.addNUnits(1);
        //player 2
        t4.addNUnits(2);
        t5.addNUnits(2);


        //a map of valid move actions(under initial map) for player1
        Map<String, List<Action>> actionMap1 = new HashMap<>();
        MoveAction a11 = new MoveAction("kingdom of the north", "kingdom of mountain and vale", 1, 1);
        actionMap1.put("Move", Arrays.asList(a11));
        //a map of invalid move(under initial map) actions for player1

        //a map of valid move actions(under initial map) for player2
        Map<String, List<Action>> actionMap2 = new HashMap<>();
        MoveAction a22 = new MoveAction("kingdom of the reach", "the storm kingdom", 2, 1);
        actionMap2.put("Move", Arrays.asList(a22));
        //a map of invalid move actions(under initial map) for player2

        String fakeActions = "fake actions";
        //mock interaction with player1
        when(p1Socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(p1Socket.getInputStream()).thenReturn(new ByteArrayInputStream(fakeActions.getBytes()));
        when(Deserializer.deserializeActions(fakeActions)).thenReturn(actionMap1);
        //mock receive a list of valid actions from player2
        when(p1Socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(p1Socket.getInputStream()).thenReturn(new ByteArrayInputStream(fakeActions.getBytes()));
        when(Deserializer.deserializeActions(fakeActions)).thenReturn(actionMap2);

        roomController.playSingleRoundGame(1);



    }
    @Test
    public void testRunGame() {
        
    }
    
    @Test
    public void testEndGame() { 
        
    }
    
    @Test
    public void testAskForMap() { 
        
    }
}
