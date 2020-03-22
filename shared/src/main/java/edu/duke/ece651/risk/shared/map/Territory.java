package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.action.Army;
import edu.duke.ece651.risk.shared.action.AttackResult;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static edu.duke.ece651.risk.shared.Utils.readFileToString;

public abstract class Territory implements Serializable {

    Set<Territory> neigh;
    //class to represent current status of this territory
    TStatus status;

    HashMap<Integer, List<Army>> attackAct;

    public Territory(String name) {
        this.neigh = new HashSet<>();
        this.status = new TStatus(name);
        this.attackAct = new HashMap<>();
    }

    //get the owner id of corresponding territory
    public int getOwner() {
        return status.getOwnerId();
    }

    //assign this territory to corresponding user
    public void setOwner(int id) {
        status.setIsFree(false);
        status.setOwnerId(id);
    }

    //get all adjacent territories
    public Set<Territory> getNeigh() {
        return neigh;
    }

    public void setNeigh(Set<Territory> neigh) {
        this.neigh = neigh;
    }

    public String getName() {
        return status.getName();
    }

    public boolean isFree() {
        return status.isFree();
    }

    public void setIsFree(boolean isFree) {
        status.setIsFree(isFree);
        status.setOwnerId(0);
    }

    //helper function to check if two territories are adjacent to each other
    private boolean DFSHelper(Territory current, Territory target, Set<Territory> visited) {
        if (visited.contains(current) || current.getOwner() != this.getOwner()) {
            return false;
        } else if (current == target) {
            return true;
        } else {
            visited.add(current);
            for (Territory neigh : current.getNeigh()) {
                if (DFSHelper(neigh, target, visited)) {
                    return true;
                }
            }
            return false;
        }
    }

    //return true only when there is path from current territory to the target territory,
    //and all territories along the path should under the control of owner of current territory
    //TODO test the correctness of this method
    public boolean hasPathTo(Territory target) {
        if (this == target || target.getOwner() != this.getOwner()) {//a territory is not adjacent to itself
            return false;
        }
        Set<Territory> visited = new HashSet<>();
        return DFSHelper(this, target, visited);
    }

    /**
     * This function will resolve all combats happen in current territory.
     * @return list of combat result
     */
    public List<AttackResult> resolveCombats() throws IOException{
        JSONObject jsonObject = new JSONObject(readFileToString("../config_file/random_seed_config.txt"));
        Random diceAttack = new Random(jsonObject.getInt("attackSeed"));
        Random diceDefend = new Random(jsonObject.getInt("defendSeed"));

        // store the whole result of combat
        ArrayList<AttackResult> attackResults = new ArrayList<>();
        // iterate through attack list
        for (Map.Entry<Integer, List<Army>> entry : attackAct.entrySet()) {
            attackResults.add(resolveCombat(entry.getKey(), entry.getValue(), diceAttack, diceDefend));
        }

        // clean up attackMap
        attackAct.clear();

        return attackResults;
    }

    public abstract int getUnitsNum();

    public abstract void addNUnits(int num) throws IllegalArgumentException;

    public abstract void lossNUnits(int num);

    public abstract void addAttack(int playerId, Army army);

    abstract AttackResult resolveCombat(int attackerID, List<Army> armies, Random diceAttack, Random diceDefend);
}
