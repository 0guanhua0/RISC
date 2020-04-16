package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.action.AttackResult;
import edu.duke.ece651.risk.shared.player.Player;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Transient;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static edu.duke.ece651.risk.shared.Utils.readFileToString;

@Embedded
public abstract class Territory implements Serializable {
    private static final long serialVersionUID = 10L;

    @Transient
    Set<Territory> neigh;
    //class to represent current status of this territory
    @Embedded
    TStatus status;

    @Embedded
    HashMap<Player, List<Army>> attackAct;

    //use null to represent don't have any friends
    @Embedded
    Player ally;

    public Territory(String name) {
        this.neigh = new HashSet<>();
        this.status = new TStatus(name);
        this.attackAct = new HashMap<>();
        this.ally = null;
    }

    //morphia constructor
    public Territory() {

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

    /**
     * @return ally id of this this territory, -1 when don't have ally
     */
    public int getAllyId() {
        if (null==this.ally){
            return -1;
        }
        return ally.getId();
    }

    public void setAlly(Player ally) {
        this.ally = ally;
    }

    /**
     * the method is used to unify all players' army with their friends' army (if existed) and let them engage in the same battle
     * @return the list of unified army, each single map represents a unified army, key is player, value is their army
     */
    List<Map<Player,List<Army>>> buildUnifiedArmy(){
        List<Map<Player,List<Army>>> unifiedArmies = new ArrayList<>();
        Set<Player> visited = new HashSet<>();
        for (Player player : attackAct.keySet()) {
            if (!visited.contains(player)){
                //this if block can handle each single player and corresponding friend
                //visited make sure friend will not be counted twice
                Map<Player,List<Army>> unifiedArmy = new HashMap<>();
                unifiedArmy.put(player,attackAct.get(player));
                visited.add(player);
                Player ally = player.getAlly();
                if (null!=ally&&!visited.contains(ally)&&attackAct.containsKey(ally)){
                    visited.add(ally);
                    unifiedArmy.put(ally,attackAct.get(ally));
                }
                unifiedArmies.add(unifiedArmy);
            }
        }
        return unifiedArmies;
    }


    /**
     * This function will resolve all combats happen in current territory.
     * @return list of combat result
     */
    public List<AttackResult> resolveCombats() throws IOException{
        JSONObject jsonObject = new JSONObject(readFileToString("../config_file/random_seed_config.txt"));
        Random diceAttack = new Random(jsonObject.getInt("attackSeed"));
        Random diceDefend = new Random(jsonObject.getInt("defendSeed"));

        List<Map<Player,List<Army>>> unifiedArmies = buildUnifiedArmy();

        // store the whole result of combat
        ArrayList<AttackResult> attackResults = new ArrayList<>();
        // iterate through unified armies
        for (Map<Player, List<Army>> unifiedArmy : unifiedArmies) {
            attackResults.add(resolveCombat(unifiedArmy, diceAttack, diceDefend));
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



    /**
     * get the number of units from ally with certain tech level
     * @param level: technology level for units you want
     * @return number of units, 0 when level not exist or ally not existed
     */
    public abstract int getAllyUnitsNum(int level);

    public abstract void addUnit(Unit unit);


    /**
     * add some basic units into this territory
     * @param num: number of level0 units
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
     * @param num: number of level0 units
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

    public abstract void addAttack(Player player, Army army);

    abstract AttackResult resolveCombat(Map<Player, List<Army>> unifiedArmy, Random diceAttack, Random diceDefend);

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

    /**
     * rupture the alliance with ally, called inside attack action
     */
    public abstract void ruptureAlly();

    public abstract void addAllyUnit(Unit unit);

    public abstract void addUnits(List<Unit> units);

}
