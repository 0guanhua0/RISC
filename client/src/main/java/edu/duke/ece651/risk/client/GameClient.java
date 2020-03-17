package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.network.Client;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static edu.duke.ece651.risk.client.InsPrompt.*;
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
        chooseRoom(scanner);
        // receive player info
        String playerInfo = (String) client.recv();
        player.init(playerInfo);
    }

    /**
     * Playing the game, take action in turn until game finish.
     */
    void playGame(Scanner scanner) throws IOException {
        while (true){
            //recv data from server
            //client.recvData();
            SceneCLI.showMap(null);
            InsPrompt.selfInfo(player.getPlayerName());

            ActionList aL = new ActionList();

            if(!PlayerInput.read(scanner, player, aL)) {
                // user want to quit the game
                break;
            }

            // send actions
            client.send(aL.getActions());
        }
    }

    /**
     * End game, probable only need to receive and print out the game result.
     */
    void endGame(){

    }

    /** ====== helper function ====== **/

    void initPlayer(Scanner scanner) {

    }

    void chooseRoom(Scanner scanner) throws IOException, ClassNotFoundException {
        List<Integer> roomInfo = (List<Integer>) client.recv();
        while (true){
            insAskRoomOption();
            String roomChoice = scanner.nextLine().toLowerCase();
            if (roomChoice.equals("j")){
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
                // create a new room
                client.send("-1");
            }else {
                insInvalidOption();
                continue;
            }
            // receive the result of room choosing
            String result = (String) client.recv();
            if (result.equals(SUCCESSFUL)){
                break;
            }else {
                System.out.println(result);
            }
        }
    }

    void chooseMap(Scanner scanner) throws IOException, ClassNotFoundException {
        MapDataBase<String> allMaps = (MapDataBase<String>) client.recv();
        while (true){

        }
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
