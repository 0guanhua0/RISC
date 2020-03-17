package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.network.Client;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * main class for player
 */

public class GameClient {
    Player player;
    Client client;

    GameClient() {
        player = new Player();
    }

    public void run(Scanner scanner) throws IOException {
        initGame(scanner);
        playGame(scanner);
        endGame();
    }

    /**
     * Initial the game, probably need to do
     * 1) connect to the game server
     * 2) ask user whether create a new room or join an existing one
     * 3) receive the initial message about player info from game server
     */
    void initGame(Scanner scanner) throws IOException {
        JSONObject config = new JSONObject(readConfigFile());
        client = new Client(config.getString("host"), config.getInt("port"));
        // TODO: interact with server to determine room
        // TODO: read initial message from server
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

    String readConfigFile() throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        Stream<String> stream = Files.lines(Paths.get("./config_file/config.txt"));
        stream.forEach(s -> contentBuilder.append(s).append("\n"));
        stream.close();
        return contentBuilder.toString();
    }

    public static void main(String[] args) throws IOException {
        GameClient gameClient = new GameClient();
        gameClient.run(new Scanner(System.in));
    }
}
