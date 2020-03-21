package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToClientMsg.ClientSelect;
import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackResult;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static edu.duke.ece651.risk.shared.Constant.*;

//TODO for every method that have networking, take client losing connection into consideration
//TODO for every method that have networking, handle some exceptions rather than just throwing it
public class RoomController {
    //TODO maybe change that in the future version? like a beginner to choose that?
    //this variable represents how many units on average we have for a single territory
    int roomID;
    // all players in current room
    List<Player<String>> players;
    // the map this room is playing
    WorldMap<String> map;


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
        player.sendPlayerInfo();
    }

    //call this method to add a new player into this room
    void addPlayer(Player<String> player) throws IOException {
        List<String> colorList = map.getColorList();
        player.setId(players.size() + 1);
        player.setColor(colorList.get(players.size()));
        players.add(player);
        player.sendPlayerInfo();
        if (players.size() == colorList.size()){
            //TODO run the whole game
            System.out.println("run this game");
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
                firstPlayer.send(SELECT_MAP_ERROR);
            }
        }
        firstPlayer.send(SUCCESSFUL);
    }

    //TODO maybe changing this method to a multi-thread version in the future?
    //call this method to let each player choose  territories they want
    // TODO: maybe change the function name to select territory, current name is a bit confusing
    void startGame() throws IOException, IllegalArgumentException, ClassNotFoundException {
        int terriNum = map.getTerriNum();
        int playerNum = players.size();
        //the variable below is the number of territories that a single player can choose
        assert(0==terriNum%playerNum);
        int terrPerUsr = terriNum/playerNum;
        //the variable below is the total number of units that a single player can choose
        int totalUnits = UNITS_PER_TERR *terrPerUsr;
//        HashSet<String> occupied = new HashSet<>();
        for (Player<String> player : players) {
            //get the current list of occupied territories
//            ClientSelect clientSelect = new ClientSelect(terrPerUsr, UNITS_PER_TERR,occupied);
            ClientSelect clientSelect = new ClientSelect(totalUnits, terrPerUsr, map);
            //tell user to select client
            player.send(clientSelect);
            while (true){
                ServerSelect serverSelect = (ServerSelect)player.recv();
                //check if the selection is valid or not
                if(serverSelect.isValid(map,totalUnits,terrPerUsr)){
                    //if valid, update the map
                    for (String terrName : serverSelect.getAllName()) {
//                        occupied.add(terrName);
                        Territory territory = map.getTerritory(terrName);
                        territory.addNUnits(serverSelect.getUnitsNum(terrName));
                        player.addTerritory(territory);
                    }
                    break;
                }else{
                    player.send(SELECT_TERR_ERROR);
                }
            }
        }
    }

    //used to play a single round of game
    void playSingleRoundGame() throws IOException, ClassNotFoundException {
        for (Player<String> player : players) {
            //tell client the most recent situation
            player.send(map);
            while (true){
                Object recvRes = player.recv();
                if (recvRes instanceof Action){
                    Action action = (Action) recvRes;
                    //act accordingly based on whether the input actions are valid or not
                    if (action.isValid(map)){//if valid, update the state of the world
                        action.perform(map);
                        player.send(SUCCESSFUL);
                    }else{//other wise ask user to resend the information
                        player.send(INVALID_ACTION);
                    }
                }else if (recvRes instanceof String && ((String)recvRes).equals(ACTION_DONE)){
                    break;
                }else {
                    player.send(INVALID_ACTION);
                }
            }
        }
        // TODO: try to update the ownership of territories which are attacked by other players
        //iterate all territory, perform the attack action, then send result

        Map<String, Territory> territoryMap = map.getAtlas();
        for (String k : territoryMap.keySet()) {
            Territory t = territoryMap.get(k);
            List<AttackResult> attackResultList =  t.performAttackMove();

            StringBuilder sb = new StringBuilder();
            //change all player id in list to player name
            for (AttackResult aR : attackResultList) {
                sb.append(aR.getAttackerID() + " attacks " + aR.getDefenderID());

                sb.append(" 's territory" + aR.getTerritory());

                if (aR.isAttackerwin()) {
                    sb.append("attacker wins");
                }
                else {
                    sb.append("attacker fails");
                }

            }

            sendAll(sb.toString());
        }


        // after execute all actions, tell the player to enter next round
        sendAll(ROUND_OVER);

        // TODO: we need to notify all player the game result, continue or game over
//        if(getWinnerId() == -1){
//            sendAll("continue");
//        }else {
//            sendAll(GAME_OVER);
//        }
    }

    /**
     * This function will send the data to all players in current room
     * @param data data to be sent
     */
    void sendAll(Object data) throws IOException {
        for (Player<String> player : players){
            player.send(data);
        }
    }

    //return -1 when no wins, otherwise return the id of winner
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
                // TODO: here we may want to send a string, e.g. Winner is Green Player.
                // TODO: probably we may want to have a mapping between player id and player color(just for convenience)
                player.send(winnerId);
            }else{
                player.send(YOU_WINS);
            }
        }
    }

//    //the logic for the whole game
//    void runGame() throws IOException, ClassNotFoundException {
//        startGame();
//        int winnerId = -1;
//        while(winnerId<=0) {
//            playSingleRoundGame();
//            winnerId = getWinnerId();
//        }
//        endGame(winnerId);
//    }
}
