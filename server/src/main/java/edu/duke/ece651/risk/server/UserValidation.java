package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.player.Player;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

/**
 * validate usr info with data base
 */


//todo: change to constant file
//TODO: add login, sign up, change password
//check user name & password
//login / sign up / change password
public class UserValidation {
    /**
     *
     */
    public static Boolean validate(Player<?> player, SQL db) throws SQLException, IOException, ClassNotFoundException {

        String msg = (String) player.recv();
        JSONObject obj = new JSONObject(msg);

        String userName = obj.get("userName").toString();
        String userPassword = obj.get("userPassword").toString();
        String action = obj.get("action").toString();


        if (action.equals("login")) {
            if (db.authUser(userName, userPassword)) {
                player.send("SUCCESSFUL");
                return true;
            } else {
                player.send("invalid login");
                return false;

            }
        }

        if (action.equals("signup")) {
            if (db.addUser(userName, userPassword)) {

                player.send("SUCCESSFUL");
                return true;

            } else {
                player.send("invalid signup");
                return false;
            }
        }

        /*
        if (action.equals("update")) {
            return validateUpdate(userName, userPassword);
        }

         */

        player.send("invalid user info");

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
