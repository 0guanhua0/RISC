package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class PlayerV1Test {
    @Test
    void constructor(){
        PlayerV1<String> p1 = new PlayerV1<String>("Red",1);
        assert p1.territories.isEmpty();
        assert p1.color.equals("Red");
        assert 1==p1.id;
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
        assert p1.territories.contains(n1);
        assert p1.territories.contains(n2);
        assert 1==n1.getOwner();
        assert 1==n2.getOwner();
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
            assert false;
        }catch (Exception e){
            assert true;
        }
        assert !p1.territories.contains(n1);
        assert p1.territories.contains(n2);
        assert n1.isFree();
        assert !n2.isFree();
    }
}