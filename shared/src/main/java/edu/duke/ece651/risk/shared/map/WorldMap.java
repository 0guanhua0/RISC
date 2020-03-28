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

public abstract class WorldMap<T extends Serializable> implements Serializable {
    String name;
    Map<String, Territory> atlas;
    List<T> colorList;
    //key is the set of names of territory, value is there are currently selected or not
    Map<Set<String>,Boolean> groups;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayerNums(){
        return colorList.size();
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

    /**
     *
     * @param srcName: name of the source territory
     * @param targetName: name of the target territory
     * @return the distance between this two territories
     * could be used to check the legality of a move action
     */

    public int getDist(String srcName, String targetName) {
        if (!this.atlas.containsKey(srcName)||!this.atlas.containsKey(targetName)){
            throw new IllegalArgumentException("invalid input territories name");
        }
        Territory src = this.atlas.get(srcName);
        Territory target = this.atlas.get(targetName);

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
        throw new IllegalStateException("The design of the map is illegal");
    }
}
