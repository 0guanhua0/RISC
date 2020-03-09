package edu.duke.ece651.risk.shared.map;

import java.util.List;
import java.util.Set;

public abstract class Territory {
    Set<Territory> neigh;
    TStatus status;

    public int getOwner(){
        return status.getOwnerId();
    }
    public void setOwner(int id){
        status.setIsFree(false);
        status.setOwnerId(id);
    }

    public void setNeigh(Set<Territory> neigh){
        this.neigh = neigh;
    }

    public boolean isFree(){
        return status.isFree();
    }

    abstract int getUnitsNum();

    abstract void addNUnits(int num);

    abstract void lossNUnits(int num);
}
