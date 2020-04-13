package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import edu.duke.ece651.risk.shared.player.SMessage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.duke.ece651.risk.shared.Mock.readAllChatFromObjectStream;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ChatThreadTest { 

    @Test
    public void testRun() throws IOException, InterruptedException, ClassNotFoundException {
        List<Object> nulls = new ArrayList<>();
        for (int i = 0; i < 10000; i++){
            nulls.add(null);
        }
        String str = "SUCCESSFUL";
        // send to 3
        SMessage message1 = new SMessage(1, 1, 3, "xxx1", "hello 3");
        // send to all
        SMessage message2 = new SMessage(1, 3, -1, "xxx3", "hello all");

        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        ByteArrayOutputStream out3 = new ByteArrayOutputStream();
        ByteArrayOutputStream out4 = new ByteArrayOutputStream();

        Player<String> p1 = new PlayerV1<String>("", 1);
        // p1 send message to p3, and then use 10000 nulls to make it connected(otherwise, next loop it will become disconnect)
        List<Object> data1 = new ArrayList<>(nulls);
        data1.add(0, message1);
        p1.setChatStream(new ObjectInputStream(setupMockInput(data1)), new ObjectOutputStream(out1));

        Player<String> p2 = new PlayerV1<String>("", 2);
        p2.setChatStream(new ObjectInputStream(setupMockInput(new ArrayList<>())), new ObjectOutputStream(out2));
        // make p2 disconnect
        p2.recvChatMessage();
        assertFalse(p2.isConnect());

        Player<String> p3 = new PlayerV1<String>("", 3);
        // p3 broadcast message to everybody, and then use nulls to keep connecting
        List<Object> data3 = new ArrayList<>(nulls);
        data3.add(0, message2);
        p3.setChatStream(new ObjectInputStream(setupMockInput(data3)), new ObjectOutputStream(out3));

        Player<String> p4 = new PlayerV1<String>("", 4);
        // p4 use nulls to keep connecting, to receive message
        p4.setChatStream(new ObjectInputStream(setupMockInput(new ArrayList<Object>(nulls))), new ObjectOutputStream(out4));

        List<Player<String>> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
        ChatThread<String> thread = new ChatThread<>(players);
        thread.start();
        // give the thread enough time to send & receive message
	    Thread.sleep(1000);
        thread.interrupt();
        thread.join();
        // player1 only receive the broadcast message
        assertEquals(1, readAllChatFromObjectStream(out1).size());
        // player2 disconnect at the beginning, receive nothing
        assertEquals(0, readAllChatFromObjectStream(out2).size());
        // player3 only receive the message player1 send
        assertEquals(1, readAllChatFromObjectStream(out3).size());
        // player4 only receive the broadcast message
        assertEquals(1, readAllChatFromObjectStream(out4).size());
    }

} 
