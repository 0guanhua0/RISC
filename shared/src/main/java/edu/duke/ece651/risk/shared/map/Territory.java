package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.action.AttackResult;
import edu.duke.ece651.risk.shared.player.Player;
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

    //use -1 to represent don't have any friends
    int friendId;


    public Territory(String name) {
        this.neigh = new HashSet<>();
        this.status = new TStatus(name);
        this.attackAct = new HashMap<>();
        this.friendId = -1;
    }

    //get the owner id of corresponding territory
    public int getOwner() {
        return status.getOwnerId();
    }


    //assign this territory to corresponding user
    //normally, this method shouldn't be called explicitly outside of the class
    //since when loseTerritory and add addTerritory are called, this method will be called automatically
    public void setOwner(int id) {
        if (id<=0){
            throw new IllegalArgumentException("a player id must be positive!");
        }
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

    public void setFree() {
        status.setOwnerId(0);
    }

    public int getFriendId() { return friendId; }
    public void setFriendId(int friendId) {
        this.friendId = friendId;
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

    /**
     * @return the number of basic units
     */
    public abstract int getBasicUnitsNum();

    /**
     * get the number of units with certain tech level
     * @param level: technology level for units you want
     * @return number of units, 0 when level not exist
     */
    public abstract int getUnitsNum(int level);

    public abstract void addUnit(Unit unit);


    /**
     * add some basic units into this territory
     * @param num： number of level0 units
     * @throws IllegalArgumentException
     */
    public abstract void addBasicUnits(int num) throws IllegalArgumentException;

    /**
     * add some units with specified level into this territory
     * @param num: number of units to move
     * @param level: technology level of this units
     */
    public abstract void addUnits(int num, int level);

    /**
     * let this territory lose some basic units
     * @param num： number of level0 units
     * @throws IllegalArgumentException
     */
    public abstract void loseBasicUnits(int num);

    /**
     * lose some units with specified level
     * @param num: number of units to move
     * @param level: technology level of this units
     */
    public abstract void loseUnits(int num,int level);

    /**
     * check if its a legal units group to add, also help ensure Liskov substitution
     * this method should ba called before any adding and losing operation
     * @param num: number of units for a add/lose operation
     * @param level: technology level for this set of units
     * @return whether such operation is legality or not
     */
    public abstract boolean canAddUnits(int num, int level);

    /**
     * check if its a legal units group to lose, also help ensure Liskov substitution
     * this method should ba called before any adding and losing operation
     * @param num: number of units for a add/lose operation
     * @param level: technology level for this set of units
     * @return whether such operation is legality or not
     */
    public abstract boolean canLoseUnits(int num, int level);

    public abstract void addAttack(int playerId, Army army);

    abstract AttackResult resolveCombat(int attackerID, List<Army> armies, Random diceAttack, Random diceDefend);

    abstract int getSize();

    abstract public int getFoodYield();
    abstract public int getTechYield();
    abstract public Map<Integer, List<Unit>> getUnitGroup();


    /**
     * @param num: number of units to update
     * @param curLevel: cur level of units
     * @param targetLevel: target level of units
     * @return can update or not
     */
    public abstract boolean canUpUnit(int num, int curLevel,int targetLevel);


    /**
     * update some units within certain territory from source level to target level
     * @param num: number of units to update
     * @param curLevel: cur level of units
     * @param targetLevel: target level of units
     */
    public abstract void upUnit(int num, int curLevel,int targetLevel);

    public abstract void ruptureAlly(Player p);

    public abstract void addFriendUnit(Unit unit);

    public abstract void addUnits(List<Unit> units);

}
