package edu.duke.ece651.risk.server;

import com.google.gson.Gson;

public class UserValidation {
    /**
     * recv string and convert to json obj
     */
    public Boolean validate(Gson gsonObj, SQL db) {
        String userName = gsonObj.fromJson("userName", String.class);
        String userPassword = gsonObj.fromJson("userPassword", String.class);
        String action = gsonObj.fromJson("action", String.class);

        if (action.equals("login")) {
            return db.authUser(userName, userPassword);
        }

        if (action.equals("signup")) {
            return db.addUser(userName,userPassword);
        }

        /*
        if (action.equals("update")) {
            return validateUpdate(userName, userPassword);
        }

         */

        return false;

    }


}
