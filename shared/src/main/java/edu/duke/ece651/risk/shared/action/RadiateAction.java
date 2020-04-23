package edu.duke.ece651.risk.shared.action;


import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;

import static edu.duke.ece651.risk.shared.Constant.*;

public class RadiateAction implements Action{

    String targetTerr;

    public RadiateAction(String targetTerr) {
        this.targetTerr = targetTerr;
    }

    @Override
    public boolean isValid(WorldState worldState) {
        WorldMap<String> map = worldState.getMap();
        Player<String> myPlayer = worldState.getMyPlayer();
        //invalid string
        if (!map.hasTerritory(this.targetTerr)){
            return false;
        }
        Territory target = map.getTerritory(this.targetTerr);
        //cannot attack herself
        if (target.getOwner()==myPlayer.getId()){
            return false;
        }
        //cannot attack ally
        if (target.getAllyId()==myPlayer.getId()){
            return false;
        }
        //must have higher enough level
        if (myPlayer.getTechLevel()<RADIATE_LEVEL){
            return false;
        }
        //must have enough tech resource
        if (myPlayer.getTechNum()<RADIATE_COST){
            return false;
        }
        return true;
    }

    @Override
    public boolean perform(WorldState worldState) {
        if (!this.isValid(worldState)){
            throw new IllegalArgumentException("Invalid argument");
        }
        WorldMap<String> map = worldState.getMap();
        Territory target = map.getTerritory(this.targetTerr);
        target.setRadiation();
        return true;
    }
}
