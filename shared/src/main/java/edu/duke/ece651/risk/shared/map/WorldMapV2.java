package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;
import java.util.*;

/**
 * @program: risk
 * @description: this is WorldMap for evolution2 of game
 * @author: Chengda Wu
 * @create: 2020-03-28 14:10
 **/
public class WorldMapV2<T extends Serializable> extends WorldMap{
    public WorldMapV2(Map<String, Set<String>> adjaList, List<T> colorList,
                      Map<Set<String>, Boolean> groups,Map<String,Integer> sizes){

        //check legality of groups
        Set<String> allName = new HashSet<>();
        for (Set<String> nameSet : groups.keySet()) {
            for (String name : nameSet) {
                assert (adjaList.containsKey(name) || !allName.contains(name));
                allName.add(name);
            }
            assert(groups.get(nameSet)==false);
        }
        assert (allName.size() == adjaList.size());
        this.groups = groups;

        int playerNum = colorList.size();
        int terriNum = adjaList.size();

        assert(playerNum<=terriNum);
        assert(0==terriNum%playerNum);

        this.colorList = colorList;
        this.atlas = new HashMap<String,Territory>();
        //initialize each single territory
        for (Map.Entry<String, Set<String>> entry : adjaList.entrySet()) {
            String terriName = entry.getKey();
            int size = sizes.get(terriName);
            Territory territory = new TerritoryV2(terriName,size);
            atlas.put(terriName,territory);
        }

        //connect them to each other
        for (Map.Entry<String, Set<String>> entry : adjaList.entrySet()) {
            String terriName = entry.getKey();
            Territory curTerri = (TerritoryV2) this.atlas.get(terriName);
            Set<String> neighNames = adjaList.get(terriName);
            Set<Territory> neigh = new HashSet<>();
            for (String neighName : neighNames) {
                neigh.add((TerritoryV2)atlas.get(neighName));
            }
            curTerri.setNeigh(neigh);
        }
    }

}
