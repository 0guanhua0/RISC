package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.map.Territory;

import java.io.*;
import java.util.ArrayList;

import static edu.duke.ece651.risk.shared.Mock.setupMockInput;

/**
 * @program: risk
 * @description: this is player class for the first version of game
 * @author: Chengda Wu(cw402)
 * @create: 2020-03-09 17:42
 **/
public class PlayerV1<T> extends Player<T> implements Serializable {

    public PlayerV1(InputStream in, OutputStream out) throws IOException {
        super(in, out);
    }

    public PlayerV1(T color, int id) throws IllegalArgumentException, IOException {
        // just for testing purpose
        super(color, id, setupMockInput(new ArrayList<>()), new ByteArrayOutputStream());
    }

    public PlayerV1(T color, int id, InputStream in, OutputStream out) throws IOException {
        super(color, id, in, out);
    }
    public PlayerV1(int id, InputStream in, OutputStream out) throws IOException {
        super(id, in, out);
    }

    public PlayerV1() {
        super();
    }

    @Override
    public void updateState() {
        for (Territory territory : territories) {
            territory.addBasicUnits(1);
        }
    }

    @Override
    public int getFoodNum() { return Integer.MAX_VALUE; }

    @Override
    public int getTechNum() { return Integer.MAX_VALUE; }

    @Override
    public void useFood(int foodUse) {}

    @Override
    public void useTech(int techUse) {}

    @Override
    public boolean canUpMaxTech() {
        return false;
    }

    @Override
    public void upMaxTech() {}

    @Override
    public int getTechLevel() {
        return 1;
    }
}
