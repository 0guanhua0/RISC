package edu.duke.ece651.risk.shared.map;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;

/**
 * @program: risk-Map
 * @description:
 * this is WorldMap class that convert user input of territory name to a real territory object
 * @author: Chengda Wu (cw402)
 * @create: 2020-03-08 20:49
 **/
public class WorldMap<T> implements Serializable {
    Map<String, Territory> atlas;
    List<T> colorList;
    public WorldMap(){
        this.atlas = new HashMap<>();
        this.colorList = new ArrayList<>();
    }
    public WorldMap(Map<String,Set<String>> adjaList,List<T> colorList){
        this.colorList = colorList;
        atlas = new HashMap<>();
        //initialize each single territory
        for (Map.Entry<String, Set<String>> entry : adjaList.entrySet()) {
            String terriName = entry.getKey();
            Territory territory = new TerritoryV1(terriName);
            atlas.put(terriName,territory);
        }
        //connect them to each other
        for (Map.Entry<String, Set<String>> entry : adjaList.entrySet()) {
            String terriName = entry.getKey();
            Territory curTerri = atlas.get(terriName);
            Set<String> neighNames = adjaList.get(terriName);
            Set<Territory> neigh = new HashSet<>();
            for (String neighName : neighNames) {
                neigh.add(atlas.get(neighName));
            }
            curTerri.setNeigh(neigh);
        }
    }
    public void setAtlas(Map<String, Territory> map){
        this.atlas = map;
    }
    public void setColorList(List<T> colorList) {
        this.colorList = colorList;
    }
    public List<T> getColorList() {
        return colorList;
    }
    public Map<String, Territory> getAtlas() {
        return atlas;
    }

    public boolean hasTerritory(String input){
        String name = input.toLowerCase();
        return atlas.containsKey(name);
    }

    public Territory getTerritory(String input){
        if (!hasTerritory(input)){
            throw new IllegalArgumentException("No such territory inside the map");
        }
        String name = input.toLowerCase();
        return atlas.get(name);
    }

    public int getTerriNum(){
        return atlas.size();
    }
    //if there is no territory with such name or this territory is currently occupied,return false
    public boolean hasFreeTerritory(String input){
        String name = input.toLowerCase();
        return atlas.containsKey(name) && atlas.get(name).isFree();
    }

    public String toJSON(){
        JSONObject jsonObject = new JSONObject();
        // serialize all territory into a json array
        JSONArray territories = new JSONArray();
        for (String key : atlas.keySet()){
            JSONObject tmp = new JSONObject();
            Territory territory = atlas.get(key);

            tmp.put("territory", new Gson().toJson(territory));
            tmp.put("name", territory.getName());
            // this is used for deserialization
            tmp.put("type", territory.getClass().getName());

            territories.put(tmp);
        }
        jsonObject.put("atlas", territories);
        return jsonObject.toString();
    }

    // TODO: have better implement equals here
}
