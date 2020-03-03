package edu.duke.ece651.server;

import edu.duke.ece651.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class wraps up network server socket, can be used to setup a simple TCP server.
 */
public class Server {
    ServerSocket serverSocket;

    /**
     * This is the default constructor, which will initialize a server socket with port number 8000.
     * @throws IOException if creation of the ServerSocket fails(likely due to the port being unavailable).
     */
    public Server() throws IOException {
        serverSocket = new ServerSocket(8000);
    }

    /**
     * The constructor of server class, which allows user to specify a port number.
     * @param portNum port number of the server
     * @throws IOException if creation of the ServerSocket fails(likely due to the port being unavailable).
     */
    public Server(int portNum) throws IOException {
        serverSocket = new ServerSocket(portNum);
    }

    /**
     * This function will keep waiting(blocking) a new connection(the beginner in this case).
     * @return the socket info of the beginner
     */
    public Socket waitBeginner(){
        List<Socket> players = waitAllPlayers(1);
        return players.get(0);
    }

    /**
     * This function will keep accepting new connection, until the number of connections reachs cnt.
     * @param cnt the desired number of connections(i.e. players)
     * @return List of the socket info of all connections(players).
     */
    public List<Socket> waitAllPlayers(int cnt){
        // TODO: maybe send some initial data to each new player
        List<Socket> players = new ArrayList<>(cnt);
        while (players.size() < cnt){
            Socket s = acceptOrNull();
            if (s != null){
                players.add(s);
            }
        }
        return players;
    }

    /**
     * Receive all actions one user want to perform in one round.
     * @param s target socket
     * @return Map of actions; key is action type, e.g. move; value is list of actions
     * @throws IOException probably because the stream is already closed
     */
    public static HashMap<String, List<Action>> recvActions(Socket s) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        return Deserializer.deserializeActions(bufferedReader.readLine());
    }

    /**
     * This function will send the data to target socket.
     * @param s target socket
     * @param data data to be sent
     * @throws IOException probably because the stream is already closed
     */
    public static void sendData(Socket s, String data) throws IOException {
        PrintWriter printWriter = new PrintWriter(s.getOutputStream());
        printWriter.println(data);
        printWriter.flush();
    }

    /**
     * This function will receive one line from the target socket.
     * @param s target socket
     * @return received data
     * @throws IOException probably because the stream is already closed
     */
    public static String recvData(Socket s) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        return bufferedReader.readLine();
    }

    /**
     * This is a helper method to accept a socket from the ServerSocket
     * or return null if it timeout.
     */
    Socket acceptOrNull() {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            return null;
        }
    }
}
