package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.network.Client;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import static edu.duke.ece651.risk.client.InsPrompt.*;
import static edu.duke.ece651.risk.shared.Constant.GAME_OVER;
import static edu.duke.ece651.risk.shared.Constant.SUCCESSFUL;

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
        System.out.println("start initial the game");
        initGame(scanner);
        System.out.println("finish initial the game");
        System.out.println("start playing the game");
        playGame(scanner);
        System.out.println("finish playing the game");
        System.out.println("end game");
        endGame();
    }

    /**
     * Initial the game, probably need to do
     * 1) connect to the game server (and receive the "hello" message)
     * 2) ask user whether create a new room or join an existing one
     * 3) receive the initial message about player info from game server
     */
    void initGame(Scanner scanner) throws IOException, ClassNotFoundException {
        JSONObject config = new JSONObject(readConfigFile());
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

        int round = 1;
        while (true){
            showMsg("====== Round " + round + " ======");
            // receive the latest world map
            WorldMap<String> worldMap = (WorldMap<String>) client.recv();

            SceneCLI.showMap(worldMap);
            InsPrompt.selfInfo(player.getPlayerName());

            Action action = PlayerInput.readValidAction(scanner, player);
            // send actions
            client.send(action);

            // receive result in the end of each round
            String result = (String) client.recv();
            if (result.equals(GAME_OVER)){
                break;
            }
        }
    }

    /**
     * End game, probable only need to receive and print out the game result.
     */
    void endGame(){

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

    void selectTerritory(Scanner scanner) {

    }

    String readConfigFile() throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        Stream<String> stream = Files.lines(Paths.get("./config_file/config.txt"));
        stream.forEach(s -> contentBuilder.append(s).append("\n"));
        stream.close();
        return contentBuilder.toString();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        GameClient gameClient = new GameClient();
        gameClient.run(new Scanner(System.in));
    }
}
