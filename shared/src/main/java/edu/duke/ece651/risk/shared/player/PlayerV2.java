package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.map.BasicResource;
import edu.duke.ece651.risk.shared.map.Territory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static edu.duke.ece651.risk.shared.Constant.*;

/**
 * @program: risk
 * @description: this is player class for evolution2
 * @author: Chengda Wu
 * @create: 2020-03-28 20:16
 **/
public class PlayerV2<T> extends PlayerV1<T> {
    BasicResource tech;
    BasicResource food;
    //this variable marks that this user have right to upgrade her maximum technology
    boolean upTechRight;
    int techLevel;
    private void initResource(){
        tech = new BasicResource(INITIAL_TECH_NUM);
        food = new BasicResource(INITIAL_FOOD_NUM);
        upTechRight = true;
        techLevel = 1;
    }

    public PlayerV2(InputStream in, OutputStream out) throws IOException {
        super(in, out);
        initResource();
    }

    @Override
    public void updateState() {
        for (Object o : territories) {
            Territory territory = (Territory)o;
            int foodYield = territory.getFoodYield();
            int techYield = territory.getTechYield();
            tech.addResource(techYield);
            food.addResource(foodYield);
        }
        this.upTechRight = true;
    }

    @Override
    public int getFoodNum() {
        return food.getRemain();
    }

    @Override
    public int getTechNum() {
        return tech.getRemain();
    }

    @Override
    public void useFood(int foodUse) {
        if (getFoodNum()<foodUse){
            throw new IllegalArgumentException();
        }
        food.useResource(foodUse);

    }

    @Override
    public void useTech(int techUse) {
        if (getTechNum()<techUse){
            throw new IllegalArgumentException();
        }
        tech.useResource(techUse);

    }

    @Override
    public boolean canUpTech() {
        if (upTechRight&&TECH_MAP.containsKey(techLevel)&&TECH_MAP.get(techLevel)<=getTechNum()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * this method should always be called after canUpTech
     */
    @Override
    public void upTech() {
        if (!canUpTech()){
            throw new IllegalArgumentException("Can't up tech now!");
        }
        this.useTech(TECH_MAP.get(techLevel));
        this.techLevel++;
        upTechRight = false;
    }

}
