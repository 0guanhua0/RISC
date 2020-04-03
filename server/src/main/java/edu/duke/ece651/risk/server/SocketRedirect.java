package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.player.Player;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static edu.duke.ece651.risk.shared.Constant.*;

/**
 * recognize the intention of different socket
 * redirect socket to proper connection
 * return according info & close socket
 *
 */
public class SocketRedirect {

    public static void redirect(User user, UserList userList, SQL db, List availableRooms, Map<Integer, Room> rooms) throws IOException, ClassNotFoundException, SQLException {
        //header info from client
        String msg = (String) user.recv();
        JSONObject obj = new JSONObject(msg);

        String userName = obj.getString(USER_NAME);
        String userPassword = obj.getString(USER_PASSWORD);
        String action = obj.getString(ACTION);

        //check user is try to login/sign up
        if (action.equals(LOGIN)) {
            UserValidation.validate(user, db, obj);
            return;
        }

        if (action.equals(SIGNUP)) {
            if (UserValidation.validate(user,db, obj) && !userList.hasUser(userName)) {
                //userList.addUser(userName);
            }
            return;
        }

        //user try to play game
        //check user is in validate list
        //if no, return error
        if (!userList.hasUser(userName)) {
            //invalid user
            user.send(INVALID_USER);
            return;
        }

        //if yes, proceed
        //according to actual action to redirect
        //create new room
        if (action.equals(CREATE_GAME)) {
            //proceed to original process
            return;

        }

        if (action.equals(GET_WAIT_ROOM)) {
            user.send(availableRooms);
            return;
        }


        //todo; return the room user has join
        if (action.equals(GET_IN_ROOM)) {
            user.send(user.getRoomList());
            return;
        }

        //join the existing game
        //if new player, then just new player
        //if existing player, then plug in the stream
        if (action.equals(JOIN_GAME)) {
            int roomID = obj.getInt("roomID");
            // user is a player already in room
            // redirect io
            if (user.isInRoom(roomID)) {
                //go to the room
                //find that player
                Player currPlayer = rooms.get(roomID).getPlayer(userName);
                //currPlayer.reConnect(user.getIn(), user.getOut());
            }



        }



    }



}
