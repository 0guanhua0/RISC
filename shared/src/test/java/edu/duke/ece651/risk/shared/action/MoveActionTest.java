package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class MoveActionTest {

    @Test
    void isValid() {
        MapDataBase mapDataBase = new MapDataBase();
        //start the game
        WorldMap worldMap = mapDataBase.getMap("a clash of kings");
        //two players join this game
        Player<String> p1 = new PlayerV1<>("Red",1);
        PlayerV1<String> p2 = new PlayerV1<>("Blue", 2);
        //assign some territories and some units to each player
        //for each player, 10 units in total
    }

    @Test
    void perform() {
    }
}