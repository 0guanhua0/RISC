package edu.duke.ece651.risk.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.duke.ece651.risk.shared.map.MapDataBase;
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
    public void testStartGame() { 
        
    }
    
    @Test
    public void testEndGame() { 
        
    }
    
    @Test
    public void testAskForMap() { 
        
    }
}
