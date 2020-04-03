package edu.duke.ece651.risk.server;

import org.json.JSONObject;

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

    public static Boolean signUp(User user, SQL db, JSONObject msg) throws SQLException, ClassNotFoundException {
        String userName = msg.getString(USER_NAME);
        String userPassword = msg.getString(USER_PASSWORD);


        if (db.addUser(userName, userPassword)) {
            user.send(SUCCESSFUL);
            return true;

        } else {
            user.send(INVALID_SIGNUP);
            return false;
        }
    }


    //validate login

    public static boolean logIn(User user, SQL db, JSONObject msg) throws SQLException, ClassNotFoundException {
        String userName = msg.getString(USER_NAME);
        String userPassword = msg.getString(USER_PASSWORD);

        if (db.authUser(userName, userPassword)) {
            user.send(SUCCESSFUL);
            return true;
        } else {
            user.send(INVALID_LOGIN);
            return false;
        }

    }

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


}
