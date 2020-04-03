package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.Client;
import edu.duke.ece651.risk.shared.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    //static GameTest serve;

   private static final int SLEEP_TIME = 500;
    private static final int PORT = 8080;

  @Test
  void acceptSocket() throws IOException, InterruptedException {
        String msgCTS = "Hello server";
        String msgSTC = "Hi client";

        new Thread(() -> {
            try {
                Game game = new Game();
                game.acceptSocket();
               // assertNotNull(game);
                assertEquals(msgCTS, Server.recvStr(game.socket));
                //game.sendMsg(msgCTS);
                Server.send(game.socket, msgSTC);

            }catch (IOException e){
                System.out.println(e.toString());
            }
        }).start();
        Thread.sleep(500);
        Client client = new Client();
        client.init("127.0.0.1", PORT);
        client.send(msgCTS);
    assertEquals(msgSTC, client.recvData());
}


    @Test
    void sendSocket() {
    }

    @Test
    void main() {
    }
}
