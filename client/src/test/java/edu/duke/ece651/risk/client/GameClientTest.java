package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.network.Server;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

class GameClientTest {

    private static int port = 8080;
    private static String ip = "127.0.0.1";

    @Test
    void main() throws InterruptedException, IOException {
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

        String[] args = new String[0];

        String s = "Q\n";
        InputStream stream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));

        System.setIn(stream);

        GameClient.main(args);




    }
}