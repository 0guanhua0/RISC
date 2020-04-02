package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static edu.duke.ece651.risk.shared.Constant.*;
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
            player.updateState();
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
        player.updateState();

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
        player.updateState();

        assertEquals(storm.getTechYield(),player.getTechNum());

    }

    @Test
    void canUpTech() throws IOException {

        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlayerV2<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        player.setId(1);
        player.addTerritory(storm);

        assertTrue(player.canUpMaxTech());
        player.upMaxTech();//after this operation is level 2
        assertFalse(player.canUpMaxTech());//test can only upgrade technology once during every single round of game
        player.updateState();
        assertFalse(player.canUpMaxTech());//test don't have enough resources
        for (int i=1;i<100;i++){
            player.updateState();
        }
        player.upMaxTech();//after this operation is level 3
        player.updateState();
        player.upMaxTech();//after this operation is level 4
        player.updateState();
        player.upMaxTech();//after this operation is level 5
        player.updateState();
        player.upMaxTech();//after this operation is level 6
        player.updateState();
        assertFalse(player.canUpMaxTech());//test at the maximum level
    }

    @Test
    void upTech() throws IOException {

        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlayerV2<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        player.setId(1);
        player.addTerritory(storm);

        player.upMaxTech();
        assertEquals(1,player.techLevel);
        assertFalse(player.upTechRight);
        assertEquals(player.tech.getRemain(),INITIAL_TECH_NUM-TECH_MAP.get(1));
        player.updateState();
        assertEquals(2,player.techLevel);
        assertTrue(player.upTechRight);
    }

    @Test
    void upMaxTech() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<String>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        Territory storm = worldMap.getTerritory("the storm kingdom");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlayerV2<String> player = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),out);
        player.setId(1);

        assertDoesNotThrow(()->{player.upMaxTech();});
        assertThrows(IllegalArgumentException.class,()->{player.upMaxTech();});
    }
}