package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.Mock;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PlayerV1Test {

    @Test
    void getFoodNum() throws IOException {
        PlayerV1<String> playerV1 = new PlayerV1<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        assertEquals(playerV1.getFoodNum(),Integer.MAX_VALUE);
    }
    @Test
    void getTechNum() throws IOException {
        PlayerV1<String> playerV1 = new PlayerV1<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        assertEquals(playerV1.getTechNum(),Integer.MAX_VALUE);
    }
}