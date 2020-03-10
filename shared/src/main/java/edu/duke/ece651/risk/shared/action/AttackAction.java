package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.map.WorldMap;

public class AttackAction implements Action {
    String src;
    String dest;
    int player_id;
    int unitsNum;

    public AttackAction(String src, String dest, int player_id, int unitsNum) {
        this.src = src;
        this.dest = dest;
        this.player_id = player_id;
        this.unitsNum = unitsNum;
    }


    @Override
    public boolean isValid(WorldMap worldMap) {
        return !src.equals(dest);
    }

    @Override
    public void perform(WorldMap worldMap) {

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AttackAction){
            AttackAction attackAction = (AttackAction) obj;
            return attackAction.src.equals(this.src) && attackAction.dest.equals(this.dest);
        }
        return false;
    }
}
