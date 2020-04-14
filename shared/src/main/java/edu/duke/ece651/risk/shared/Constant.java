package edu.duke.ece651.risk.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * This class stores some common constant value which will be used by both client and server side.
 * (e.g. the "key" value of serialization and deserialization)
 */
public class Constant {
    public static final String ACTION_MOVE = "moveAction";
    public static final String ACTION_ATTACK = "attackAction";
    public static final String ACTION_DONE = "doneAction";

    public static final String SUCCESSFUL = "successful";
    public static final String GAME_OVER = "gameOver";
    public static final String ROUND_OVER = "roundOver";

    public static final String PLAYER_ID = "playerID";
    public static final String PLAYER_COLOR = "playerColor";

    public static final String SELECT_GROUP_ERROR = "This group is chosen by other players.";

    public static final String SELECT_MAP_ERROR = "The map name you select is invalid.";
    public static final String SELECT_TERR_ERROR = "The territories you select is invalid.";


    public static final String INVALID_ACTION = "Your action is invalid.";
    public static final String YOU_WINS = "Congratulation, you win!!!";

    public static final int UNITS_PER_TERR = 5;

    public static final int INITIAL_FOOD_NUM = 30;
    public static final int INITIAL_TECH_NUM = 50;

    //map info
    public static final String MAP_0 = "a clash of kings";


    //for networking info
    public static final String ROOM_ID = "roomID";

    //JSON communicate format
    public static final String USER_NAME = "userName";
    public static final String USER_PASSWORD = "userPassword";
    public static final String ACTION = "action";
    public static final String SIGNUP = "signup";
    public static final String LOGIN = "login";


    //constant for info send to client
    public static final String INVALID_LOGIN = "invalid name/password";
    public static final String INVALID_SIGNUP = "user name already exists";
    public static final String INVALID_VALIDATION_ACTION = "invalid validation action";
    public static final String VALIDATE_SUCCESSFUL = "SUCCESSFUL";
    public static final String INVALID_USER = "invalid user / not login";
    public static final String INVALID_RECONNECT = "the room ID you want to reconnect is invalid";


    public static final String VALID_RECONNECT = "welcome back to game";


    //player disconnect default input
    public static final String DISCONNECT_INPUT = "d";



    // client requiring gaming info
    public static final String ACTION_GET_WAIT_ROOM = "getWaitRoom";
    public static final String ACTION_GET_IN_ROOM = "getInRoom";
    public static final String ACTION_JOIN_GAME = "joinGame";
    public static final String ACTION_CREATE_GAME = "createGame";
    public static final String ACTION_RECONNECT_ROOM = "reconnect room";
    public static final String ACTION_CONNECT_CHAT = "connect chat";

    public static final String ROOM_NAME = "roomName";
    public static final String MAP_NAME = "mapName";
    public static final String INFO_ALL_PLAYER = "allPlayer";


    // the time other player will wait for any player which is disconnected
    // i.e. a player disconnect but don't reconnect in 60s, his/her round will be forced finished
    public static final int WAIT_TIME_OUT = 60;


    // the key is current technical level, value is the cost of tech resources to upgrade maximum tech level to the next technical level
    public static final Map<Integer, Integer> TECH_MAP = new HashMap<Integer, Integer>() {
        {
            put(1, 50);
            put(2, 75);
            put(3, 125);
            put(4, 200);
            put(5, 300);
        }
    };
    // key is the level of units, value is the name of corresponding units
    public static final Map<Integer, String> UNIT_NAME = new HashMap<Integer, String>() {{
        put(0, "Light Infantry");
        put(1, "Infantry");
        put(2, "Hussar");
        put(3, "Cavalry");
        put(4, "Heavy Cavalry");
        put(5, "Elite Cavalry");
        put(6, "Cavalry Commander");
    }};

    //key is the target level of a unit
    //value is the cost of tech resources to upgrade this unit from level 0 to level specified in key
    public static final Map<Integer,Integer> UP_UNIT_COST = new HashMap<Integer,Integer>(){
        {
            put(0,0);
            put(1,3);
            put(2,11);
            put(3,30);
            put(4,55);
            put(5,90);
            put(6,140);
        }
    };
    //key is the current level of units, value is the extra bonus for a fight
    public static final Map<Integer, Integer> UNIT_BONUS = new HashMap<Integer, Integer>() {
        {
            put(0, 0);
            put(1, 1);
            put(2, 3);
            put(3, 5);
            put(4, 8);
            put(5, 11);
            put(6, 15);
        }
    };
}
