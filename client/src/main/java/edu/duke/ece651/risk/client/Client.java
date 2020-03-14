package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.network.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class Client {
    private static int port = 12345;

    public static void main (String[] args) throws IOException, InterruptedException {

        edu.duke.ece651.risk.shared.network.Client client = new edu.duke.ece651.risk.shared.network.Client();
        client.init("127.0.0.1", port);

        Player player = new Player(0, "A");

        //build network connection
        new Thread(()->{
            try {
                Server serve = new Server(port);
                Socket socket = serve.accept();
                System.out.println(Server.recvStr(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(500);

        //while loop
        while (true) {
            //TODO: read from input
            //if end game then quit
            //curr round
            Display.showMap();
            Instruction.selfInfo(player.getPlayerName());

            HashMap<String, List<Action>> actions = new HashMap<>();
            PlayerInput.read(System.in, player, actions);

            client.send(actions);
        }

    }
}
