package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;
import java.util.*;
/**
 * @program: risk
 * @description: this is the map class for evolution1
 * @author: Mr.Wang
 * @create: 2020-03-28 15:00
 **/
public class WorldMapV1<T extends Serializable> extends WorldMap{
    public WorldMapV1(Map<String, Set<String>> adjaList, List<T> colorList, Map<Set<String>, Boolean> groups) throws IllegalArgumentException {

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

        assert(playerNum<=terriNum);
        assert(0==terriNum%playerNum);

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
            Territory curTerri = (TerritoryV1)atlas.get(terriName);
            Set<String> neighNames = adjaList.get(terriName);
            Set<Territory> neigh = new HashSet<>();
            for (String neighName : neighNames) {
                neigh.add((TerritoryV1)atlas.get(neighName));
            }
            curTerri.setNeigh(neigh);
        }
    }

}
