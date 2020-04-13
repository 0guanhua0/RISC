package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.SMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the thread for chat function in one game.
 * This thread will open another N thread for N players in current room.
 */
public class ChatThread<T> extends Thread{

    private List<Player<T>> allPlayers;
    private List<Thread> threads;

    public ChatThread(List<Player<T>> allPlayers) {
        this.allPlayers = allPlayers;
        threads = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            for (Player<?> player : allPlayers){
                // open a new thread for each player
                Thread t = new Thread(() -> {
                    while (!Thread.currentThread().isInterrupted()){
                        if (player.isConnect()){
                            Object object = player.recvChatMessage();
                            if (object instanceof SMessage){
                                SMessage message = (SMessage) object;
                                System.out.println(message.toString());
                                if (message.getReceiverID() == -1){
                                    sendAllExcept(message, player.getId());
                                }else {
                                    sendTo(message, message.getReceiverID());
                                }
                            }
                        }
                    }
                });
                threads.add(t);
                t.start();
            }
            while (!Thread.currentThread().isInterrupted()){ }
            for (Thread t : threads){
                t.interrupt();
            }
        }catch (Exception ignored){
        }
    }

    /**
     * Send the message to a specific player.
     * @param data SMessage
     * @param playerID id of the receiver
     */
    synchronized void sendTo(Object data, int playerID){
        for (Player<?> player : allPlayers) {
            if (player.isConnect() && player.getId() == playerID) {
                // NOTE: should use the chat socket here
                player.sendChatMessage(data);
            }
        }
    }

    /**
     * Broadcase the message to all players except one.
     * @param data SMessage
     * @param playerID id of the excluded player
     */
    synchronized void sendAllExcept(Object data, int playerID)  {
        for (Player<?> player : allPlayers) {
            if (player.isConnect() && player.getId() != playerID) {
                // NOTE: should use the chat socket here
                player.sendChatMessage(data);
            }
        }
    }
}
