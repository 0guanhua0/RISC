package edu.duke.ece651.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class wraps up network server socket, can be used to setup a simple TCP server.
 */
public class Server {
    ServerSocket serverSocket;

    /**
     * This is the default constructor, which will initialize a server socket with port number 8000.
     * @throws IOException if creation of the ServerSocket fails  (likely due to the port being unavailable).
     */
    public Server() throws IOException {
        serverSocket = new ServerSocket(8000);
    }

    /**
     * The constructor of server class, which allows user to specify a port number.
     * @param portNum port number of the server
     * @throws IOException if creation of the ServerSocket fails  (likely due to the port being unavailable).
     */
    public Server(int portNum) throws IOException {
        serverSocket = new ServerSocket(portNum);
    }

    /**
     * This function will keep waiting(blocking) a new connection(the beginner in this case).
     * @return the socket info of the beginner
     */
    public Socket waitBeginner(){
        Socket s = null;
        while (s == null){
            s = acceptOrNull();
        }
        return s;
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

    /**
     * This function will send the data to target socket.
     * @param s target socket
     * @param data data to be sent
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
     */
    public static String recvData(Socket s) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        return bufferedReader.readLine();
    }
}
