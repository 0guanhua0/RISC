package edu.duke.ece651.risk.client;


import edu.duke.ece651.risk.shared.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        /*
        Set<String> s = new HashSet<String>();
        s.add("Mikdemia");
        s.add("Elantris");
        terr.put("Narnia",s);
        Map<String, Set<String>> terr= new HashMap<String, Set<String>>();
        */
        instruction.selfInfo("A");
//        display.showMap();
        instruction.actInfo("A");

        String tmp= "hello from client";

        edu.duke.ece651.risk.shared.Client c = new edu.duke.ece651.risk.shared.Client();
        c.init("127.0.0.1", port);
        c.send(tmp);

    }
}
