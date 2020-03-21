package edu.duke.ece651.risk.shared.action;

/**
 * record the result of attack
 */
public class AttackResult {
    int attackerID;
    int defenderID;
    String territory;
    boolean attackerwin;


    public AttackResult(int attackerID, int defenderID, String territory, boolean attackerwin) {
        this.attackerID = attackerID;
        this.defenderID = defenderID;
        this.territory = territory;
        this.attackerwin = attackerwin;

    }

    public int getAttackerID() {
        return attackerID;
    }

    public int getDefenderID() {
        return defenderID;
    }

    public boolean isAttackerwin() {
        return attackerwin;
    }
    public String getTerritory() {
        return territory;
    }

}
