package edu.duke.ece651.risk.client;

import java.io.IOException;

/**
 * main class for player
 */

public class GameClient {
    private static int port = 8080;
    private static String ip = "127.0.0.1";

    public static void main(String[] args) throws IOException, InterruptedException {

        edu.duke.ece651.risk.shared.network.Client client = new edu.duke.ece651.risk.shared.network.Client();
        client.init(ip, port);

        Player player = new Player(0, "A");

        //curr round
        while (true) {
            //recv data from server
            //client.recvData();
            SceneCLI.showMap();
            InsPrompt.selfInfo(player.getPlayerName());

            ActionList aL = new ActionList();
            PlayerInput.read(System.in, player, aL);

            client.send(aL.getActions());
        }

    }
}
