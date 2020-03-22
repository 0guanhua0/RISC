package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.ToClientMsg.ClientSelect;
import edu.duke.ece651.risk.shared.ToClientMsg.RoundInfo;
import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.network.Client;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static edu.duke.ece651.risk.client.InsPrompt.*;
import static edu.duke.ece651.risk.client.PlayerInput.readValidInt;
import static edu.duke.ece651.risk.shared.Constant.*;
import static edu.duke.ece651.risk.shared.Utils.readFileToString;

/**
 * main class for player
 */

public class GameClient {
    Player<String> player;
    Client client;

    GameClient() {
        player = new Player<>();
    }

    public void run(Scanner scanner) throws IOException, ClassNotFoundException {
        initGame(scanner);
        playGame(scanner);
        endGame();
    }

    /**
     * Initial the game, probably need to do
     * 1) connect to the game server (and receive the "hello" message)
     * 2) ask user whether create a new room or join an existing one
     * 3) receive the initial message about player info from game server
     */
    void initGame(Scanner scanner) throws IOException, ClassNotFoundException {
        JSONObject config = new JSONObject(readFileToString("../config_file/client_config.txt"));
        client = new Client(config.getString("host"), config.getInt("port"));
        // receive hello message
        String hello = (String) client.recv();
        showMsg(hello);
        // choose room
        boolean newRoom = chooseRoom(scanner);
        // if user create a new room, he/she also need to choose a map for this room
        if (newRoom){
            chooseMap(scanner);
        }
        // initialize the player info
        initPlayer();
    }

    /**
     * Playing the game, take action in turn until game finish.
     */
    void playGame(Scanner scanner) throws IOException, ClassNotFoundException {
        selectTerritory(scanner);

        String result = "";
        while (!result.equals(GAME_OVER)){
            // receive the round info
            RoundInfo roundInfo = (RoundInfo) client.recv();

            showMsg("====== Round " + roundInfo.getRoundNum() + " ======");

            SceneCLI.showMap(roundInfo.getMap(), roundInfo.getIdToName());
            InsPrompt.selfInfo(player.getPlayerName());

            // keep asking action until user specify done
            while (true){
                Action action = PlayerInput.readValidAction(scanner, player);
                if (action != null){
                    client.send(action);
                    // receive successful or error message
                    checkResult();
                }else {
                    break;
                }
            }

            // action == null, represent done
            client.send(ACTION_DONE);
            // after submit all actions, wait for the server to publish attack result
            receiveAttackResult();
            // receive game result in the end of each round
            result = (String) client.recv();
        }
    }

    /**
     * End game, probable only need to receive and print out the game result.
     */
    void endGame() throws IOException, ClassNotFoundException {
        // TODO: receive & print the game result
        showMsg((String) client.recv());
    }

    /** ====== helper function ====== **/

    void initPlayer() throws IOException, ClassNotFoundException {
        // receive player info
        String playerInfo = (String) client.recv();
        player.init(playerInfo);
    }

    /**
     * Interacting with user and server to choose a room.
     * 1) receive a room list from server
     * 2) interact with user to ask a valid room number(-1 represents new room)
     * @param scanner scanner, readAction user input
     * @return true is user want to create a new room; false if user want to join an existing room
     * @throws IOException probably because of stream is closed
     * @throws ClassNotFoundException this exception shout not happen unless you don't follow the protocol we defined
     */
    boolean chooseRoom(Scanner scanner) throws IOException, ClassNotFoundException {
        boolean isNewRoom;
        List<Integer> roomInfo = (List<Integer>) client.recv();
        while (true){
            insAskRoomOption();
            String roomChoice = scanner.nextLine().toLowerCase();
            if (roomChoice.equals("j")){
                isNewRoom = false;
                // join an existing room
                insShowRooms(roomInfo);
                // ask for room number
                String roomNum = scanner.nextLine();
                if (Format.isNumeric(roomNum) && roomInfo.contains(Integer.parseInt(roomNum))){
                    client.send(roomNum);
                }else {
                    insInvalidOption();
                    continue;
                }
            }else if (roomChoice.equals("c")){
                isNewRoom = true;
                // create a new room
                client.send("-1");
            }else {
                insInvalidOption();
                continue;
            }
            if (checkResult()){
                return isNewRoom;
            }
        }
    }

    void chooseMap(Scanner scanner) throws IOException, ClassNotFoundException {
        MapDataBase<String> mapDB = (MapDataBase<String>) client.recv();
        Map<String, WorldMap<String>> allMaps = mapDB.getAllMaps();

        // construct a mapping between number and map name(so that we can ask user to input number rather than map name)
        Map<Integer, String> numMap = new HashMap<>();
        int num = 1;
        for (String name : allMaps.keySet()){
            numMap.put(num, name);
            num++;
        }

        insShowMaps(allMaps);

        while (true){
            insShowMapOption(numMap);
            String choice = scanner.nextLine().toLowerCase();
            if (Format.isNumeric(choice) && numMap.containsKey(Integer.parseInt(choice))){
                // a valid choice, send back the map name
                client.send(numMap.get(Integer.parseInt(choice)));
            }else {
                showMsg("Invalid choice, try again.");
                continue;
            }
            if (checkResult()){
                break;
            }
        }
    }

    /**
     * This function will receive a result message from server, and will print out the error message if fail
     * @return true is result is successful
     * @throws IOException probably because of stream is closed
     * @throws ClassNotFoundException this exception shout not happen unless you don't follow the protocol we defined
     */
    boolean checkResult() throws IOException, ClassNotFoundException {
        // receive the result of room choosing
        String result = (String) client.recv();
        if (result.equals(SUCCESSFUL)){
            return true;
        }else {
            System.out.println(result);
            return false;
        }
    }

    void selectTerritory(Scanner scanner) throws IOException, ClassNotFoundException {
        ClientSelect select = (ClientSelect) client.recv();
        while (true){
            Map<String, Integer> selection = new HashMap<>();

            // display the map
            SceneCLI.showMap(select.getMap());
            // display the territory group
            List<Set<String>> terrGroup = select.getGroups();
            SceneCLI.showTerritoryGroup(terrGroup);

            System.out.println("Which group of territories you want to choose?");
            int index = readValidInt(scanner, 1, terrGroup.size()) - 1;
            List<String> groupChosen = new ArrayList<>(terrGroup.get(index));
            // initialize the result
            for (String name : groupChosen){
                selection.put(name, 0);
            }

            System.out.println("Start assigning units");
            int totalUnits = select.getUnitsTotal();
            while (totalUnits > 0){
                System.out.println(String.format("\nTerritories you have(%d units left):", totalUnits));
                for (int i = 0; i < groupChosen.size(); i++){
                    System.out.println(String.format("%d. %s", i + 1, groupChosen.get(i)));
                }
                System.out.println("Please input the index of territory");
                int tIndex = readValidInt(scanner, 1, groupChosen.size()) - 1;
                String name = groupChosen.get(tIndex);
                System.out.println(String.format("How many units you want to put in \"%s\"(%d units left)", name, totalUnits));
                int units= readValidInt(scanner, 1, totalUnits);
                int oldCnt = selection.get(name);
                selection.replace(name, oldCnt + units);
                totalUnits -= units;
            }

            ServerSelect serverSelect = new ServerSelect(selection);
            client.send(serverSelect);
            if (checkResult()){
                break;
            }
        }
    }

    /**
     * Since the requirement require send the result of each attack action to *all* players,
     * we use a while loop to keep receiving all results until OVER
     */
    void receiveAttackResult() throws IOException, ClassNotFoundException {
        while (true){
            String result = (String) client.recv();
            if (result.equals(ROUND_OVER)){
                break;
            }else {
                showMsg(result);
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        GameClient gameClient = new GameClient();
        gameClient.run(new Scanner(System.in));
    }
}
