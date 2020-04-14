package edu.duke.ece651.risk.shared;

import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import java.util.ArrayList;
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

    //Player for corresponding action
    Player<String> myPlayer;

    // the map this room is playing
    WorldMap<String> map;

    // all players in current room
    List<Player<String>> players;

    public WorldState(Player<String> myPlayer, WorldMap<String> map) {
        this.myPlayer = myPlayer;
        this.map = map;
        this.players = new ArrayList<>();
    }

    public WorldState(Player<String> myPlayer, WorldMap<String> map, List<Player<String>> players) {
        this.myPlayer = myPlayer;
        this.map = map;
        this.players = players;
    }

    public WorldMap<String> getMap() {
        return map;
    }

    public Player<String> getMyPlayer() {
        return myPlayer;
    }

    public  List<Player<String>>  getPlayers() {
        return players;
    }

}
