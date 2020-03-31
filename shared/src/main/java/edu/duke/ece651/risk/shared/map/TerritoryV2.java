package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.action.AttackResult;

import java.io.IOException;
import java.util.*;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;

/**
 * @program: risk
 * @description: this is territory class for evolution2 of risk game
 * @author: Chengda Wu
 * @create: 2020-03-28 10:20
 **/
public class TerritoryV2 extends TerritoryV1 {

    int size;
    int foodYield;
    int techYield;
    Group group;

    public TerritoryV2(String name, int size, int foodYield, int techYield) {
        super(name);
        this.size = size;
        this.foodYield = foodYield;
        this.techYield = techYield;
        this.group = new Group();
    }

    public int getSize() {
        return size;
    }

    @Override
    public int getFoodYield(){
        return foodYield;
    }

    @Override
    public int getTechYield() {
        return techYield;
    }

    //TODO  overwrite this method
    @Override
    public void addAttack(int playerId, Army army) {
        super.addAttack(playerId, army);
    }

    //TODO overwrite this method
    @Override
    AttackResult resolveCombat(int attackerID, List<Army> armies, Random diceAttack, Random diceDefend) {
        return super.resolveCombat(attackerID, armies, diceAttack, diceDefend);
    }

    //TODO overwrite this method
    @Override
    public List<AttackResult> resolveCombats() throws IOException {
        return super.resolveCombats();
    }

    @Override
    public boolean canAddUnits(int num, int level) {
        return group.canAdd(num,level);
    }

    @Override
    public boolean canLoseUnits(int num, int level) {
        return group.canLose(num,level);
    }

    @Override
    public void addBasicUnits(int num) throws IllegalArgumentException {
        if (!canAddUnits(num,0)){
            throw new IllegalArgumentException("invalid arguments!");
        }
        addUnits(num,0);
    }

    @Override
    public void loseBasicUnits(int num) throws IllegalArgumentException {
        if (!canLoseUnits(num,0)){
            throw new IllegalArgumentException("Invalid arguments");
        }
        loseUnits(num,0);
    }

    @Override
    public void addUnits(int num, int level) {
        if (!canAddUnits(num,level)){
            throw new IllegalArgumentException("invalid input arguments!");
        }
        group.addUnits(num,level);
    }

    @Override
    public void loseUnits(int num, int level) {
        if (!canLoseUnits(num, level)){
            throw new IllegalArgumentException("invalid input arguments");
        }
        group.loseUnits(num,level);
    }

    @Override
    public int getBasicUnitsNum() {
        return group.getUnitsNum(0);
    }

    @Override
    public int getUnitsNum(int level) {
        return group.getUnitsNum(level);
    }
}
