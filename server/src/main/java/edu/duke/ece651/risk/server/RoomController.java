package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.network.Deserializer;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

//TODO for every method that have networking, take client losing connection into consideration
//TODO for every method that have networking, handle some exceptions rather than just throwing it
public class RoomController {
    int roomID;
    // all players in current room
    List<Player<String>> players;
    // the map this room is playing
    WorldMap map;

    //constructor: let the starter start the whole game
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

    //call this method to add a new player into this room
    void addPlayer(Socket socket) throws IOException {
        List<String> colorList = map.getColorList();
        players.add(new PlayerV1<>(colorList.get(players.size()), players.size() + 1, socket));
        if (players.size() == colorList.size()){
            //TODO run the whole game
//            this.runGame();
        }
    }

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

    //call this method to let each player choose  territories they want
    //TODO maybe change this method to a multi-thread version? the current version is letting each player choose one by one
    //TODO maybe add a new action type to check the integrity and correctness of data outside current method is a wise idea
    //TODO take assign units for each territory into consideration
    void startGame() throws IOException, IllegalArgumentException {
        int TerriNum = map.getTerriNum();
        int playerNum = map.getColorList().size();
        if (playerNum>TerriNum){
            throw new IllegalArgumentException("The number of players can't be larger than the number of territories");
        }else if(0!=TerriNum/playerNum){
            throw new IllegalArgumentException("This is unfair to the last player!");
        }
        int singleNum = TerriNum/playerNum;
        HashSet<String> occupied = new HashSet<>();
        for (Player<String> player : players) {
            //get the current list of occupied territories
            String delimiter = "";
            StringBuilder sb = new StringBuilder();
            for (String s : occupied) {
                sb.append(delimiter);
                sb.append(s);
                delimiter = ",";
            }
            String occMsg = sb.toString();
            //try to let the player to communicate with server to choose the territories
            while (true){
                boolean isValid = true;
                //inform current player about how many territories she can choose and which territories are occupied now
                player.send("the number of territories you should choose is: "+singleNum);
                player.send(occMsg);
                String terrStr = player.recv();
                //check if the number of territories is valid or not
                if (null==terrStr) continue;
                String[] split = terrStr.split(",");
                HashSet<String> terrNames = new HashSet<>();
                for (String terrName : split) {
                    terrNames.add(terrName);
                }
                if (terrNames.size()!=singleNum) continue;
                //check if all name is valid and free
                for (String terrName : terrNames) {
                    terrName = terrName.strip();
                    if (!map.hasFreeTerritory(terrName)){
                        isValid = false;
                        break;
                    }
                }
                if (isValid) {
                    occupied.addAll(terrNames);
                    for (String terrName : terrNames) {
                        Territory territory = map.getTerritory(terrName);
                        player.addTerritory(territory);
                    }
                    break;
                }
            }
        }
    }
    //TODO take exception into consideration
    void playSingleRoundGame(int round) throws IOException {
        int i = 1;
        for (Player<String> player : players) {
            System.out.println("player"+(i));
            i++;
            while (true){
                System.out.println("inside while");
                //inform client that new round of game begins
                player.send(""+round);
                String actionMsg = player.recv();
                System.out.println(actionMsg);
                Map<String, List<Action>> actionMap = Deserializer.deserializeActions(actionMsg);
                boolean isValid = true;
                for (String actionName : actionMap.keySet()) {
                    List<Action> actions = actionMap.get(actionName);
                    for (int j = 0; j < actions.size(); j++) {
                        Action action = actions.get(j);
                        if (!action.isValid(map)){
                            //inform client that previous input has failed
                            player.send("Invalid "+j);
                            isValid = false;
                            break;
                        }
                    }
                    if (!isValid) break;
                }
                if (isValid){
                    System.out.println("success");
                    for (String actionName : actionMap.keySet()) {
                        List<Action> actions = actionMap.get(actionName);
                        for (Action action : actions) {
                            action.perform(map);
                        }
                    }
                    break;
                }else {
                    System.out.println("fail");
                }
            }
        }
    }

    //return -1 when no wins
    int getWinnerId(){
        int targetNum = map.getTerriNum();
        for (Player<String> player : players) {
            if (player.getTerrNum()==targetNum){
                return player.getId();
            }
        }
        return -1;
    }

    void endGame(int winnerId) throws IOException {
        if (winnerId<=0){
            throw new IllegalArgumentException("winner id must be positive");
        }
        //tell all players that we want to end this game
        for (Player<String> player : players) {
            if (player.getId()!=winnerId){
                player.send("Game has finished, Player"+winnerId+" is the winner");
            }else{
                player.send("Game has finished, you are the winner!");
            }
        }
    }

    //the logic for the whole game
    void runGame() throws IOException {
        int round = 0;
        startGame();
        int winnerId = -1;
        while(winnerId<=0) {
            round++;
            playSingleRoundGame(round);
            winnerId = getWinnerId();
        }
        endGame(winnerId);
    }
}
