package edu.duke.ece651.riskclient;

import java.util.HashMap;
import java.util.Map;

public class Constant {
    // the longest waiting time for any network operation
    public static final int TIME_OUT = 30 * 1000;

    // CI server: vcm-12835.vm.duke.edu
    // testing server: vcm-13663.vm.duke.edu
    public static final String HOST = "vcm-13663.vm.duke.edu";
    public static final int PORT = 12345;

    public static final String USER_NAME = "userName";
    public static final String USER_PASSWORD = "userPassword";
    public static final String PASSWORD_NEW = "newPassword";
    public static final String PASSWORD_OLD = "oldPassword";
    public static final String ROOM_NAME = "roomName";
    public static final String MAP_NAME = "mapName";

    public static final String ACTION_TYPE = "action";
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_SIGN_UP = "signup";
    public static final String ACTION_CHANGE_PASSWORD = "changePassword";
    public static final String ACTION_CREATE_NEW_ROOM = "createNewRoom";
    public static final String ACTION_GET_IN_ROOM = "getInRoom";
    public static final String ACTION_GET_WAIT_ROOM = "getWaitRoom";

    public static final String RESULT = "result";
    public static final String SUCCESSFUL = "successful";

    public static final String ACTION_PERFORMED = "actionPerformed";

    public static final String FAIL_TO_SEND = "Fail to send the data, check your network connection";
    public static final String NETWORK_PROBLEM = "Network problem";

    public static final Map<String, Integer> MAP_NAME_TO_RESOURCE_ID = new HashMap<String, Integer>() {{
        put("t1", R.drawable.risk_img);
    }};

}
