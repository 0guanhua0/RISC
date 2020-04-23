package edu.duke.ece651.riskclient;

import java.util.HashMap;
import java.util.Map;

public class Constant {
    // the longest waiting time for any network operation
    public static final int TIME_OUT = 30 * 1000;

    // CI server: vcm-12835.vm.duke.edu
    // testing server: vcm-14677.vm.duke.edu
    // localhost: 192.168.1.102
    public static final String HOST = "vcm-14677.vm.duke.edu";
    public static final int PORT = 12345;

    public static final String PASSWORD_NEW = "newPassword";
    public static final String PASSWORD_OLD = "oldPassword";

    public static final String RESULT = "result";

    public static final String ACTION_PERFORMED = "actionPerformed";

    public static final String FAIL_TO_SEND = "Fail to send the data, check your network connection";
    public static final String NETWORK_PROBLEM = "Network problem";

    public static final Map<String, Integer> MAP_NAME_TO_RESOURCE_ID = new HashMap<String, Integer>() {{
        put("a clash of kings", R.drawable.map_clash);
        put("ring", R.drawable.map_ring);
        put("test", R.drawable.map_test);
    }};

}
