package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToClientMsg.ClientSelect;
import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.IOException;
import java.util.*;

//TODO for every method that have networking, take client losing connection into consideration
//TODO for every method that have networking, handle some exceptions rather than just throwing it
public class RoomController {
    //TODO maybe change that in the future version? like a beginner to choose that?
    //this variable represents how many units on average we have for a single territory
    private static final int unitsNum = 5;
    int roomID;
    // all players in current room
    List<Player<String>> players;
    // the map this room is playing
    WorldMap map;


    //constructor: let the starter start the whole game
    public RoomController(int roomID, Player<String> player, MapDataBase<String> mapDataBase) throws IOException, IllegalArgumentException, ClassNotFoundException {
        if (roomID<0){
            throw new IllegalArgumentException("Invalid value of Room Id");
        }
        this.roomID = roomID;
        this.players = new ArrayList<>();
        player.setId(players.size() + 1);
        this.players.add(player);
        askForMap(mapDataBase);
        List<String> colorList = map.getColorList();
        players.get(0).setColor(colorList.get(0));
    }

    //call this method to add a new player into this room
    void addPlayer(Player<String> player) throws IOException {
        List<String> colorList = map.getColorList();
        player.setId(players.size() + 1);
        player.setColor(colorList.get(players.size()));
        players.add(player);
        if (players.size() == colorList.size()){
            //TODO run the whole game
//            System.out.println("run this game");
            //this.runGame();
        }
    }

    void askForMap(MapDataBase<String> mapDataBase) throws IOException, ClassNotFoundException {
        Player<String> firstPlayer = players.get(0);
        firstPlayer.send(mapDataBase);
        while(true){
            String mapName = (String) firstPlayer.recv();
            if (mapDataBase.containsMap(mapName)){
                this.map = mapDataBase.getMap(mapName);
                break;
            }else {
                firstPlayer.send("The map name you select is invalid");
            }
        }
    }

    //TODO maybe changing this method to a multi-thread version in the future?
    //call this method to let each player choose  territories they want
    void startGame() throws IOException, IllegalArgumentException, ClassNotFoundException {
        int TerriNum = map.getTerriNum();
        int playerNum = players.size();
        //the variable below is the number of territories that a single player can choose
        int singleNum = TerriNum/playerNum;
        //the variable below is the total number of units that a single player can choose
        int totalUnits = unitsNum*singleNum;
        HashSet<String> occupied = new HashSet<>();
        for (Player<String> player : players) {
            //get the current list of occupied territories
            ClientSelect clientSelect = new ClientSelect(singleNum,unitsNum,occupied);
            //tell user to select client
            player.send(clientSelect);
            while (true){
                boolean isValid = true;
                ServerSelect serverSelect = (ServerSelect)player.recv();
                //check if the selection is valid or not
                if(serverSelect.isValid(map,totalUnits,singleNum)){
                    //if valid, update the map
                    for (String terrName : serverSelect.getAllName()) {
                        occupied.add(terrName);
                        Territory territory = map.getTerritory(terrName);
                        player.addTerritory(territory);
                    }
                    break;
                }else{
                    player.send(" “Your initialization is invalid”\n");
                }
            }
        }
    }

    void playSingleRoundGame(int round) throws IOException, ClassNotFoundException {
        int i = 1;
        for (Player<String> player : players) {
            while (true){
                //inform client that new round of game begins
                player.send(""+round);
                Map<String, List<Action>> actionMap = (Map<String, List<Action>>) player.recv();
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
                    for (String actionName : actionMap.keySet()) {
                        List<Action> actions = actionMap.get(actionName);
                        for (Action action : actions) {
                            action.perform(map);
                        }
                    }
                    break;
                }
            }
        }
    }

    //return -1 when no wins
    int getWinnerId(){
        int res = -1;
        int targetNum = map.getTerriNum();
        int totalNum = 0;
        for (Player<String> player : players) {
            int curNum = player.getTerrNum();
            totalNum += curNum;
            if (totalNum>targetNum){
                throw new IllegalStateException("Illegal State of current world");
            }
            if (curNum==targetNum){
                res =  player.getId();
            }
        }
        return res;
    }

    void endGame(int winnerId) throws IOException {
        if (winnerId<=0){
            throw new IllegalArgumentException("winner id must be positive");
        }
        //tell all players that we want to end this game
        for (Player<String> player : players) {
            if (player.getId()!=winnerId){
                player.send("Game has finished, Player"+winnerId+" is the winner!");
            }else{
                player.send("Game has finished, you are the winner!");
            }
        }
    }

//    //the logic for the whole game
//    void runGame() throws IOException, ClassNotFoundException {
//        int round = 0;
//        startGame();
//        int winnerId = -1;
//        while(winnerId<=0) {
//            round++;
//            playSingleRoundGame(round);
//            winnerId = getWinnerId();
//        }
//        endGame(winnerId);
//    }
}
