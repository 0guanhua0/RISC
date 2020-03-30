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

    public static final String SELECT_GROUP_ERROR = "The group you choose is either be chosen or invalid.";

    public static final String SELECT_MAP_ERROR = "The map name you select is invalid.";
    public static final String SELECT_TERR_ERROR = "The territories you select is invalid.";


    public static final String INVALID_ACTION = "Your action is invalid.";
    public static final String YOU_WINS = "Congratulation, you win!!!";

    public static final int UNITS_PER_TERR = 5;

    public static final int INITIAL_FOOD_NUM = 30;
    public static final int INITIAL_TECH_NUM = 50;

    public static final Map<Integer,Integer> TECH_MAP = new HashMap<>(){
        {
            put(1,50);
            put(2,75);
            put(3,125);
            put(4,200);
            put(5,300);
        }
    };


}
