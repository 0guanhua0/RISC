package edu.duke.ece651.risk.shared.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;

/**
 * @program: risk
 * @description: this is the group class to represent a group of units for evolution2
 * @author: Chengda Wu
 * @create: 2020-03-31 10:44
 **/
public class Group {

    //key is the technology level of units, value is the set of units
    Map<Integer, Set<Unit>> unitGroup;

    public Group(Map<Integer, Set<Unit>> unitGroup) {
        this.unitGroup = unitGroup;
    }

    public Group(){
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
        Set<Unit> units = unitGroup.get(level);
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
        Set<Unit> units = unitGroup.get(level);
        for (int i = 0; i < num; i++) {
            units.add(new Unit());
        }
        for (int i = 0; i < num; i++) {
            units.remove(units.size() - 1);
        }
    }



}
