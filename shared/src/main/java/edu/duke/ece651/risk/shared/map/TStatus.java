package edu.duke.ece651.risk.shared.map;
/**
 * @program: risk-Map
 * @description:
 * this is class that represent the status of a territory
 * note that when corresponding territory is occupied,
 * the ownerId should be the corresponding playerId, which should be a positive number
 * When this territory is free, the ownerId should be 0
 * @author: Chengda Wu (cw402)
 * @create: 2020-03-08 20:32
 **/

public class TStatus {

    //player id of the owner of corresponding territory
    private int ownerId;
    //name of corresponding territory
    String name;
    //show whether this territory is free or not
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
