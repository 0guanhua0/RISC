package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;
import edu.duke.ece651.risk.shared.map.WorldMap;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

class PlayerV1Test {
    @Test
    void constructor(){
        PlayerV1<String> p1 = new PlayerV1<String>("Red",1);
        assert (p1.territories.isEmpty());
        assert (p1.color.equals("Red"));
        assert (1==p1.id);
        try {
            PlayerV1<String> p2 = new PlayerV1<String>("Red",0);
            assert (false);
        }catch (Exception e){
            assert (true);
        }

    }

    @Test
    void addTerritory() {
        PlayerV1<String> p1 = new PlayerV1<String>("Red",1);
        TerritoryV1 n1 = new TerritoryV1("n1");
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> n1Neigh = new HashSet<>(){{
            add(n2);
        }};
        p1.addTerritory(n1);
        p1.addTerritory(n2);
        assert (p1.territories.contains(n1));
        assert (p1.territories.contains(n2));
        assert (1==n1.getOwner());
        assert (1==n2.getOwner());
    }

    @Test
    void loseTerritory() {
        PlayerV1<String> p1 = new PlayerV1<String>("Red",1);
        TerritoryV1 n1 = new TerritoryV1("n1");
        int owner = n1.getOwner();
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> n1Neigh = new HashSet<>(){{
            add(n2);
        }};
        p1.addTerritory(n1);
        p1.addTerritory(n2);
        p1.loseTerritory(n1);
        TerritoryV1 n3 = new TerritoryV1("n3");
        try{
            p1.loseTerritory(n3);
            assert (false);
        }catch (Exception e){
            assert (true);
        }
        assert (!p1.territories.contains(n1));
        assert (p1.territories.contains(n2));
        assert (n1.isFree());
        assert (!n2.isFree());
    }

    @Test
    void getNeigh(){
        MapDataBase mapDataBase = new MapDataBase();
        WorldMap worldMap = mapDataBase.getMap("a clash of kings");
        String n1 = "the storm kingdom";
        String n2 = "kingdom of the reach";
        String n3 = "kingdom of the rock";
        String n4 = "kingdom of mountain and vale";
        String n5 = "kingdom of the north";
        String n6 = "principality of dorne";

        Territory t1 = worldMap.getTerritory(n1);
        Territory t2 = worldMap.getTerritory(n2);
        Territory t3 = worldMap.getTerritory(n3);

        Territory t4 = worldMap.getTerritory(n4);
        Territory t5 = worldMap.getTerritory(n5);
        Territory t6 = worldMap.getTerritory(n6);









    }
}