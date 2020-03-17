package edu.duke.ece651.risk.shared.ToClientMsg;

import java.io.Serializable;
import java.util.Set;

/**
 * @program: risk
 * @description: this class has all fields that server want client to know to select territories
 * @author: Chengda Wu(cw402)
 * @create: 2020-03-16 17:17
 **/
public class ClientSelect implements Serializable {
    int terrNum;
    int unitsNum;
    Set<String> occupied;


    public ClientSelect(int terrNum, int unitsNum, Set<String> occupied) {
        this.terrNum = terrNum;
        this.unitsNum = unitsNum;
        this.occupied = occupied;
    }
}
