package edu.duke.ece651.client;

import static org.junit.jupiter.api.Assertions.*;

import edu.duke.ece651.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTest {

    @Test
    public void testSendRecvData() throws IOException, InterruptedException {
        // different with sever test, avoid port conflict
        int port = 8081;
        String msgCTS = "Hello server";
        String msgSTC = "Hi client";
        new Thread(() -> {
            try {
                Server server = new Server(port);
                Socket socket = server.waitBeginner();
                assertNotNull(socket);
                assertEquals(msgCTS, Server.recvData(socket));
                Server.sendData(socket, msgSTC);
                socket.shutdownOutput();
                Server.sendData(socket, msgSTC);
            }catch (IOException e){
                System.out.println(e.toString());
            }
        }).start();
        Thread.sleep(500);
        Client client = new Client();
        client.init("127.0.0.1", port);
        client.sendData(msgCTS);
        assertEquals(msgSTC, client.recvData());
    }

    @Test
    public void testGetHostByName() throws UnknownHostException {
        Client client = new Client();
        assertEquals("67.159.88.31", client.getHostByName("vcm-12305.vm.duke.edu"));
        assertThrows(UnknownHostException.class, ()->{client.getHostByName("hello");});
    }

} 
