package edu.duke.ece651.risk.shared.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * @program: risk-Map
 * @description:
 * this is Territory class that let server use to set/represent/update the state of a certain territory
 * @author: Chengda Wu (cw402)
 * @create: 2020-03-08 20:37
 **/
public class TerritoryV1 extends Territory{

    List<Unit> units;

    public TerritoryV1(String name) {
       super(name);
       this.units = new ArrayList<>();
    }

    public void addNUnits(int num) throws IllegalArgumentException {
        if (num<0){
            throw new IllegalArgumentException("Input number can't be negative");
        }
        for (int i = 0; i < num; i++) {
            units.add(new Unit("soldier"));
        }
    }
    public void lossNUnits(int num) throws IllegalArgumentException{
        if (num>units.size()||num<0){
            throw new IllegalArgumentException("Invalid input number");
        }
        for (int i = 0; i < num; i++) {
            units.remove(units.size() - 1);
        }
    }

    public int getUnitsNum(){
        return units.size();
    }

}
