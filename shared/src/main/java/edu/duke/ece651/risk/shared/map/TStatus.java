package edu.duke.ece651.risk.shared.map;

import org.mongodb.morphia.annotations.Embedded;

import java.io.Serializable;

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

@Embedded
public class TStatus implements Serializable {

    //player id of the owner of corresponding territory, 0 to represent this territory is currently free
    //when ownerId==0, corresponding territory is free and not owned by any player
    int ownerId;
    //name of corresponding territory
    String name;


    public TStatus(String name) {
        this.name = name;
        this.ownerId = 0;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
    public int getOwnerId() {
        return ownerId;
    }

    public boolean isFree(){
        return ownerId==0 ;
    }

    public String getName() {
        return name;
    }
}
