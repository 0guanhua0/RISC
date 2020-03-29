package edu.duke.ece651.risk.server;

import com.google.gson.Gson;

public class UserValidation {
    /**
     * recv string and convert to json obj
     */
    public Boolean validate(Gson gsonObj) {
        String userName = gsonObj.fromJson("userName", String.class);
        String userPassword = gsonObj.fromJson("userPassword", String.class);
        String action = gsonObj.fromJson("action", String.class);

        if (action.equals("login")) {
            return validateLogin(userName, userPassword);
        }

        if (action.equals("signup")) {
            return validateSignup(userName, userPassword);
        }

        if (action.equals("update")) {
            return validateUpdate(userName, userPassword);
        }

        return false;

    }

    /**
     * parse json obj
     */

    /**
     * validate login
     */

    public Boolean validateLogin(String userName, String userPassword) {
        //check database

        //
        return false;
    }

    /**
     * validate create
     */
    public Boolean validateSignup(String userName, String userPassword) {
        //check database

        //
        return false;
    }

    /**
     * validate update
     */
    public Boolean validateUpdate(String userName, String userPassword) {
        //check database

        //
        return false;
    }
}
