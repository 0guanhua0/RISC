package edu.duke.ece651.risk.server;

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

    public static Boolean validate(User user, SQL db, JSONObject msg) throws SQLException, IOException, ClassNotFoundException {
        String userName = msg.getString(USER_NAME);
        String userPassword = msg.getString(USER_PASSWORD);
        String action = msg.getString(ACTION);


        if (action.equals(LOGIN)) {
            if (db.authUser(userName, userPassword)) {
                user.send(SUCCESSFUL);
                return true;
            } else {
                user.send(INVALID_LOGIN);
                return false;

            }
        }

        if (action.equals(SIGNUP)) {
            if (db.addUser(userName, userPassword)) {

                user.send(SUCCESSFUL);
                return true;

            } else {
                user.send(INVALID_SIGNUP);
                return false;
            }
        }

        /*
        if (action.equals("update")) {
            return validateUpdate(userName, userPassword);
        }

         */

        user.send(INVALID_VALIDATE);
        return false;


        //todo: add change passsword func
        /*
        if (action.equals("change")) {
            //check db for user id
            //ch password
            if (true) {
                return true;

            } else {
                user.send("invalid change");
            }

        }

         */

        //todo: add logout
    }
    //validate login


}
