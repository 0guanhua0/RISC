package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.util.*;

/**
 * show map
 */
class SceneCLI implements Scene {

    /**
     * Simply show the map(only contains the structure info)
     * This function should only be called during the territory selection.
     * @param map WorldMap object
     */
    public static void showMap(WorldMap<String> map) {
        for (Territory territory : map.getAtlas().values()){
            showTerritory(territory);
        }
    }

    /**
     * Show the map, but group territories by player, also show the map in group.
     * @param map WorldMao object
     * @param idToColor the mapping between player id and player color
     */
    public static void showMap(WorldMap<String> map, Map<Integer, String> idToColor) {
        // group the territory by owner id
        Map<Integer, List<Territory>> playerTerritory = new HashMap<>();
        playerTerritory.put(0, new ArrayList<>());
        // initialize the territory group
        for (Integer id : idToColor.keySet()){
            playerTerritory.put(id, new ArrayList<>());
        }
        // group territories
        for (Territory territory : map.getAtlas().values()){
            playerTerritory.get(territory.getOwner()).add(territory);
        }

        // show free territory(useless in evolution 1)
        if (!playerTerritory.get(0).isEmpty()){
            System.out.println("free territory:");
            System.out.println("--".repeat(8));
            for (Territory t : playerTerritory.get(0)){
                showTerritory(t);
            }
            System.out.println();
        }

        for (Map.Entry<Integer, List<Territory>> entry : playerTerritory.entrySet()){
            if (entry.getKey() != 0){
                System.out.println(String.format("%s player:", idToColor.get(entry.getKey())));
                System.out.println("--".repeat(8));
                for (Territory t : entry.getValue()){
                    showTerritory(t);
                }
                System.out.println();
            }
        }
    }

    /**
     * Show territories in group.
     * @param territories territories in group
     */
    public static void showTerritoryGroup(List<Set<String>> territories){
        System.out.println("Here is all territory groups you can choose");
        for (int i = 0; i < territories.size(); i++){
            System.out.println(String.format("%d. group%d", i + 1, i + 1));
            for (String name : territories.get(i)){
                System.out.println(name);
            }
        }
    }

    /**
     * This function will show the info of a territory, int the format
     * e.g. <units> units in <territory_name> (next to: <neigh_names>)
     * @param territory Territory object
     */
    static void showTerritory(Territory territory){
        int unitNum = territory.getBasicUnitsNum();
        String name = territory.getName();
        List<String> neighNames = new ArrayList<>();
        for (Territory neigh : territory.getNeigh()){
            neighNames.add(neigh.getName());
        }

        StringBuilder str =
                new StringBuilder(
                        String.format("%d units in %s (next to: ", unitNum, name)
                );

        for (String n : neighNames){
            str.append(n);
            str.append(", ");
        }
        str.delete(str.length()-2, str.length());
        str.append(")");
        System.out.println(str);
    }
}