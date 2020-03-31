package edu.duke.ece651.risk.shared;

import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.util.List;

/**
 * @program: risk
 * @description: this is state of current game, used in evolution2
 * Since Room is a server side class and cannot be accessed by the client side code, this is
 * what we use to represent the current state of the game to check the legality an action send by player
 * @author: Chengda Wu
 * @create: 2020-03-29 14:58
 **/
public class WorldState {

    // all players in current room
    Player<String> player;

    // the map this room is playing
    WorldMap<String> map;

    public WorldState(Player<String> player, WorldMap<String> map) {
        this.player = player;
        this.map = map;
    }


    public WorldMap<String> getMap() {
        return map;
    }

    public Player<String> getPlayer() {
        return player;
    }

}
