package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.action.Army;
import edu.duke.ece651.risk.shared.action.AttackResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @program: risk-Map
 * @description:
 * this is Territory class that let server use to set/represent/update the state of a certain territory
 * used for evolution1
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
        if (num > units.size() || num < 0){
            throw new IllegalArgumentException("Invalid input number");
        }
        for (int i = 0; i < num; i++) {
            units.remove(units.size() - 1);
        }
    }

    public int getUnitsNum(){
        return units.size();
    }

    public void addAttack(int playerId, Army army) {
        if (attackAct.containsKey(playerId)) {
            attackAct.get(playerId).add(army);
        } else {
            attackAct.put(playerId, new ArrayList<>(Collections.singletonList(army)));
        }
    }

    /**
     * This function will resolve one combat.
     * @param attackerID attacker id
     * @param armies all the armies the attacker send(from different territories)
     * @param diceAttack 20 side dice for attacker
     * @param diceDefend 20 side dice for defender
     * @return combat result
     */
    AttackResult resolveCombat(int attackerID, List<Army> armies, Random diceAttack, Random diceDefend){
        // the attack info
        int defenderID = getOwner();
        List<String> srcNames = new ArrayList<>();
        int attackUnits = 0;
        String destName = getName();

        for (Army army : armies){
            attackUnits += army.getUnitNums();
            srcNames.add(army.getSrc());
        }

        // start combat
        while (attackUnits > 0 && this.getUnitsNum() > 0) {
            int i1 = diceAttack.nextInt(20); // attacker dice
            int i2 = diceDefend.nextInt(20); // defender dice

            // the one with lower roll loss one unit(tie, defender win)
            if (i1 <= i2) {
                attackUnits--;
            } else {
                this.lossNUnits(1);
            }
        }
        // update the ownership only if attacker has units left
        if (attackUnits > 0) {
            setOwner(attackerID);
            // left units will remain in this territory
            addNUnits(attackUnits);
        }
        return new AttackResult(attackerID, defenderID, srcNames, destName, attackUnits > 0);
    }

    public int getSize(){
        return 0;
    }

    @Override
    public int getFoodYield(){
        return 0;
    }

    @Override
    public int getTechYield(){
        return 0;
    }
}
