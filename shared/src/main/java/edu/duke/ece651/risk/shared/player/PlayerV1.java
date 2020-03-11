package edu.duke.ece651.risk.shared.player;

import java.util.HashSet;

/**
 * @program: risk
 * @description: this is player class for the first version of game
 * @author: Chengda Wu(cw402)
 * @create: 2020-03-09 17:42
 **/
public class PlayerV1<T> extends Player<T> {

    public PlayerV1(T color, int id) {
        if (id<=0){
            throw new IllegalArgumentException("Id must be positive");
        }
        this.color = color;
        this.id = id;
        this.territories = new HashSet<>();
    }
}
