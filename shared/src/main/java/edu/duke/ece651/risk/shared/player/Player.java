package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.map.Territory;

import java.util.Set;

/**
 * @program: risk
 * @description: this is the abstract player class
 * note that a valid user id should be positive, 0 is for unoccupied id
 * @author: Chengda Wu(cw402)
 * @create: 2020-03-09 16:24
 **/

public abstract class Player<T> {
    T color;
    int id;
    Set<Territory> territories;

    public void addTerritory(Territory territory){
        territories.add(territory);
        territory.setOwner(this.id);
    }

    public void loseTerritory(Territory territory){
        if(!territories.contains(territory)){
            throw new IllegalArgumentException("there territory doesn't belong to this uesr!");
        }
        territories.remove(territory);
        territory.setIsFree(true);
    }
}
