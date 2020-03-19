package edu.duke.ece651.risk.shared.ToClientMsg;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.io.Serializable;
import java.util.*;

/**
 * @program: risk
 * @description: this class has all fields that server want client to know to select territories
 * @author: Chengda Wu(cw402)
 * @create: 2020-03-16 17:17
 **/
public class ClientSelect implements Serializable {
    int unitsTotal;
    WorldMap<String> map;
    List<Set<String>> groups;

    public ClientSelect(int unitsTotal, int terrPerUser, WorldMap<String> map){
        this.unitsTotal = unitsTotal;
        this.map = map;
        generateGroups(terrPerUser);
    }

    public int getUnitsTotal() {
        return unitsTotal;
    }

    public WorldMap<String> getMap() {
        return map;
    }

    public List<Set<String>> getGroups() {
        return groups;
    }

    // TODO: maybe we can put this function into MapDataBase(hardcode the group)
    void generateGroups(int terrPerUser){
        Map<String, Territory> territories = map.getAtlas();

        int groupSize = territories.size() / terrPerUser;
        groups = new ArrayList<>(groupSize);
        for (int i = 0; i < groupSize; i++){
            groups.add(new HashSet<>());
        }

        int i = 0;
        int groupCnt = groups.size();
        for (String name : territories.keySet()){
            groups.get(i % groupCnt).add(name);
            i++;
        }
        System.out.println(groups);
    }
}
