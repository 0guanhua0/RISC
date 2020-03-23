package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.Utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @program: risk-map
 * @description: This is database which includes all maps we create
 * @author: Chengda Wu(cw402)
 * @create: 2020-03-09 10:20
 **/

// TODO: do we really need T here? since we hard code the map, we already know the type
    //TODO put the name of map inside constructor
public class MapDataBase<T extends Serializable> implements Serializable{
    Map<String, WorldMap<T>> mapHub;
    private static final String baseDirStr = "config_file/MapDB_config/";


    public MapDataBase() throws IOException {
        mapHub = new HashMap<>();
        File baseDir = new File(baseDirStr);
        for (File subDir : baseDir.listFiles()) {
            String mapName = subDir.getName();

            String colorPath = Paths.get(baseDirStr,mapName, "color.txt").toString();
            String groupPath = Paths.get(baseDirStr,mapName, "group.txt").toString();
            String neighPath = Paths.get(baseDirStr,mapName, "neigh.txt").toString();

            Map<String,Set<String>> atlas1 = Utils.readNeighConfig(neighPath);
            List<String> colorList = Utils.readColorConfig(colorPath);
            Map<Set<String>,Boolean> groups = Utils.readGroupConfig(groupPath);

            WorldMap<T> worldMap = new WorldMap(atlas1,colorList,groups);
            worldMap.setName(mapName);
            mapHub.put(mapName,worldMap);
        }


    }
    public boolean containsMap(String inputName){
        if (null==inputName){
            return false;
        }else{
            String mapName = inputName.toLowerCase();
            return mapHub.containsKey(mapName);
        }
    }
    public WorldMap<T> getMap(String inputName){
        if (!containsMap(inputName)){
            throw new IllegalArgumentException("Input map name doesn't exist!");
        }
        String mapName = inputName.toLowerCase();
        return mapHub.get(mapName);
    }
    public Map<String, WorldMap<T>> getAllMaps(){
        return mapHub;
    }
}
