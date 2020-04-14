package edu.duke.ece651.risk.shared.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class wraps up network server socket, can be used to setup a simple TCP server.
 */
public class Server implements AutoCloseable{
    ServerSocket serverSocket;

    /**
     * This is the default constructor, which will initialize a server socket with port number 8000.
     * @throws IOException if creation of the ServerSocket fails(likely due to the port being unavailable).
     */
    public Server() throws IOException {
        this(12345);
    }

    /**
     * The constructor of server class, which allows user to specify a port number.
     * @param portNum port number of the server
     * @throws IOException if creation of the ServerSocket fails(likely due to the port being unavailable).
     */
    public Server(int portNum) throws IOException {
        serverSocket = new ServerSocket(portNum);
        serverSocket.setSoTimeout(1000);
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

    @Override
    public void close() throws Exception {
        serverSocket.close();
    }
}
