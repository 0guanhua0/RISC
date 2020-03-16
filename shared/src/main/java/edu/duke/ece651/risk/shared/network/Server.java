package edu.duke.ece651.risk.shared.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
        serverSocket = new ServerSocket(12345);
        serverSocket.setSoTimeout(1000);
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
     * Send the data to the output stream
     * @param out output stream
     * @param object data to be sent
     * @throws IOException probably because the stream is already closed
     */
    public static void send(OutputStream out, Object object) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }

    /**
     * This function will receive an object from target stream.
     * @param in input stream
     * @return received data
     * @throws IOException probably because the stream is already closed
     * @throws ClassNotFoundException probably because receive some illegal data
     */
    public static Object recv(InputStream in) throws IOException, ClassNotFoundException {
        return new ObjectInputStream(in).readObject();
    }

}
