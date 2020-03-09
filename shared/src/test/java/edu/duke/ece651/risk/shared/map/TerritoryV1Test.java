package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TerritoryV1Test {


    @Test
    void getOwner() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        stormKindom.setOwner(3);
        assert 3==stormKindom.getOwner();
        assert !stormKindom.isFree();
    }

    @Test
    void setNeigh() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        TerritoryV1 n1 = new TerritoryV1("n1");
        TerritoryV1 n2 = new TerritoryV1("n2");
        HashSet<Territory> neigh = new HashSet<>(){{
            add(n1);
            add(n2);
        }};
        stormKindom.setNeigh(neigh);
        assert stormKindom.neigh.contains(n1);
        assert stormKindom.neigh.contains(n2);
    }

    @Test
    void isFree() {
        TerritoryV1 stormKindom = new TerritoryV1("The Storm Kindom");
        assert stormKindom.isFree();
        stormKindom.setOwner(3);
        assert 3==stormKindom.getOwner() && !stormKindom.isFree();
    }

    @Test
    void addNUnits() {
        TerritoryV1 test = new TerritoryV1("test");
        assert test.units.isEmpty();
        test.addNUnits(10);
        assert 10==test.getUnitsNum();
        for (Unit unit : test.units) {
            unit.name.equals("soldier");
        }
    }

    @Test
    void lossNUnits() {
        TerritoryV1 test = new TerritoryV1("test");
        assert test.units.isEmpty();
        test.addNUnits(10);
        assert 10==test.getUnitsNum();

        try {
            test.lossNUnits(11);
            assert false;
        }catch (IllegalArgumentException e){
            assert 10==test.getUnitsNum();
            for (Unit unit : test.units) {
                unit.name.equals("soldier");
            }
        }

        test.lossNUnits(5);
        assert 5==test.getUnitsNum();
        for (Unit unit : test.units) {
            unit.name.equals("soldier");
        }
        test.lossNUnits(5);
        assert 0==test.getUnitsNum();
    }
}