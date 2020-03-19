package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.map.WorldMap;

import java.util.List;
import java.util.Set;

/**
 * show map
 */
class SceneCLI implements Scene {

    public static void showMap(WorldMap<String> map) {
        System.out.println("showing example map");
        // TODO: actually implement the display here
        System.out.println("10 units in Narnia (next to: Elantris, Midkemia)");
    }

    public static void showTerritoryGroup(List<Set<String>> territories){
        System.out.println("Here is all territory groups you can choose");
        for (int i = 0; i < territories.size(); i++){
            System.out.println(String.format("%d. group%d", i + 1, i + 1));
            for (String name : territories.get(i)){
                System.out.println(name);
            }
        }
    }
}