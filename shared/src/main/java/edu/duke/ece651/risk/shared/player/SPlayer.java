package edu.duke.ece651.risk.shared.player;

import java.io.Serializable;

/**
 * This is a simple player class to avoid primitive obsession.
 * In each game, we need to send all player info(only id & name needed) to the client, for alliance & chat.
 */
public class SPlayer implements Serializable {
    private static final long serialVersionUID = 20L;

    private int id;
    private String name;

    public SPlayer(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
