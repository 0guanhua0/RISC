package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.ToClientMsg.ClientSelect;
import edu.duke.ece651.risk.shared.ToClientMsg.RoundInfo;
import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackResult;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.io.IOException;
import java.util.*;

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
    // the mapping between player id and player name(for now, use the color)
    Map<Integer, String> idToName;
    // winner ID, mainly for testing purpose
    int winnerID;

    /**
     * The constructor, initialize the whole game(room.
     * @param roomID roomID for this room
     * @param player the "beginner", the player create this room
     * @param mapDataBase all map we have
     * @throws IOException probably because of stream close
     * @throws IllegalArgumentException probably because of invalid roomID(should be positive)
     * @throws ClassNotFoundException probably because of not follow the protocol
     */
    public RoomController(int roomID, Player<String> player, MapDataBase<String> mapDataBase) throws IOException, IllegalArgumentException, ClassNotFoundException {
        if (roomID < 0){
            throw new IllegalArgumentException("Invalid value of Room Id");
        }
        this.roomID = roomID;
        this.winnerID = -1;

        players = new ArrayList<>();
        players.add(player);
        player.setId(players.size());

        // let the beginner determine the map
        askForMap(mapDataBase);

        List<String> colorList = map.getColorList();
        // assign the color
        player.setColor(colorList.get(0));
        player.sendPlayerInfo();

        player.send(String.format("Please wait other players to join th game(need %d, joined %d)", colorList.size(), players.size()));

        idToName = new HashMap<>();
        idToName.put(player.getId(), player.getColor());

    }

    /**
     * call this method to add a new player into this room
     * after the last player enter the room, game will begin automatically
     * @param player new player
     * @throws IOException probably because of stream close
     */
    void addPlayer(Player<String> player) throws IOException, ClassNotFoundException {
        // TODO: in evolution 2, we need to check whether this player is already in room
        players.add(player);

        List<String> colorList = map.getColorList();
        player.setId(players.size());
        player.setColor(colorList.get(players.size() - 1));
        player.sendPlayerInfo();

        idToName.put(player.getId(), player.getColor());

        // check whether has enough player to start the game
        if (players.size() == colorList.size()){
            player.send("You are the last player, game will start now.");
            runGame();
        }else {
            player.send(String.format("Please wait other players to join th game(need %d, joined %d)", colorList.size(), players.size()));
        }
    }

    void askForMap(MapDataBase<String> mapDataBase) throws IOException, ClassNotFoundException {
        Player<String> firstPlayer = players.get(0);
        firstPlayer.send(mapDataBase);
        while(true){
            String mapName = (String) firstPlayer.recv();
            if (mapDataBase.containsMap(mapName)){
                map = new MapDataBase<String>().getMap(mapName);
                break;
            }else {
                firstPlayer.send(SELECT_MAP_ERROR);
            }
        }
        firstPlayer.send(SUCCESSFUL);
    }

    // TODO maybe changing this method to a multi-thread version in the future?
    void selectTerritory() throws IOException, IllegalArgumentException, ClassNotFoundException {
        int terriNum = map.getTerriNum();
        int playerNum = players.size();
        //the variable below is the number of territories that a single player can choose
        assert(0 == terriNum % playerNum);
        int terrPerUsr = terriNum / playerNum;
        //the variable below is the total number of units that a single player can choose
        int totalUnits = UNITS_PER_TERR * terrPerUsr;

        // select territory
        for (Player<String> player : players) {
            //get the current list of occupied territories
            ClientSelect clientSelect = new ClientSelect(totalUnits, terrPerUsr, map.getName());
            //tell user to select client
            player.send(clientSelect);

            while (true){
                ServerSelect serverSelect = (ServerSelect)player.recv();
                //check if the selection is valid or not
                if(serverSelect.isValid(map, totalUnits, terrPerUsr)){
                    //if valid, update the map
                    for (String terrName : serverSelect.getAllName()) {
                        Territory territory = map.getTerritory(terrName);
                        territory.addNUnits(serverSelect.getUnitsNum(terrName));
                        player.addTerritory(territory);
                    }
                    player.send(SUCCESSFUL);
                    break;
                }else{
                    player.send(SELECT_TERR_ERROR);
                }
            }
        }
    }

    void playSingleRound(int roundNum) throws IOException, ClassNotFoundException {
        RoundInfo roundInfo = new RoundInfo(roundNum, map, idToName);
        for (Player<String> player : players) {
            // tell client the round info
            // 1. latest WorldMap
            // 2. mapping between id and color
            // 3. round number
            player.send(roundInfo);
            while (true){
                Object recvRes = player.recv();
                if (recvRes instanceof Action){
                    Action action = (Action) recvRes;
                    //act accordingly based on whether the input actions are valid or not
                    if (action.isValid(map)){
                        //if valid, update the state of the world
                        action.perform(map);
                        player.send(SUCCESSFUL);
                    }else{
                        //otherwise ask user to resend the information
                        player.send(INVALID_ACTION);
                    }
                }else if (recvRes instanceof String && recvRes.equals(ACTION_DONE)){
                    break;
                }else {
                    player.send(INVALID_ACTION);
                }
            }
        }
        // resolve all combats and send out the result
        resolveCombats();
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

    /**
     * This function should be called at the end of each round, will resolve all combats happen in all territories.
     * @throws IOException probably because of stream closed
     */
    void resolveCombats() throws IOException {
        Map<String, Territory> territoryMap = map.getAtlas();
        for (Territory t : territoryMap.values()) {
            // calculate the attack result
            List<AttackResult> attackResultList = t.resolveCombats();

            // generate attack result message based on AttackResult object
            for (AttackResult aR : attackResultList) {
                StringBuilder sb = new StringBuilder();

                Player<String> pAttack = players.get(aR.getAttackerID() - 1);
                Player<String> pDefend = players.get(aR.getDefenderID() - 1);

                Territory destTerritory = map.getAtlas().get(aR.getDestTerritory());
                List<Territory> srcTerritories = new ArrayList<>();
                for (String name : aR.getSrcTerritories()){
                    srcTerritories.add(map.getAtlas().get(name));
                }

                // attack info
                sb.append(pAttack.getColor()).append(" attacks ").append(pDefend.getColor());
                sb.append(" 's territory ").append(destTerritory.getName());

                // change ownership of territory
                if (aR.isAttackerWin()) {
                    sb.append(" ---> attacker wins");
                    // attacker wins, win the destination territory
                    pDefend.loseTerritory(destTerritory);
                    pAttack.addTerritory(destTerritory);
                }
                else {
                    sb.append(" ---> attacker loses");
                    // attacker loses, lose all territories involved in combat
                    for (Territory territory : srcTerritories){
                        pAttack.loseTerritory(territory);
                        pDefend.addTerritory(territory);
                    }
                }

                // send the result of each attack action to all players
                sendAll(sb.toString());
            }
        }
    }

    /**
     * check whether there is a winner
     */
    void checkWinner(){
        int targetNum = map.getTerriNum();
        int totalNum = 0;
        for (Player<String> player : players) {
            int curNum = player.getTerrNum();
            totalNum += curNum;
            if (totalNum > targetNum){
                throw new IllegalStateException("Illegal State of current world");
            }
            if (curNum == targetNum){
                winnerID =  player.getId();
            }
        }
    }

    void endGame() throws IOException {
        System.out.println("Winner ID: " + winnerID);
        if (!idToName.containsKey(winnerID)){
            throw new IllegalArgumentException("Player doesn't exist.");
        }
        String winnerName = idToName.get(winnerID);
        for (Player<String> player : players) {
            if (player.getId() != winnerID){
                player.send(String.format("Winner is the %s player.", winnerName));
            }else{
                player.send(YOU_WINS);
            }
        }
    }

    void addNewUnits(){
        for (Territory territory : map.getAtlas().values()){
            territory.addNUnits(1);
        }
    }

    void runGame() throws IOException, ClassNotFoundException {
        selectTerritory();

        int roundNum = 1;
        while(winnerID < 0) {
            playSingleRound(roundNum);
            // after execute all actions, tell the player to enter next round
            sendAll(ROUND_OVER);
            // check the game result
            checkWinner();
            if(winnerID == -1){
                sendAll("continue");
            }else {
                sendAll(GAME_OVER);
                continue;
            }
            roundNum ++;
            // add one units to all territory
            addNewUnits();
        }
        endGame();
    }
}
