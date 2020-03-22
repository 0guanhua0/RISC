package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.map.Territory;

import java.util.List;

/**
 * record the result of attack
 */
public class AttackResult {
    int attackerID;
    int defenderID;
    List<String> srcTerritories;
    String destTerritory;
    boolean isAttackerWin;

    public AttackResult(int attackerID, int defenderID, List<String> srcTerritories, String destTerritory, boolean isAttackerWin) {
        this.attackerID = attackerID;
        this.defenderID = defenderID;
        this.srcTerritories = srcTerritories;
        this.destTerritory = destTerritory;
        this.isAttackerWin = isAttackerWin;

    }

    public int getAttackerID() {
        return attackerID;
    }

    public int getDefenderID() {
        return defenderID;
    }

    public boolean isAttackerWin() {
        return isAttackerWin;
    }

    public List<String> getSrcTerritories() {
        return srcTerritories;
    }

    public String getDestTerritory() {
        return destTerritory;
    }

}
