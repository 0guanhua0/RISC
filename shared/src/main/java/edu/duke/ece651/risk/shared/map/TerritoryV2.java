package edu.duke.ece651.risk.shared.map;

import java.util.*;

/**
 * @program: risk
 * @description: this is territory class for evolution2 of risk game
 * @author: Chengda Wu
 * @create: 2020-03-28 10:20
 **/
public class TerritoryV2 extends TerritoryV1 {

    int size;
    public TerritoryV2(String name,int size){
        super(name);
        this.size = size;
    }
    public int getSize() {
        return size;
    }
}
