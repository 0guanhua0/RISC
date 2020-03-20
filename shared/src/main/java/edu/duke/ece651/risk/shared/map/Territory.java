package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class Territory implements Serializable {

    Set<Territory> neigh;
    //class to represent current status of this territory
    TStatus status;
    HashMap<Integer, Integer> attackAct;

    public Territory(String name) {
        this.neigh = new HashSet<>();
        this.status = new TStatus(name);
        this.attackAct = new HashMap<>();
    }

    //get the owner id of corresponding territory
    public int getOwner() {
        return status.getOwnerId();
    }

    //assign this territory to corresponding user
    public void setOwner(int id) {
        status.setIsFree(false);
        status.setOwnerId(id);
    }

    //get all adjacent territories
    public Set<Territory> getNeigh() {
        return neigh;
    }

    public void setNeigh(Set<Territory> neigh) {
        this.neigh = neigh;
    }

    public String getName() {
        return status.getName();
    }

    public boolean isFree() {
        return status.isFree();
    }

    public void setIsFree(boolean isFree) {
        status.setIsFree(isFree);
        status.setOwnerId(0);
    }

    //helper function to check if two territories are adjacent to each other
    private boolean DFSHelper(Territory current, Territory target, Set<Territory> visited) {
        if (visited.contains(current) || current.getOwner() != this.getOwner()) {
            return false;
        } else if (current == target) {
            return true;
        } else {
            visited.add(current);
            for (Territory neigh : current.getNeigh()) {
                if (DFSHelper(neigh, target, visited)) {
                    return true;
                }
            }
            return false;
        }
    }

    //return true only when there is path from current territory to the target territory,
    //and all territories along the path should under the control of owner of current territory
    //TODO test the correctness of this method
    public boolean hasPathTo(Territory target) {
        if (this == target || target.getOwner() != this.getOwner()) {//a territory is not adjacent to itself
            return false;
        }
        Set<Territory> visited = new HashSet<>();
        return DFSHelper(this, target, visited);
    }

    public abstract int getUnitsNum();

    public abstract void addNUnits(int num) throws IllegalArgumentException;

    public abstract void lossNUnits(int num);

    public abstract void addAttack(int playerId, int unitNum);


    /**
     * called at the end of round, to update all combat info
     */
    public abstract void performMove();


    /**
     * random dice
     *
     * @param min lower bound
     * @param max upeer bound
     * @return
     */
    //random number decide attack
    public abstract boolean random(int min, int max);
}
