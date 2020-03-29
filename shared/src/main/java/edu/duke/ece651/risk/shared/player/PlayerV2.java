package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.map.BasicResource;
import edu.duke.ece651.risk.shared.map.Territory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static edu.duke.ece651.risk.shared.Mock.setupMockInput;

/**
 * @program: risk
 * @description: this is player class for evolution2
 * @author: Chengda Wu
 * @create: 2020-03-28 20:16
 **/
public class PlayerV2<T> extends PlayerV1 {
    BasicResource tech;
    BasicResource food;
    private void initResource(){
        tech = new BasicResource();
        food = new BasicResource();
    }

    public PlayerV2(InputStream in, OutputStream out) throws IOException {
        super(in, out);
        initResource();
    }

    public PlayerV2(T color, int id, InputStream in, OutputStream out) throws IOException {
        super(color, id, in, out);
        initResource();
    }

    public PlayerV2(int id, InputStream in, OutputStream out) throws IOException {
        super(id, in, out);
        initResource();
    }

    @Override
    public void updateResource() {
        for (Object o : territories) {
            Territory territory = (Territory)o;
            int foodYield = territory.getFoodYield();
            int techYield = territory.getTechYield();
            tech.addResource(techYield);
            food.addResource(foodYield);
        }
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

    }

    @Override
    public void useTech(int techUse) {
        if (getTechNum()<techUse){
            throw new IllegalArgumentException();
        }

    }
}
