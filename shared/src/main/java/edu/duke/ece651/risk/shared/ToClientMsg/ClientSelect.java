package edu.duke.ece651.risk.shared.ToClientMsg;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
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

    public ClientSelect(int unitsTotal, int terrPerUser, String mapName) throws IOException {
        this.unitsTotal = unitsTotal;
        this.map = new MapDataBase<String>().getMap(mapName);
//        this.map = map;
    }

    public int getUnitsTotal() {
        return unitsTotal;
    }

    public WorldMap<String> getMap() {
        return map;
    }

    //only return groups whose value is false
    public List<Set<String>> getGroups() {
        List<Set<String>> groups = new ArrayList<>();
        for (Map.Entry<Set<String>, Boolean> entry : map.getGroups().entrySet()) {
            //if (!entry.getValue()){
            groups.add(entry.getKey());
            //}
        }
        return groups;
    }


//    void generateGroups(int terrPerUser){
//        Map<String, Territory> territories = map.getAtlas();
//
//        int groupSize = territories.size() / terrPerUser;
//        groups = new ArrayList<>(groupSize);
//        for (int i = 0; i < groupSize; i++){
//            groups.add(new HashSet<>());
//        }
//
//        int i = 0;
//        int groupCnt = groups.size();
//
//        for (String name : territories.keySet()){
//            groups.get(i % groupCnt).add(name);
//            i++;
//        }
//        System.out.println(groups);
//    }
}
