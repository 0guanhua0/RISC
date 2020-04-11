package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.action.AttackResult;

import java.util.*;
import java.util.stream.Collectors;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;
import static edu.duke.ece651.risk.shared.Utils.getMaxKey;
import static edu.duke.ece651.risk.shared.Utils.getMinKey;

/**
 * @program: risk
 * @description: this is territory class for evolution2 of risk game
 * @author: Chengda Wu
 * @create: 2020-03-28 10:20
 **/
public class TerritoryV2 extends Territory {

    int size;
    int foodYield;
    int techYield;
    //key is the technology level of units, value is the set of units
    Map<Integer, List<Unit>> unitGroup;

    public TerritoryV2(String name, int size, int foodYield, int techYield) {
        super(name);
        this.size = size;
        this.foodYield = foodYield;
        this.techYield = techYield;
        this.unitGroup = new HashMap<>();;
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

    @Override
    public void addAttack(int playerId, Army army) {
        if (attackAct.containsKey(playerId)) {
            attackAct.get(playerId).add(army);
        } else {
            attackAct.put(playerId, new ArrayList<Army>(Collections.singletonList(army)));
        }
    }

    //TODO test the correctness of this data
    @Override
    AttackResult resolveCombat(int attackerID, List<Army> armies, Random diceAttack, Random diceDefend) {
        // retrieve the attack info
        int defenderID = getOwner();
        List<String> srcNames = armies.stream().map(Army::getSrc).collect(Collectors.toList());
        String destName = getName();

        //get all forces that this enemy has
        Map<Integer,Integer> enemy = new HashMap<Integer, Integer>();
        for (Army army : armies){
            Map<Integer, Integer> troops = army.getTroops();
            for (Map.Entry<Integer, Integer> entry : troops.entrySet()) {
                enemy.put(entry.getKey(),enemy.getOrDefault(entry.getKey(),0)+entry.getValue());
            }
        }
        boolean isOwnTurn = false;
        // start combat
        while (!enemy.isEmpty() && !unitGroup.isEmpty()) {
            //select the unit for attacker and defender
            int attackLevel = isOwnTurn? getMinKey(enemy): getMaxKey(enemy);
            int defendLevel = isOwnTurn? getMaxKey(unitGroup): getMinKey(unitGroup);
            int i1 = diceAttack.nextInt(20)+UNIT_BONUS.get(attackLevel); // attacker dice
            int i2 = diceDefend.nextInt(20)+UNIT_BONUS.get(defendLevel); // defender dice
            // the one with lower roll loss one unit(tie, defender win)
            if (i1 <= i2) {//attacker loses
                enemy.put(attackLevel,enemy.get(attackLevel)-1);
                if (0==enemy.get(attackLevel)){
                    enemy.remove(attackLevel);
                }
            } else {//defender loses
                List<Unit> units = unitGroup.get(defendLevel);
                if (1==units.size()){
                    unitGroup.remove(defendLevel);
                }else{
                    units.remove(units.size()-1);
                }
            }
            isOwnTurn = !isOwnTurn;
        }
        // update the ownership only if attacker has units left
        if (!enemy.isEmpty()) {
            setOwner(attackerID);
            this.unitGroup = new HashMap<Integer, List<Unit>>();
            for (Map.Entry<Integer, Integer> entry : enemy.entrySet()) {
                int num = entry.getValue();
                List<Unit> units = new ArrayList<>();
                for (int i = 0; i < num ; i++) {
                    units.add(new Unit());
                }
                unitGroup.put(entry.getKey(),units);
            }
        }
        return new AttackResult(attackerID, defenderID, srcNames, destName, !enemy.isEmpty());
    }

    @Override
    public boolean canAddUnits(int num, int level) {
        if (num<=0||!UNIT_BONUS.containsKey(level)){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean canLoseUnits(int num, int level) {
        if (num<=0||!UNIT_BONUS.containsKey(level)||unitGroup.getOrDefault(level,new ArrayList<>()).size()<num){
            return false;
        }else{
            return true;
        }
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
        List<Unit> units = unitGroup.getOrDefault(level,new ArrayList<>());
        for (int i = 0; i < num; i++) {
            units.add(new Unit());
        }
        unitGroup.put(level,units);
    }

    @Override
    public void loseUnits(int num, int level) {
        if (!canLoseUnits(num, level)){
            throw new IllegalArgumentException("invalid input arguments");
        }
        if (unitGroup.get(level).size()==num){
            unitGroup.remove(level);
        }else{
            List<Unit> units = unitGroup.get(level);
            for (int i = 0; i < num; i++) {
                units.remove(units.size()-1);
            }
            unitGroup.put(level,units);
        }
    }

    @Override
    public int getBasicUnitsNum() {
        return unitGroup.getOrDefault(0,new ArrayList<>()).size();
    }

    @Override
    public int getUnitsNum(int level) {
        return unitGroup.getOrDefault(level,new ArrayList<>()).size();
    }

    @Override
    public boolean canUpUnit(int unitsNum, int srcLevel, int targetLevel) {
        //check if the number of units with source tech level is valid
        if (unitsNum<=0||this.getUnitsNum(srcLevel)<unitsNum||srcLevel>=targetLevel){
            return false;
        }
        //check if the target tech level is valid
        if (!UNIT_BONUS.containsKey(targetLevel)){
            return false;
        }
        return true;
    }

    @Override
    public void upUnit(int num, int curLevel, int targetLevel) {
        if (!canUpUnit(num,curLevel,targetLevel)){
            throw new IllegalArgumentException("Invalid argument!");
        }
        List<Unit> source = unitGroup.get(curLevel);
        List<Unit> target = unitGroup.getOrDefault(targetLevel, new ArrayList<Unit>());


        //update source territory
        if (source.size()==num){
            unitGroup.remove(curLevel);
        }else{
            for (int i = 0; i < num; i++) {
                source.remove(source.size()-1);
            }
            unitGroup.put(curLevel, source);
        }
        //update target territory
        for (int i = 0; i < num; i++) {
            target.add(new Unit());
        }

        unitGroup.put(targetLevel,target);
    }

    @Override
    public Map<Integer, List<Unit>> getUnitGroup() {
        return unitGroup;
    }
}
