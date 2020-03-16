package edu.duke.ece651.risk.client;

/**
 * store player info
 */
public class Player{
    private Integer playerId;
    private String playerName;

    public Player(){
        playerId = 0;
        playerName = "";
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Integer getPlayerId() {
        return playerId;
    }

}
