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
        SMessage message1 = new SMessage(1, 1, 2, "xxx", "hello");
        // send to all
        SMessage message2 = new SMessage(1, 1, -1, "xxx", "hello");

        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        ByteArrayOutputStream out3 = new ByteArrayOutputStream();
        ByteArrayOutputStream out4 = new ByteArrayOutputStream();

        Player<String> p1 = new PlayerV1<String>("", 1);
        p1.setChatStream(new ObjectInputStream(setupMockInput(new ArrayList<Object>(Arrays.asList(message1, message2)))), new ObjectOutputStream(out1));

        Player<String> p2 = new PlayerV1<String>("", 2);
        p2.setChatStream(new ObjectInputStream(setupMockInput(new ArrayList<Object>(nulls))), new ObjectOutputStream(out2));

        Player<String> p3 = new PlayerV1<String>("", 3);
        p3.setChatStream(new ObjectInputStream(setupMockInput(new ArrayList<Object>(nulls))), new ObjectOutputStream(out3));

        Player<String> p4 = new PlayerV1<String>("", 3);
        p4.setChatStream(new ObjectInputStream(setupMockInput(new ArrayList<Object>())), new ObjectOutputStream(out4));

        List<Player<String>> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
        ChatThread<String> thread = new ChatThread<>(players);
        thread.start();
        Thread.sleep(10);
        thread.interrupt();
        thread.join();
        assertEquals(2, readAllChatFromObjectStream(out2).size());
        // player3 only receive the broadcast message
        assertEquals(1, readAllChatFromObjectStream(out3).size());
    }
    
    @Test
    public void testSendTo() { 
        
    }
    
    @Test
    public void testSendAllExcept() { 
        
    }
    
    @Test
    public void testHashCode() { 
        
    }
    
    @Test
    public void testEquals() { 
        
    }
    
    @Test
    public void testUncaughtException() { 
        
    }
    

} 
