package edu.duke.ece651.risk.shared.player;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static edu.duke.ece651.risk.shared.Mock.setupMockInput;

/**
 * @program: risk
 * @description: this is player class for the first version of game
 * @author: Chengda Wu(cw402)
 * @create: 2020-03-09 17:42
 **/
public class PlayerV1<T> extends Player<T> {

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

    @Override
    public void updateState() {}

    @Override
    public int getFoodNum() { return Integer.MAX_VALUE; }

    @Override
    public int getTechNum() { return Integer.MAX_VALUE; }

    @Override
    public void useFood(int foodUse) {}

    @Override
    public void useTech(int techUse) {}

    @Override
    public boolean canUpTech() {
        return false;
    }

    @Override
    public void upTech() {}


}
