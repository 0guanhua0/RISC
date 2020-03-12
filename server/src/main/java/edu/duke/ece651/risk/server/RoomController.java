package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class RoomController {
    int roomID;
    // all players in current room
    List<Player<String>> players;
    // the map this room is playing
    WorldMap map;

    public RoomController(int roomID, Socket socket) {
        this.roomID = roomID;
        this.players = new LinkedList<>();
        this.players.add(new PlayerV1<>("G", this.players.size(), socket));
        askForMap();
    }

    void addPlayer(Socket socket){
        // TODO: assign color here(probably each map needs to store a list of available color)
        players.add(new PlayerV1<>("B", players.size(), socket));
        // TODO: replace magic 2 with the actual player number support by current WorldMap
        if (players.size() >= 3){
            startGame();
        }
    }

    void startGame() {

    }

    void endGame() {

    }

    void askForMap(){
        // TODO: actually ask the player to choose the map
        map = new MapDataBase().getMap("a clash of kings");
    }
}
