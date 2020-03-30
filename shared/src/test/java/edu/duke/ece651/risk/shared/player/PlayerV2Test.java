package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static edu.duke.ece651.risk.shared.Constant.INITIAL_FOOD_NUM;
import static edu.duke.ece651.risk.shared.Constant.INITIAL_TECH_NUM;
import static org.junit.jupiter.api.Assertions.*;

class PlayerV2Test {



    @Test
    void updateResource() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");
        Territory reach = worldMap.getTerritory("kingdom of the reach");
        Territory vale = worldMap.getTerritory("kingdom of mountain and vale");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlayerV2<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        player.setId(1);

        player.addTerritory(storm);
        player.addTerritory(reach);
        player.addTerritory(vale);

        for (int i = 1; i <= 10; i++) {
            player.updateResource();
            int foodYield = (storm.getFoodYield()+reach.getFoodYield()+vale.getFoodYield())*i;
            int techYield = (storm.getTechYield()+reach.getTechYield()+vale.getTechYield())*i;
            assertEquals(player.food.getRemain(), foodYield+INITIAL_FOOD_NUM);
            assertEquals(player.tech.getRemain(),techYield+INITIAL_TECH_NUM);
        }

    }

    @Test
    void getFoodNum() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Player<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);

        assertEquals(player.getFoodNum(),INITIAL_FOOD_NUM);
    }

    @Test
    void getTechNum() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlayerV2<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        player.setId(1);

        assertEquals(player.getTechNum(),INITIAL_TECH_NUM);
    }

    @Test
    void useFood() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Player<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        player.setId(1);
        player.addTerritory(storm);

        assertEquals(player.getFoodNum(),INITIAL_FOOD_NUM);
        player.useFood(INITIAL_FOOD_NUM);
        assertEquals(0, player.getFoodNum());

        assertThrows(IllegalArgumentException.class,()->{player.useFood(1);});
        player.updateResource();

        assertEquals(storm.getFoodYield(),player.getFoodNum());

    }

    @Test
    void useTech() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Player<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        player.setId(1);

        player.addTerritory(storm);

        assertEquals(player.getTechNum(),INITIAL_TECH_NUM);
        player.useTech(INITIAL_TECH_NUM);
        assertEquals(0, player.getTechNum());

        assertThrows(IllegalArgumentException.class,()->{player.useTech(1);});
        player.updateResource();

        assertEquals(storm.getTechYield(),player.getTechNum());

    }
}