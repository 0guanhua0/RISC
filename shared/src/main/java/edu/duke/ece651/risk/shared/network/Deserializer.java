package edu.duke.ece651.risk.shared.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * This is the deserializer for server part, which deserialize the json string and return what's needed.
 */
public class Deserializer {

    /**
     * This function takes in a json string representation, and serialize it into map of actions(group by action type).
     * @param jsonStr json string representation
     * @return Map of actions; key is action type, e.g. move; value is list of actions
     */
    public static HashMap<String, List<Action>> deserializeActions(String jsonStr){
        HashMap<String, List<Action>> actions = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonStr);

        actions.put("move",
                new Gson().fromJson(jsonObject.getJSONArray("move").toString(),
                        new TypeToken<ArrayList<MoveAction>>(){}.getType())
        );
        actions.put("attack",
                new Gson().fromJson(jsonObject.getJSONArray("attack").toString(),
                        new TypeToken<ArrayList<AttackAction>>(){}.getType())
        );

        return actions;
    }

    /**
     * This function takes in the result of toJSON() method in WorldMap class.
     * @param jsonStr json string representation for WorldMap
     * @return a WorldMap object
     * @throws ClassNotFoundException probably because of invalid value in type field
     */
    static WorldMap deserializeWorldMap(String jsonStr) throws ClassNotFoundException {
        Map<String, Territory> map = new HashMap<>();
        // serialize the json to map of territory
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray allTerritory = jsonObject.getJSONArray("atlas");
        for (int i = 0; i < allTerritory.length(); i++) {
            JSONObject territory = allTerritory.getJSONObject(i);
            map.put(
                    territory.getString("name"),
                    (Territory)new Gson().fromJson(
                            territory.getString("territory"),
                            Class.forName(territory.getString("type"))
                    )
            );
        }
        // fetch the actual object of neighbours by list of neighbour names
        for (Territory t : map.values()){
            Set<Territory> neighs = new HashSet<>();
            for (String neighName : t.getNeighNames()){
                neighs.add(map.get(neighName));
            }
            t.setNeigh(neighs);
        }
        WorldMap worldMap = new WorldMap();
        worldMap.setAtlas(map);
        return worldMap;
    }
}
