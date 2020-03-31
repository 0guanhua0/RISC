package edu.duke.ece651.risk.shared.map;

import java.util.ArrayList;
import java.util.List;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;

public interface Group {

    /**
     * check if an add operation makes sense or not
     * @param num: number of units to be added
     * @param level: technology level of units
     * @return: such action is valid or not
     */
    boolean canAdd(int num,int level);

    /**
     * check if a lose operation makes sense or not
     * @param num: number of units to lose
     * @param level: technology level of units
     * @return: such action is valid or not
     */
    boolean canLose(int num,int level);

    /**
     * add some units with certain tech level into this group
     * @param num: number of units
     * @param level: technology level of units
     */
    void addUnits(int num,int level);

    /**
     * lose some units with certain tech level
     * @param num: number of units
     * @param level: technology level of units
     */
    public void loseUnits(int num,int level);

    /**
     * get the number of units with specified technology level
     * @param level: technology level of units you want
     * @return number of units, 0 for invalid technology level
     */
    public int getUnitsNum(int level);

}
