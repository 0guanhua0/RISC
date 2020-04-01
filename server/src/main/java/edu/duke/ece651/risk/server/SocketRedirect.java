package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.player.Player;
import org.json.JSONObject;

import java.io.IOException;

import static edu.duke.ece651.risk.shared.Constant.*;

/**
 * recognize the intention of different socket
 * redirect socket to proper connection
 * return according info & close socket
 *
 */
public class SocketRedirect {

    public void redirect(Player<?> player) throws IOException, ClassNotFoundException {
        String msg = (String) player.recv();
        JSONObject obj = new JSONObject(msg);

        String userName = obj.getString(USER_NAME);
        String action = obj.getString(ACTION);

        //check user is in validate list

        //if yes, proceed
        //if no, return error


    }

    //function handle income socket

    //get available room
    //return available room

    //get room player is in
    //return room player is in

    //join the available room
    //redirect the socket in/out

    //create new room

}
