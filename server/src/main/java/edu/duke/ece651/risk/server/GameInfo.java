package edu.duke.ece651.risk.server;

import org.mongodb.morphia.annotations.Embedded;

import java.util.HashMap;
import java.util.Map;

@Embedded
public class GameInfo {
    int winnerID;
    int roundNum;
    // the mapping between player id and player name(for now, use the color)
    Map<Integer, String> idToName;

    public GameInfo(int winnerID, int roundNum) {
        this.winnerID = winnerID;
        this.roundNum = roundNum;
        this.idToName = new HashMap<>();
    }

    public int getWinnerID() {
        return winnerID;
    }

    public int getRoundNum(){
        return roundNum;
    }

    public Map<Integer, String> getIdToName() {
        return idToName;
    }

    public void setWinner(int id){
        if (!idToName.containsKey(id)){
            throw new IllegalArgumentException("Player doesn't exist.");
        }
        winnerID = id;
    }

    public boolean hasFinished() {
        return winnerID != -1;
    }

    public void nextRound(){
        roundNum++;
    }

    public void addPlayer(int id, String name){
        idToName.put(id, name);
    }

    public String getWinnerName() {
        return idToName.get(winnerID);
    }
}
