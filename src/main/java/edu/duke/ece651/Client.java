package edu.duke.ece651;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client {

    /* ====== helper function ====== */
    String getHostByName(String hostStr) throws UnknownHostException {
        InetAddress addr = InetAddress.getByName(hostStr);
        return addr.getHostAddress();
    }
}
