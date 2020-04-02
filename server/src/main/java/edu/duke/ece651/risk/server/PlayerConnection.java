package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.player.Player;

import java.io.IOException;

/**
 * handle player who dis-connect
 */
public class PlayerConnection {
    //set the connection to mocking input
    public void DisCon(Player player) throws InterruptedException {

        player.disConnect();

    }

    //reset the connection to the socket
    public void ReCon(User user, Player player) throws IOException {
        player.setConnection(user.getIn(), user.getOut());
    }
}
