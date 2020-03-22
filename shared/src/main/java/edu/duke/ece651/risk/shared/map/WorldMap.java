package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;
import java.util.*;

/**
 * @program: risk-Map
 * @description:
 * this is WorldMap class that convert user input of territory name to a real territory object
 * @author: Chengda Wu (cw402)
 * @create: 2020-03-08 20:49
 **/

//TODO take generic and serializable into consideration
public class WorldMap<T extends Serializable> implements Serializable {
    String name;
    Map<String, Territory> atlas;
    List<T> colorList;
    public WorldMap(){
        this.atlas = new HashMap<>();
        this.colorList = new ArrayList<>();
    }
    public WorldMap(Map<String,Set<String>> adjaList, List<T> colorList){
        int playerNum = colorList.size();
        int terriNum = adjaList.size();
        if (playerNum>terriNum){
            throw new IllegalArgumentException("The number of players can't be larger than the number of territories");
        }else if(0!=terriNum%playerNum){
            throw new IllegalArgumentException("This is unfair to the last player!");
        }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    // TODO: have better implement equals here
}
