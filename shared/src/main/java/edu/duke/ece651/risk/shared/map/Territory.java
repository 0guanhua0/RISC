package edu.duke.ece651.risk.shared.map;

import java.util.List;
import java.util.Set;

public abstract class Territory {

    Set<Territory> neigh;
    //class to represent current status of this territory
    TStatus status;

    public int getOwner(){
        return status.getOwnerId();
    }
    public void setOwner(int id){
        status.setIsFree(false);
        status.setOwnerId(id);
    }
    public Set<Territory> getNeigh() {
        return neigh;
    }
    public void setNeigh(Set<Territory> neigh){
        this.neigh = neigh;
    }

    public boolean isFree(){
        return status.isFree();
    }

    public void setIsFree(boolean isFree){
        status.setIsFree(isFree);
        status.setOwnerId(0);
    }

    abstract int getUnitsNum();

    abstract void addNUnits(int num);

    abstract void lossNUnits(int num);
}
