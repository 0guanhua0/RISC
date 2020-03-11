package edu.duke.ece651.risk.server;

import org.junit.jupiter.api.Test;

class GameTest {
    static GameTest serve;
   private static final int SLEEP_TIME = 500;
    private static final int PORT = 8080;

//  @Test
//    void acceptSocket() {
//        String msgCTS = "Hello server";
//        String msgSTC = "Hi client";
//
//        new Thread(() -> {
//            try {
//                Socket socket = serve.accept();
//                assertNotNull(socket);
//                //   assertEquals(msgCTS, Server.recvStr(socket));
//                Server.send(socket, msgSTC);
//                socket.shutdownOutput();
//                Server.send(socket, msgSTC);
//            }catch (IOException e){
//                System.out.println(e.toString());
//            }
//        }).start();
//        Client client = new Client();
//        client.init("127.0.0.1", PORT);
//        client.sendData(msgCTS);
//        assertEquals(msgSTC, client.recvData());
//    }


    @Test
    void sendSocket() {

    }

    @Test
    void main() {
    }
}
