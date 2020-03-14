package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.network.Deserializer;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//TODO take client losing connection into consideration
//TODO
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
        this.players.add(new PlayerV1<>(this.players.size() + 1, socket));
        askForMap(mapDataBase);
        List<String> colorList = map.getColorList();
        players.get(0).setColor(colorList.get(0));
    }

    void addPlayer(Socket socket){
        List<String> colorList = map.getColorList();
        players.add(new PlayerV1<>(colorList.get(players.size()), players.size() + 1, socket));
        if (players.size() == colorList.size()){
            startGame();
        }
    }

    void startGame() {

    }
    //TODO take exception of msg into consideration
    void playSingleRoundGame() throws IOException {
        for (Player<String> player : players) {
            String msg = player.recv();
            HashMap<String, List<Action>> actionMap = Deserializer.deserializeActions(msg);
            actionMap.get()
        }
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
