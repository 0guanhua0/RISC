package edu.duke.ece651.risk.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.network.Deserializer;
import edu.duke.ece651.risk.shared.player.Player;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

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
    void testStartGame() throws IOException {
        //set up the game
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        Socket p1Socket = mock(Socket.class);
        Socket p2Socket = mock(Socket.class);
        when(p1Socket.getInputStream()).
                thenReturn(new ByteArrayInputStream("a clash of kings".getBytes()));
        when(p1Socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        RoomController roomController = new RoomController(0,p1Socket, mapDataBase);
        roomController.addPlayer(p2Socket);

        //let the player1 choose the territory
        when(p1Socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(p1Socket.getInputStream()).
                thenReturn(new ByteArrayInputStream(("the storm kingdom," +
                          "kingdom of mountain and vale,kingdom of the rock").getBytes()));

        //let player2 choose the territory
        roomController.startGame();

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

        //a map of valid move actions(under initial map) for player1

        //a map of invalid move(under initial map) actions for player1

        //a map of valid move actions(under initial map) for player2

        //a map of invalid move actions(under initial map) for player2


        String fakeActions = "fake actions";
        //mock interaction with player1
        when(p1Socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(p1Socket.getInputStream()).thenReturn(new ByteArrayInputStream(fakeActions.getBytes()));
//        when(Deserializer.deserializeActions(fakeActions)).thenReturn();
        //mock receive a list of valid actions from player2

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
