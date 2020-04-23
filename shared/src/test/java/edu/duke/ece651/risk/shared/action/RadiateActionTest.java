package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.Mock;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RadiateActionTest {
    private static final String storm = "the storm kingdom";
    private static final String reach = "kingdom of the reach";
    private static final String rock = "kingdom of the rock";
    private static final String vale = "kingdom of mountain and vale";
    private static final String north = "kingdom of the north";
    private static final String dorne = "principality of dorne";

    @Test
    void isValid() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player3 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);
        player3.setId(3);

        Territory stormTerr = worldMap.getTerritory("the storm kingdom");
        Territory reachTerr = worldMap.getTerritory("kingdom of the reach");
        Territory rockTerr = worldMap.getTerritory("kingdom of the rock");

        player1.addTerritory(stormTerr);
        player2.addTerritory(reachTerr);
        player3.addTerritory(rockTerr);


        WorldState worldState = new WorldState(player1, worldMap, Arrays.asList(player1, player2,player3));

        AllyAction allyAction = new AllyAction(2);
        assertTrue(allyAction.isValid(worldState));
        player1.setAllyRequest(2);
        assertFalse(allyAction.isValid(worldState));
        player2.setAllyRequest(1);
        player1.allyWith(player2);

        player1.upMaxTech();
        for (int i = 0; i < 10; i++) {
            player1.updateState();
        }
        RadiateAction radiateAction = new RadiateAction("rock");
        assertFalse(radiateAction.isValid(worldState));

        player1.upMaxTech();
        for (int i = 0; i < 30; i++) {
            player1.updateState();
        }
        player1.upMaxTech();

        RadiateAction radiateAction1 = new RadiateAction(rock);
        assertTrue(radiateAction1.isValid(worldState));


        RadiateAction radiateAction2 = new RadiateAction("invalid");
        assertFalse(radiateAction2.isValid(worldState));

        RadiateAction radiateAction3 = new RadiateAction(storm);
        assertFalse(radiateAction3.isValid(worldState));

        RadiateAction radiateAction4 = new RadiateAction(reach);
        assertFalse(radiateAction4.isValid(worldState));

        int techNum = player1.getTechNum();
        player1.useTech(techNum-80);
        RadiateAction radiateAction5 = new RadiateAction(rock);
        assertFalse(radiateAction5.isValid(worldState));
    }

    @Test
    void perform() throws IOException {
        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> worldMap = mapDataBase.getMap("a clash of kings");
        PlayerV2<String> player1 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        PlayerV2<String> player2 = new PlayerV2<String>(Mock.setupMockInput(Arrays.asList()),new ByteArrayOutputStream());
        player1.setId(1);
        player2.setId(2);

        Territory stormTerr = worldMap.getTerritory("the storm kingdom");
        Territory reachTerr = worldMap.getTerritory("kingdom of the reach");

        player1.addTerritory(stormTerr);
        player2.addTerritory(reachTerr);


        WorldState worldState = new WorldState(player1, worldMap, Arrays.asList(player1, player2));


        player1.upMaxTech();
        for (int i = 0; i < 10; i++) {
            player1.updateState();
        }
        player1.upMaxTech();
        for (int i = 0; i < 30; i++) {
            player1.updateState();
        }
        player1.upMaxTech();


        RadiateAction radiateAction = new RadiateAction("invalid");
        assertThrows(IllegalArgumentException.class,()->{radiateAction.perform(worldState);});

        RadiateAction radiateAction1 = new RadiateAction(reach);
        radiateAction1.perform(worldState);
        assertTrue(reachTerr.isRadiated());

    }
}