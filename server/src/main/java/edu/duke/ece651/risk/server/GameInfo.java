package edu.duke.ece651.risk.server;

public class GameInfo {
    int winnerID;
    int roundNum;

    public GameInfo(int winnerID, int roundNum) {
        this.winnerID = winnerID;
        this.roundNum = roundNum;
    }

    public boolean hasFinished() {
        return winnerID != -1;
    }

    public void nextRound(){
        roundNum++;
    }

    public int getRoundNum(){
        return roundNum;
    }

    public void setWinner(int id){
        winnerID = id;
    }
}
