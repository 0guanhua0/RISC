package edu.duke.ece651.risk.shared.network;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class wraps up the network socket, and can be used to communicate with remote server.
 */
public class Client {
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

    /**
     * The default constructor.
     * After this, you probably need to call init() explicitly to initialize client(i.e. connect to server)
     */
    public Client(){}

    /**
     * Construct the client with host name and port number, and the client will connect to it automatically.
     * @param hostName host name, e.g. vcm-12305.vm.duke.edu
     * @param port port number, 8000
     * @throws IOException probably because of invalid host name or wrong port number
     */
    public Client(String hostName, int port) throws IOException {
        init(getHostByName(hostName), port);
    }

    /**
     * Initialize the client object, connect to the remote server.
     * @param ip ip address, e.g. 127.0.0.1
     * @param port port number, e.g. 8000
     * @throws IOException probably because of invalid host name or wrong port number
     */
    public void init(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        /*
          WARNING!!! here you should initialize "out-in" in this order!!! Otherwise, it will
          cause deadlock.(Because server will initialize in "in-out" order.
          https://stackoverflow.com/questions/21075453/objectinputstream-from-socket-getinputstream
         */
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Send data to remote server.
     * @param object data to be sent
     * @throws IOException probably output stream closed
     */
    public void send(Object object) throws IOException {
        out.writeObject(object);
        out.flush();
    }

    /**
     * Receive data from remote server.
     * @return object received(cast to whatever you need)
     * @throws IOException probably input stream closed
     * @throws ClassNotFoundException probably because receive wrong format(not an Object)
     */
    public Object recv() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    /**
     *  This function translate the host name to its corresponding IP address.
     * @param hostStr string of the host name
     * @return the corresponding IP address
     * @throws UnknownHostException if the host is invalid
     */
    String getHostByName(String hostStr) throws UnknownHostException {
        InetAddress addr = InetAddress.getByName(hostStr);
        return addr.getHostAddress();
    }
}
