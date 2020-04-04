package edu.duke.ece651.riskclient;

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
    public static final String ACTION_TYPE = "action";
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_SIGN_UP = "signup";
    public static final String ACTION_CHANGE_PASSWORD = "changePassword";
    public static final String ACTION_GET_IN_ROOM = "getInRoom";
    public static final String ACTION_GET_WAIT_ROOM = "getWaitRoom";

    public static final String RESULT = "result";
    public static final String SUCCESSFUL = "successful";

}
