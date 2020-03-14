package edu.duke.ece651.risk.shared.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static edu.duke.ece651.risk.shared.Constant.ACTION_ATTACK;
import static edu.duke.ece651.risk.shared.Constant.ACTION_MOVE;

/**
 * This is the deserializer for server part, which deserialize the json string and return what's needed.
 */
public class Deserializer {

    /**
     * This function takes in a json string representation, and serialize it into map of actions(group by action type).
     * @param jsonStr json string representation
     * @return Map of actions; key is action type, e.g. move; value is list of actions
     */
    static HashMap<String, List<Action>> deserializeActions(String jsonStr){
        HashMap<String, List<Action>> actions = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonStr);

        actions.put(ACTION_MOVE,
                new Gson().fromJson(jsonObject.getJSONArray(ACTION_MOVE).toString(),
                        new TypeToken<ArrayList<MoveAction>>(){}.getType())
        );
        actions.put(ACTION_ATTACK,
                new Gson().fromJson(jsonObject.getJSONArray(ACTION_ATTACK).toString(),
                        new TypeToken<ArrayList<AttackAction>>(){}.getType())
        );

        return actions;
    }
}
