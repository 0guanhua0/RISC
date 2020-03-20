package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class AttackAction implements Action, Serializable {
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

    /**
     * validate the attack move
     *
     * @param worldMap
     * @return true if valid, false if invalid
     */

    @Override
    public boolean isValid(WorldMap<?> worldMap) {
        //validate src & dst & unitnum
        if (!worldMap.hasTerritory(this.src) || !worldMap.hasTerritory(this.dest) || this.unitsNum <= 0) {
            return false;
        }

        //validate src own by player
        Territory src = worldMap.getTerritory(this.src);
        if (src.getOwner() != this.player_id) {
            return false;
        }

        //validate src has enough unit
        if (src.getUnitsNum() < this.unitsNum) {
            return false;
        }
        //validate dst owns by opponent

        Territory dst = worldMap.getTerritory(this.dest);
        if (dst.getOwner() == this.player_id) {
            return false;
        }

        //validate connection
        Set<Territory> neighbour = src.getNeigh();
        if (!neighbour.contains(dst)) {
            return false;
        }


        return true;
    }


    /**
     * following function perform single attack update, add update to territory map
     *
     * @param worldMap
     * @return true, if valid
     */
    @Override
    public boolean perform(WorldMap<?> worldMap) {
        //validate
        if (!isValid(worldMap)) {
            throw new IllegalArgumentException("Invalid attack action!");
        }

        //reduce src unit num
        Territory src = worldMap.getTerritory(this.src);
        src.lossNUnits(this.unitsNum);

        //add move to dst
        Territory dst = worldMap.getTerritory(this.dest);

        dst.addMove(this);

        return true;
    }

    public boolean performSingleAttack(WorldMap<?> worldMap) {
        //validate
        if (!isValid(worldMap)) {
            throw new IllegalArgumentException("Invalid attack action!");
        }

        //perform actual action
        //check dst will change owner or not
        Territory dst = worldMap.getTerritory(this.dest);

        //perform attack action
        while (unitsNum > 0 && dst.getUnitsNum() > 0) {
            if (random(0, 20)) {
                unitsNum--;
            } else {
                dst.lossNUnits(1);
            }
        }

        //check which player occupy

        //TODO: store combat result
        //check which player occupy
        //attacker has more unit, then change owner
        if (this.unitsNum > 0) {
            //switch owner & reset unit
            dst.setOwner(this.player_id);
            dst.addNUnits(this.unitsNum);
        }

        return true;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AttackAction) {
            AttackAction attackAction = (AttackAction) obj;
            return attackAction.src.equals(this.src) && attackAction.dest.equals(this.dest) && attackAction.unitsNum == this.unitsNum && attackAction.player_id == this.player_id;
        }
        return false;
    }

    //random number decide attack
    public boolean random(int min, int max) {
        int ran1 = ThreadLocalRandom.current().nextInt(min, max + 1);
        int ran2 = ThreadLocalRandom.current().nextInt(min, max + 1);

        return ran1 < ran2;
    }
}
