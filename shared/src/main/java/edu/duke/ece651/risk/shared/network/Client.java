package edu.duke.ece651.risk.shared.network;

import com.google.gson.Gson;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

/**
 * This class wraps up the network socket, and can be used to communicate with remote server.
 */
public class Client {
    private BufferedReader in;
    private PrintWriter out;
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
        Socket socket = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());
    }

    /**
     * This function will send all actions user specify in one round.
     * @param actions map of actions; key is the action type, e.g. move; value is list of actions
     */
    public void send(HashMap<String, List<Action>> actions){
        String str = new Gson().toJson(actions);
        send(str);
    }

    /**
     * Send data to remote server.
     * @param data data to be sent
     */
    public void send(String data){
        out.println(data);
        // finish writing data, flush it(remote can use readLine to receive)
        out.flush();
    }

    /**
     * Receive data from remote server.
     * @return data received
     * @throws IOException probably input stream closed
     */
    public String recvData() throws IOException {
        return in.readLine();
    }

    /**
     * Receive a world map object from remote server.
     * @return a world map object
     * @throws IOException probably input stream closed
     * @throws ClassNotFoundException probably json string is invalid(e.g. not generate by calling toJSON() method)
     */
    public WorldMap recvMap() throws IOException, ClassNotFoundException {
        return Deserializer.deserializeWorldMap(in.readLine());
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
