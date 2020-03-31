package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.player.Player;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

import static edu.duke.ece651.risk.shared.Constant.*;

/**
 * validate usr info with data base
 */

//login / sign up / change password
public class UserValidation {
    /**
     *
     */

    public static Boolean validate(Player<?> player, SQL db) throws SQLException, IOException, ClassNotFoundException {

        String msg = (String) player.recv();
        JSONObject obj = new JSONObject(msg);

        String userName = obj.getString(USER_NAME);
        String userPassword = obj.getString(USER_PASSWORD);
        String action = obj.getString(ACTION);


        if (action.equals(LOGIN)) {
            if (db.authUser(userName, userPassword)) {
                player.send(SUCCESSFUL);
                return true;
            } else {
                player.send(INVALID_LOGIN);
                return false;

            }
        }

        if (action.equals(SIGNUP)) {
            if (db.addUser(userName, userPassword)) {

                player.send(SUCCESSFUL);
                return true;

            } else {
                player.send(INVALID_SIGNUP);
                return false;
            }
        }

        /*
        if (action.equals("update")) {
            return validateUpdate(userName, userPassword);
        }

         */

        player.send(INVALID_VALIDATE);
        return false;


        //todo: add change passsword func
        /*
        if (action.equals("change")) {
            //check db for user id
            //ch password
            if (true) {
                return true;

            } else {
                player.send("invalid change");
            }

        }

         */

        //todo: add logout
    }
    //validate login


}
