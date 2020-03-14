package edu.duke.ece651.risk.client;

/**
 * store player info
 */
public class Player{
    private Integer playerId;
    private String playerName;


    public Player(Integer playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Integer getPlayerId() {
        return playerId;
    }

}
