package edu.duke.ece651.risk.server;

import static edu.duke.ece651.risk.shared.Mock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import edu.duke.ece651.risk.shared.player.SMessage;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatThreadTest { 

    @Test
    public void testRun() throws IOException, InterruptedException, ClassNotFoundException {
        List<Object> nulls = new ArrayList<>();
        for (int i = 0; i < 10000; i++){
            nulls.add(null);
        }
        String str = "SUCCESSFUL";
        // send to 2
        SMessage message1 = new SMessage(1, 1, 3, "xxx1", "hello");
        // send to all
        SMessage message2 = new SMessage(1, 2, -1, "xxx2", "hello");

        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        ByteArrayOutputStream out3 = new ByteArrayOutputStream();
        ByteArrayOutputStream out4 = new ByteArrayOutputStream();

        Player<String> p1 = new PlayerV1<String>("", 1);
        // player1 send to player2
        p1.setChatStream(new ObjectInputStream(setupMockInput(new ArrayList<Object>(Arrays.asList(message1)))), new ObjectOutputStream(out1));

        Player<String> p2 = new PlayerV1<String>("", 2);
        // player2 broadcast to all
        p2.setChatStream(new ObjectInputStream(setupMockInput(new ArrayList<>())), new ObjectOutputStream(out2));
        p2.recvChatMessage();

        Player<String> p3 = new PlayerV1<String>("", 3);
        p3.setChatStream(new ObjectInputStream(setupMockInput(new ArrayList<Object>(Arrays.asList(message2)))), new ObjectOutputStream(out3));

        Player<String> p4 = new PlayerV1<String>("", 4);
        p4.setChatStream(new ObjectInputStream(setupMockInput(new ArrayList<Object>(nulls))), new ObjectOutputStream(out4));
//        // make it disconnect
//        p4.recvChatMessage();
//        assertFalse(p4.isConnect());

        List<Player<String>> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
        ChatThread<String> thread = new ChatThread<>(players);

        thread.start();
        thread.interrupt();
        thread.join();
        Thread.sleep(1000);
        // player1 only receive the broadcast message
        assertEquals(1, readAllChatFromObjectStream(out1).size());
        // player2 only receive the message send from 1
        assertEquals(0, readAllChatFromObjectStream(out2).size());
        // player3 only receive the broadcast message
        assertEquals(1, readAllChatFromObjectStream(out3).size());
        assertEquals(1, readAllChatFromObjectStream(out4).size());
    }

} 
