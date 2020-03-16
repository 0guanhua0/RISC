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
    /** WARNING!!! Do not use ObjectInputStream here, it will stuck
     * when call new ObjectInputStream(inStream), it will try to read stream header info from inStream
     * (which will block forever, since no header for now)
     */
    InputStream in;
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
        in = socket.getInputStream();
        out = new ObjectOutputStream(socket.getOutputStream());
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
        return new ObjectInputStream(in).readObject();
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
