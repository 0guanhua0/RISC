package edu.duke.ece651.risk.shared.map;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Transient;

import java.io.Serializable;
import java.util.*;

/**
 * @program: risk-Map
 * @description:
 * this is WorldMap class that convert user input of territory name to a real territory object
 * @author: Chengda Wu (cw402)
 * @create: 2020-03-08 20:49
 **/

@Embedded
public abstract class WorldMap<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 14L;

    String name;
    @Transient
    Map<String, Territory> atlas;

    @Transient
    List<T> colorList;
    //key is the set of names of territory, value is there are currently selected or not
    @Transient
    Map<Set<String>,Boolean> groups;

    //morphia constructor
    public WorldMap(){}

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
     * this method could be used to check the legality of a move action
     * note that the distance is defined as minimum path between src and target territory and
     * the whole minimum should under the control of same user
     * when there is no such a path, it will return Integer.MAX_VALUE
     */

    public int getMinCtrlDist(String srcName, String targetName){
        if (!this.atlas.containsKey(srcName)||!this.atlas.containsKey(targetName)){
            return Integer.MAX_VALUE;
        }
        Territory src = this.atlas.get(srcName);
        Territory target = this.atlas.get(targetName);

        int owner = src.getOwner();
        if (owner!=target.getOwner()&&owner!=target.getAllyId()){
            return Integer.MAX_VALUE;
        }

        //the dist it takes from src territory to key
        Map<Territory,Integer> dist = new HashMap<>();
        //mark a node as visited after polling it of pq
        Set<Territory> visited = new HashSet<>();

        dist.put(src,0);//we start from src territory, so it takes 0 to go here

        PriorityQueue<Territory> pq = new PriorityQueue<Territory>(Comparator.comparingInt(dist::get));
        pq.offer(src);
//        System.out.println("Owner id is "+src.getOwner());
        while(!pq.isEmpty()){
            Territory curTerr = pq.poll();
//            System.out.println("curTerr is "+curTerr.getName());
//            System.out.println("target is "+target.getName());
            if (curTerr==target){
//                System.out.println("is equal");
//                System.out.println(dist.get(curTerr));
                return dist.get(curTerr);
            }
            if(!visited.contains(curTerr)){
                visited.add(curTerr);
                int neighDist = curTerr.getSize()+dist.get(curTerr);
                for(Territory neigh:curTerr.getNeigh()){
//                    System.out.println("Terr name is "+neigh.getName());
//                    System.out.println("Terr ally id "+neigh.getAllyId());
                    if (neigh.getOwner()!=owner&&neigh.getAllyId()!=owner){
//                        System.out.println("is ignored terr. ");
                        continue;
                    }
//                    System.out.println("is accepted terr. ");
                    dist.put(neigh,Math.min(dist.getOrDefault(neigh,Integer.MAX_VALUE),neighDist));
                    pq.offer(neigh);
                }
            }
        }
        return Integer.MAX_VALUE;
    }
}
