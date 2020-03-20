package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Display {
    public String showMap(Map<String, Set<String>> terr);
}

class Display0 implements Display {
    public String showMap(Map<String, Set<String>> terr) {
        StringBuilder sb = new StringBuilder();
       for (Map.Entry<String, Set<String>> entry : terr.entrySet()) {
            String territoryName = entry.getKey();
           TerritoryV1 numOfUnits = new TerritoryV1(territoryName);
//how to get the units?
           numOfUnits.addNUnits(45);
           sb.append(numOfUnits.getUnitsNum());
           sb.append(" units in ");
           sb.append(territoryName);
            sb.append("(next to: ");
            Set<String> neighNames = terr.get(territoryName);
            for (String neighName : neighNames){
                sb.append(neighName);
                sb.append(", ");
            }
            sb.append(")\n");
        }
      //  System.out.println("sb = "+sb);
        return sb.toString();
    }
}

