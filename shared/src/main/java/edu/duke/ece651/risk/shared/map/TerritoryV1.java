package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.action.AttackResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

    public void addAttack(int playerId, int unitNum) {
        if (attackAct.containsKey(playerId)) {
            int newNum = attackAct.get(playerId) + unitNum;
            attackAct.replace(playerId, newNum);
        } else {
            attackAct.put(playerId, unitNum);
        }
    }

    /**
     * called at the end of round, to update all combat info
     * will return string to tell who wins
     */
    public String performAttackMove() {
        //store the whole result of combat
        StringBuilder sb = new StringBuilder();
        ArrayList<AttackResult> attackResults = new ArrayList<>();
        //iterate through list
        for (Integer a : attackAct.keySet()) {
            //perform attack action
            Integer unitsNum = attackAct.get(a);

            //store attack result info
            int attackerId = a;
            int defenderID = this.getOwner();
            String tName = this.getName();
            Boolean aRes = true;
            //the start of the fight
            sb.append("player " + a + " attacks player + "  + this.getOwner()  +"'s " + this.getName() + "\n");
            while (unitsNum > 0 && this.getUnitsNum() > 0) {
                if (random(0, 20)) {
                    unitsNum--;
                } else {
                    this.lossNUnits(1);
                }
            }

            //update the owner only if attacker has remain
            if (unitsNum > 0) {
                setOwner(a);
                addNUnits(unitsNum);
            }

            //add winner
            if (unitsNum > 0) {
                aRes = true;
                sb.append("attacker wins\n");
            }
            else {
                aRes = false;
                sb.append("attacker fails\n");
            }
            AttackResult r = new AttackResult(attackerId, defenderID, tName, aRes);
            attackResults.add(r);

        }

        //clean up attackMap
        attackAct.clear();

        return sb.toString();
    }


    /**
     * random boolean, simulate the dice, return true indicate p 1 wins
     *
     * @param min lower bound
     * @param max upeer bound
     * @return
     */
    //random number decide attack
    public boolean random(int min, int max) {
        int ran1 = ThreadLocalRandom.current().nextInt(min, max + 1);
        int ran2 = ThreadLocalRandom.current().nextInt(min, max + 1);

        return ran1 < ran2;
    }

}
