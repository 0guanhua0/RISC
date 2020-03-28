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

public class WorldMap<T extends Serializable> implements Serializable {
    String name;
    Map<String, Territory> atlas;
    List<T> colorList;
    //key is the set of names of territory, value is there are currently selected or not
    Map<Set<String>,Boolean> groups;
    public WorldMap(){
        this.atlas = new HashMap<>();
        this.colorList = new ArrayList<>();
    }

    public WorldMap(Map<String, Set<String>> adjaList, List<T> colorList, Map<Set<String>, Boolean> groups) throws IllegalArgumentException {

        //check legality of groups
        Set<String> allName = new HashSet<>();
        for (Set<String> nameSet : groups.keySet()) {
            for (String name : nameSet) {
                assert (adjaList.containsKey(name) || !allName.contains(name));
                allName.add(name);
            }
        }
        assert (allName.size() == adjaList.size());
        this.groups = groups;

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

    public int getPlayerNums(){
        return colorList.size();
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

    public int getTerrPerPlayer(){
        return atlas.size() / colorList.size();
    }

    //if there is no territory with such name or this territory is currently occupied,return false
    public boolean hasFreeTerritory(String input){
        String name = input.toLowerCase();
        return atlas.containsKey(name) && atlas.get(name).isFree();
    }

    public Map<Set<String>, Boolean> getGroups() {
        return groups;
    }


    public Boolean hasFreeGroup(Set<String> names){
        return this.groups.containsKey(names) && (!this.groups.get(names));
    }
    public void useGroup(Set<String> name){
        this.groups.replace(name,true);
    }


    public int getDist(Territory src, Territory target) {

        //the dist it takes from src territory to key
        Map<Territory,Integer> dist = new HashMap<>();
        //mark a node as visited after polling it of pq
        Set<Territory> visited = new HashSet<>();

        dist.put(src,0);//we start from src territory, so it takes 0 to go here

        PriorityQueue<Territory> pq = new PriorityQueue<Territory>(Comparator.comparingInt(dist::get));
        pq.offer(src);

        while(!pq.isEmpty()){
            Territory curTerr = pq.poll();
            if (curTerr==target){
                return dist.get(curTerr);
            }
            if(!visited.contains(curTerr)){
                visited.add(curTerr);
                int neighDist = curTerr.getSize()+dist.get(curTerr);
                for(Territory neigh:curTerr.getNeigh()){
                    dist.put(neigh,Math.min(dist.getOrDefault(neigh,Integer.MAX_VALUE),neighDist));
                    pq.offer(neigh);
                }
            }
        }
        return Integer.MAX_VALUE;
    }
}
