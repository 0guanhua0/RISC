package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
//TODO take client losing connection into consideration
public class RoomController {
    int roomID;
    // all players in current room
    List<Player<String>> players;
    // the map this room is playing
    WorldMap map;

    public RoomController(int roomID, Socket socket,MapDataBase<String> mapDataBase) throws IOException, IllegalArgumentException {
        if (roomID<0){
            throw new IllegalArgumentException("Invalid value of Room Id");
        }
        this.roomID = roomID;
        this.players = new ArrayList<>();
        this.players.add(new PlayerV1<>("G", this.players.size() + 1, socket));
        askForMap(mapDataBase);
    }

    void addPlayer(Socket socket){
        List<String> colorList = map.getColorList();
        players.add(new PlayerV1<String>(colorList.get(players.size()), players.size() + 1, socket));
        // TODO: replace magic 2 with the actual player number support by current WorldMap
        if (players.size() >= 3){
            startGame();
        }
    }

    void startGame() {

    }

    void endGame() {

    }

    //TODO take client losing connection into consideration
    void askForMap(MapDataBase<String> mapDataBase) throws IOException {
        if (players.size()!=1){
            throw new IllegalStateException("Invalid number of players");
        }
        Player<String> firstPlayer = players.get(0);
        while(true){
            firstPlayer.send("Please select the map you want");
            String mapName = firstPlayer.recv();
            if (mapDataBase.containsMap(mapName)){
                this.map = mapDataBase.getMap(mapName);
                break;
            }
        }

    }
}
