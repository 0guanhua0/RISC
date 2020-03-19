package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.map.Territory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Display {
    public void showMap(Map<String, Set<String>> terr);
}

class Display0 implements Display {
    public void showMap(Map<String, Set<String>> terr) {
        for (Map.Entry<String, Set<String>> entry : terr.entrySet()) {
            String territoryName = entry.getKey();
            Set<String> neighNames = terr.get(territoryName);
            for (String neighName : neighNames){
            }
            System.out.println("10 units in "+territoryName+"(next to: "+neighNames+")");
        }
    }
}
 //Task1 :Need to make a function to print the different adjacent territories

//Task2 : get the number of units