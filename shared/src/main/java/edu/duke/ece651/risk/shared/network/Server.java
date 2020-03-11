package edu.duke.ece651.risk.shared.network;

import com.google.gson.Gson;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
    public Socket accept(){
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * This function will send the data to target socket.
     * @param s target socket
     * @param data data string to be sent
     * @throws IOException probably because the stream is already closed
     */
    public static void send(Socket s, String data) throws IOException {
        PrintWriter printWriter = new PrintWriter(s.getOutputStream());
        printWriter.println(data);
        printWriter.flush();
    }

    /**
     * This function will send the world map to target socket.
     * @param s target socket
     * @param map world map object to be sent
     * @throws IOException probably because the stream is already closed
     */
    public static void send(Socket s, WorldMap map) throws IOException {
        send(s, map.toJSON());
    }

    /**
     * This function will receive one line from the target socket.
     * @param s target socket
     * @return received data
     * @throws IOException probably because the stream is already closed
     */
    public static String recvStr(Socket s) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        return bufferedReader.readLine();
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
}
