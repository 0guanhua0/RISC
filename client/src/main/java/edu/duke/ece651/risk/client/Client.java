package edu.duke.ece651.risk.client;


import edu.duke.ece651.risk.shared.network.Server;

import java.io.IOException;
import java.net.Socket;

public class Client{
    public static void main (String[] args) throws IOException, InterruptedException {
        int port = 12345;
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
        Instruction instruction = new Instruction0() ;
        Display display = new Display0();

        instruction.selfInfo("A");
        display.showMap();
        instruction.actInfo("A");

        String tmp= new String("hello from client");

        edu.duke.ece651.risk.shared.network.Client c = new edu.duke.ece651.risk.shared.network.Client();
        c.init("127.0.0.1", port);
        c.send(tmp);

    }
}
