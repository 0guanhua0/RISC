package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.network.Server;

import java.io.IOException;
import java.net.Socket;

/**
 * main class for player
 */

public class GameClient {
    private static int port = 8080;
    private static String ip = "127.0.0.1";

    public static void main(String[] args) throws IOException, InterruptedException {

        //build network connection
        new Thread(() -> {
            try {
                Server serve = new Server(port);
                Socket socket = serve.accept();
                System.out.println(Server.recvStr(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(500);

        edu.duke.ece651.risk.shared.network.Client client = new edu.duke.ece651.risk.shared.network.Client();
        client.init(ip, port);

        Player player = new Player(0, "A");

        //curr round
        while (true) {
            Display.showMap();
            Instruction.selfInfo(player.getPlayerName());

            ActionList aL = new ActionList();
            PlayerInput.read(System.in, player, aL);

            client.send(aL.getActions());
        }

    }
}
