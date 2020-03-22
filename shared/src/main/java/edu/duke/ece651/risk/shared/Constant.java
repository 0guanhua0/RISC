package edu.duke.ece651.risk.shared;

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

    public static final String SELECT_MAP_ERROR = "The map name you select is invalid";
    public static final String SELECT_TERR_ERROR = "The territories you select is invalid";
    public static final String INVALID_ACTION = "Your action is invalid";
    public static final String YOU_WINS = "Congratulation, you win!!!";

    public static final int UNITS_PER_TERR = 5;



}
