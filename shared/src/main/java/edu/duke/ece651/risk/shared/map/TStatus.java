package edu.duke.ece651.risk.shared.map;
/**
 * @program: risk-Map
 * @description:
 * this is class that represent the status of a territory
 * @author: Chengda Wu (cw402)
 * @create: 2020-03-08 20:32
 **/
public class TStatus {

    private int ownerId;
    String name;
    boolean isFree;

    public TStatus(String name) {
        this.name = name;
        this.isFree = true;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
    public int getOwnerId() {
        return ownerId;
    }


    public void setIsFree(boolean free) {
        isFree = free;
    }
    public boolean isFree(){
        return isFree;
    }

    public String getName() {
        return name;
    }
}
