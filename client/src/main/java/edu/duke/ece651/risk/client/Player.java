package edu.duke.ece651.risk.client;

import org.json.JSONObject;

import static edu.duke.ece651.risk.shared.Constant.PLAYER_COLOR;
import static edu.duke.ece651.risk.shared.Constant.PLAYER_ID;

/**
 * store player info
 */
public class Player<T>{
    int playerId;
    String playerName;
    T playerColor;

    public Player(){
        playerId = 0;
        playerName = "";
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerID(){
        return playerId;
    }

    public void init(String json){
        JSONObject jsonObject = new JSONObject(json);
        this.playerId = jsonObject.getInt(PLAYER_ID);
        this.playerColor = (T) jsonObject.get(PLAYER_COLOR);
    }
}
