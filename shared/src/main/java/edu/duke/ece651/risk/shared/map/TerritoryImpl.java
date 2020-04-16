package edu.duke.ece651.risk.shared.map;

import edu.duke.ece651.risk.shared.action.AttackResult;
import edu.duke.ece651.risk.shared.player.Player;

import java.util.*;
import java.util.stream.Collectors;

import static edu.duke.ece651.risk.shared.Constant.UNIT_BONUS;

/**
 * @program: risk
 * @description: this is territory class for evolution2 of risk game
 * @author: Chengda Wu
 * @create: 2020-03-28 10:20
 **/
public class TerritoryImpl extends Territory {
    private static final long serialVersionUID = 11L;

    int size;
    int foodYield;
    int techYield;
    //key is the technology level of units, value is the set of units
    TreeMap<Integer, List<Unit>> unitGroup;

    //unit from ally
    TreeMap<Integer, List<Unit>> allyUnits;



    public TerritoryImpl(String name, int size, int foodYield, int techYield) {
        super(name);
        this.size = size;
        this.foodYield = foodYield;
        this.techYield = techYield;
        this.unitGroup = new TreeMap<>();;
        this.allyUnits = new TreeMap<>();
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
    public void addAttack(Player player, Army army) {
        if (attackAct.containsKey(player)) {
            attackAct.get(player).add(army);
        } else {
            attackAct.put(player, new ArrayList<Army>(Collections.singletonList(army)));
        }
    }

    //TODO test the correctness of this method
    @Override
    AttackResult resolveCombat(Map<Player, List<Army>> unifiedArmy, Random diceAttack, Random diceDefend) {
        // retrieve the attack info
        int defenderID = getOwner();

        //get all territories where these army come from
        List<String> srcNames = new ArrayList<>();
        for (Map.Entry<Player, List<Army>> entry : unifiedArmy.entrySet()) {
            List<Army> comeFrom = entry.getValue();
            srcNames.addAll(comeFrom.stream().map(Army::getSrc).collect(Collectors.toList()));
        }
        String destName = getName();

        //represent the attackers army
        //represent seperate armies for each player, so after the battle, we know how many units left for each single player
        List<TreeMap<Integer,Integer>> combinedAttack = new ArrayList<>();
        List<Player> attackers = new ArrayList<>();
        for (Player player : unifiedArmy.keySet()) {//iterate through each single player
            TreeMap<Integer,Integer> enemy = new TreeMap<Integer, Integer>();//caculate
            List<Army> armies = unifiedArmy.get(player);
            for (Army army : armies) {//for each single player, iterate through all armies
                Map<Integer, Integer> troops = army.getTroops();
                for (Map.Entry<Integer, Integer> entry : troops.entrySet()) {
                    enemy.put(entry.getKey(), enemy.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
            }
            combinedAttack.add(enemy);
            attackers.add(player);
        }

        boolean isDefenderTurn = false;

        // start combat
        while (!attackerDone(combinedAttack)&&!defenderDone()) {
            //decide each side level and caculate corresponding result
            List<Integer> attackList = isDefenderTurn?selectMinAttackUnit(combinedAttack):selectMaxAttackUnit(combinedAttack);
            List<Integer> defenderList = isDefenderTurn?selectMaxDefendUnit():selectMinDefendUnit();
            int i1 = diceAttack.nextInt(20)+UNIT_BONUS.get(attackList.get(0)); // attacker dice
            int i2 = diceDefend.nextInt(20)+UNIT_BONUS.get(defenderList.get(0)); // defender dice

            // the one with lower roll loss one unit(for tie, defender win)
            if (i1 <= i2) {//attacker loses
                updateAttacker(attackList.get(0),attackList.get(1),combinedAttack);
            } else {//defender loses
                //decide which defender lose this unit
                updateDefender(defenderList.get(0),defenderList.get(1));
            }
            isDefenderTurn = !isDefenderTurn;
        }
        //update the ownership , ally-relation ship and unit state if attackers win the battle
        int ownerId = updateState(attackers, combinedAttack);
        //TODO note that since we can multiple attackers, the logic here need to be changed
        return new AttackResult(ownerId==-1?attackers.get(0).getId():ownerId, defenderID, srcNames, destName, !attackerDone(combinedAttack));
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
            units.add(new Unit(level));
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


        //update source unit
        if (source.size()==num){
            unitGroup.remove(curLevel);
        }else{
            for (int i = 0; i < num; i++) {
                source.remove(source.size()-1);
            }
            unitGroup.put(curLevel, source);
        }
        //update target unit
        for (int i = 0; i < num; i++) {
            target.add(new Unit(targetLevel));
        }

        unitGroup.put(targetLevel,target);
    }

    @Override
    public Map<Integer, List<Unit>> getUnitGroup() {
        return unitGroup;
    }


    @Override
    public void addAllyUnit(Unit unit) {
        if (ally==null){
            throw new IllegalStateException("Invalid state");
        }
        int level = unit.getLevel();
        List<Unit> units = allyUnits.getOrDefault(level, new ArrayList<>());
        units.add(unit);
        allyUnits.put(level,units);
    }

    /**
     * this method change the state of all territories: mark the allyId as -1(no ally)
     * and expel all units to nearest territory
     */
    @Override
    public void ruptureAlly(){
        if (null == ally){
            throw new IllegalStateException("Invalid action");
        }
        Set<Territory> neigh = this.getNeigh();
        Set<Territory> visited = new HashSet<>();
        //using BFS to find the most near Territory
        Queue<Territory> queue = new ArrayDeque<>();
        queue.addAll(neigh);
        while(!queue.isEmpty()){
            int size = queue.size();
            for (int i=0;i<size;i++){//iterate through new level
                Territory territory = queue.poll();
                if (visited.contains(territory)) continue;
                visited.add(territory);
                if (territory.getOwner()== ally.getId()){//find target territory, expel all ally unit to this territory
                    //expel all units
                    this.expelAlly(territory);
                    //mark ally as not existed
                    break;
                }else{//add new adjacent territories
                    Set<Territory> neighTmp = territory.getNeigh();
                    queue.addAll(neighTmp);
                }
            }
        }
        this.ally = null;
    }

    /**
     * this method can only add free units
     * @param unit: unit to add
     */
    @Override
    public void addUnit(Unit unit) {
        int level = unit.getLevel();
        List<Unit> units = unitGroup.getOrDefault(level, new ArrayList<>());
        units.add(unit);
        unitGroup.put(level,units);
    }

    @Override
    public void addUnits(List<Unit> units) {
        for (Unit unit : units) {
            this.addUnit(unit);
        }
    }

    private void expelAlly(Territory allyTerr){
        if (this.allyUnits.isEmpty()){
            return;
        }
        //expel all units
        for (int level : allyUnits.keySet()) {
            List<Unit> units = allyUnits.get(level);
            allyTerr.addUnits(units);
        }
        this.allyUnits = new TreeMap<>();
    }

    /**
     * check if the attacker lose the battle or not
     * @param combinedAttack: unified armies from all attackers
     * @return true when attackers lose all units
     */
    private boolean attackerDone(List<TreeMap<Integer,Integer>> combinedAttack){
        for (TreeMap<Integer, Integer> treeMap : combinedAttack) {
            if (!treeMap.isEmpty()){
                return false;
            }
        }
        return true;
    }


    /**
     * check if the defender lose the battle or not
     * @return true when defenders lose all units
     */
    private boolean defenderDone(){
        return unitGroup.isEmpty()&&(null==allyUnits||allyUnits.isEmpty());
    }


    /**
     * select the unit on defend side with maximum level
     * @return the first element of list is the level of selected units
     * the second element of list represents where does this unit come from(0 for unitGroup, 1 for allyGroup)
     */
    List<Integer> selectMaxDefendUnit(){
        if (unitGroup.isEmpty()&&(allyUnits.isEmpty())){
            throw new IllegalStateException("Invalid state");
        }
        if (unitGroup.isEmpty()||(allyUnits.isEmpty())){
            int level = unitGroup.isEmpty()?allyUnits.lastKey():unitGroup.lastKey();
            int index = unitGroup.isEmpty()?1:0;
            return Arrays.asList(level,index);
        }else{
            int myLevel = unitGroup.lastKey();
            int allyLevel = allyUnits.lastKey();
            if (myLevel==allyLevel){
                return Arrays.asList(myLevel,new Random().nextInt(2));
            }else{
                int idx = myLevel>allyLevel?0:1;
                int level = Math.max(myLevel,allyLevel);
                return Arrays.asList(level,idx);
            }
        }
    }


    /**
     * select the unit on defend side with minimum level
     * @return the first element of list is the level of selected units
     * the second element of list represents where does this unit come from(0 for unitGroup, 1 for allyGroup)
     */
    List<Integer> selectMinDefendUnit(){
        if (unitGroup.isEmpty()&&(null==allyUnits||allyUnits.isEmpty())){
            throw new IllegalStateException("Invalid state");
        }
        if (unitGroup.isEmpty()||(null==allyUnits||allyUnits.isEmpty())){
            int level = unitGroup.isEmpty()?allyUnits.firstKey():unitGroup.firstKey();
            int index = unitGroup.isEmpty()?1:0;
            return Arrays.asList(level,index);
        }else{
            int myLevel = unitGroup.firstKey();
            int allyLevel = allyUnits.firstKey();
            if (myLevel==allyLevel){
                return Arrays.asList(myLevel,new Random().nextInt(2));
            }else{
                int idx = myLevel<allyLevel?0:1;
                int level = Math.min(myLevel,allyLevel);
                return Arrays.asList(level,idx);
            }
        }
    }


    /**
     * select the units with maximum level from the attacker side,
     * randomly pick one when there are multiple units with same level
     * @param combinedAttack: unified army from different attackers
     * @return: the first element of list is the level, second element is for index
     */
    List<Integer> selectMaxAttackUnit(List<TreeMap<Integer,Integer>> combinedAttack){
        List<Integer> max = new ArrayList<>();
        int maxLevel = Integer.MIN_VALUE;
        for (int i = 0; i < combinedAttack.size(); i++) {
            TreeMap<Integer, Integer> treeMap = combinedAttack.get(i);
            if (treeMap.isEmpty()) continue;
            int maxKey = treeMap.lastKey();
            if (maxKey>maxLevel){
                max = new ArrayList<>();
                max.add(i);
                maxLevel = maxKey;
            }else if (maxKey==maxLevel){
                max.add(i);
            }
        }

        Random random = new Random();
        int selectedPlayer = max.get(random.nextInt(max.size()));
        return Arrays.asList(maxLevel,selectedPlayer);
    }

    /**
     * select the units with minimum level from the attacker side,
     * randomly pick one when there are multiple units with same level
     * @param combinedAttack: unified army from different attackers
     * @return: the first element of list is the level, second element is for index
     */
    List<Integer> selectMinAttackUnit(List<TreeMap<Integer,Integer>> combinedAttack){
        List<Integer> min = new ArrayList<>();
        int minLevel = Integer.MAX_VALUE;
        for (int i = 0; i < combinedAttack.size(); i++) {
            TreeMap<Integer, Integer> treeMap = combinedAttack.get(i);
            if (treeMap.isEmpty()) continue;
            int minKey = treeMap.firstKey();
            if (minKey<minLevel){
                min = new ArrayList<>();
                min.add(i);
                minLevel = minKey;
            }else if (minKey==minLevel){
                min.add(i);
            }
        }
        Random random = new Random();
        int selectedPlayer = min.get(random.nextInt(min.size()));
        return Arrays.asList(minLevel,selectedPlayer);
    }



    /**
     * when attackers lose a unit with corresponding level, use this method to update the state
     * @param level: level of units
     * @param index: index inside array, which records which player here lose this unit
     * @param combinedAttack: the unified army from attacker
     */
    void updateAttacker(int level, int index, List<TreeMap<Integer,Integer>> combinedAttack){
        TreeMap<Integer, Integer> treeMap = combinedAttack.get(index);
        if (treeMap.getOrDefault(level,0)<=0){
            throw new IllegalArgumentException("Invalid level");
        }
        if (1==treeMap.get(level)){//remove the entry when we no longer have
            treeMap.remove(level);
        }else{
            treeMap.put(level,treeMap.get(level)-1);
        }
    }

    /**
     * when defender lose a unit with corresponding level, use this method to update the state
     * @param level: level of units
     * @param idx: if 0, update owner, otherwise update the ally
     */
    void updateDefender(int level,int idx){
        if (0==idx){//update the owner
            this.loseUnits(1,level);
        }else {//update the ally
            assert(allyUnits.get(level).size()>=1);
            if (allyUnits.get(level).size()==1){
                allyUnits.remove(level);
            }else{
                List<Unit> units = allyUnits.get(level);
                units.remove(units.size()-1);
                allyUnits.put(level,units);
            }
        }
    }


    /**
     * after each combat, this method should be called,when attackers win the battle,
     * this method will change allyGroup field and unitGroup field to units sent by attacker
     * @param attackers: all attackers for this battle
     * @param combinedAttack: all force from attackers
     * @return return player id of owner if attackers win, -1 otherwise
     */
    int updateState(List<Player> attackers, List<TreeMap<Integer,Integer>> combinedAttack){
        // update the ownership only if attacker has units left
        if (!attackerDone(combinedAttack)) {
            //if there are multiple attackers decide, randomly pick one to be the owner of this territory
            int random = new Random().nextInt(attackers.size());
            Player finalOwner = attackers.get(random);
            TreeMap<Integer, Integer> ownerForce = combinedAttack.get(random);
            //note that for multiple alliance, the line below will need to be changed
            Player ownerAlly = null;
            TreeMap<Integer, Integer> allyForce = new TreeMap<Integer, Integer>();
            if (2==attackers.size()){
                ownerAlly = attackers.get(Math.abs(1-random));
                allyForce = combinedAttack.get(Math.abs(1 - random));
            }
            //change the ownership and ally for this territory
            setOwner(finalOwner.getId());
            setAlly(ownerAlly);
            //rebuild the defender group based on the attacker side force
            this.unitGroup = buildForce(ownerForce);
            this.allyUnits = buildForce(allyForce);
            return finalOwner.getId();
        }else{
            return -1;
        }
    }

    /**
     * this method servers as a helper function to covert attack information to real unit
     * @param input: map which represent attack information
     * @return conversion result
     */
    private TreeMap<Integer,List<Unit>> buildForce(Map<Integer,Integer> input){
        TreeMap<Integer,List<Unit>> output = new TreeMap<Integer, List<Unit>>();
        for (Map.Entry<Integer, Integer> entry : input.entrySet()) {
            int num = entry.getValue();
            int level = entry.getKey();
            List<Unit> units = new ArrayList<Unit>();
            for (int i = 0; i < num ; i++) {
                units.add(new Unit(level));
            }
            output.put(level,units);
        }
        return output;
    }

    @Override
    public int getAllyUnitsNum(int level){
        if (allyUnits.isEmpty()) return 0;
        else return allyUnits.getOrDefault(level,new ArrayList<>()).size();
    }


}
