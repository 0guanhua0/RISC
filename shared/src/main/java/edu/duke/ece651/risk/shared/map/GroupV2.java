package edu.duke.ece651.risk.shared.map;

import java.io.Serializable;
import java.util.*;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;

/**
 * @program: risk
 * @description: this is the group class to represent a group of units for evolution2
 * @author: Chengda Wu
 * @create: 2020-03-31 10:44
 **/
public class GroupV2 implements Serializable, Group {

    //key is the technology level of units, value is the set of units
    Map<Integer, List<Unit>> unitGroup;

    public GroupV2(Map<Integer, List<Unit>> unitGroup) {
        this.unitGroup = unitGroup;
    }

    public GroupV2(){
        this.unitGroup = new HashMap<>();
    }

    /**
     * check if an add operation makes sense or not
     * @param num: number of units to be added
     * @param level: technology level of units
     * @return: such action is valid or not
     */
    public boolean canAdd(int num,int level){
        if (num<=0||!UNIT_BONUS.containsKey(level)){
            return false;
        }else{
            return true;
        }
    }

    /**
     * check if a lose operation makes sense or not
     * @param num: number of units to lose
     * @param level: technology level of units
     * @return: such action is valid or not
     */
    public boolean canLose(int num,int level){
        if (num<=0||!UNIT_BONUS.containsKey(level)||unitGroup.get(level).size()<num){
            return false;
        }else{
            return true;
        }
    }

    /**
     * add some units with certain tech level into this group
     * @param num: number of units
     * @param level: technology level of units
     */
    public void addUnits(int num,int level){
        if (!canAdd(num,level)){
            throw new IllegalArgumentException("Invalid argument!");
        }
        List<Unit> units = unitGroup.getOrDefault(level,new ArrayList<>());
        for (int i = 0; i < num; i++) {
            units.add(new Unit());
        }
        unitGroup.put(level,units);
    }

    /**
     * lose some units with certain tech level
     * @param num: number of units
     * @param level: technology level of units
     */
    public void loseUnits(int num,int level){
        if (!canLose(num, level)){
            throw new IllegalArgumentException("Invalid argument");
        }
        List<Unit> units = unitGroup.get(level);
        for (int i = 0; i < num; i++) {
            units.remove(units.size()-1);
        }
        unitGroup.put(level,units);

    }

    /**
     * get the number of units with specified technology level
     * @param level: technology level of units you want
     * @return number of units, 0 for invalid technology level
     */
    public int getUnitsNum(int level){
        return unitGroup.getOrDefault(level,new ArrayList<>()).size();
    }


}
