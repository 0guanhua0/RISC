package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV1;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PlayerInputTest {
    static Player<String> player = new Player<>();
    static List<Territory> territories = new ArrayList<>();
    static Territory t1 = new TerritoryV1("t1");
    static Territory t2 = new TerritoryV1("t2");
    static Territory t3 = new TerritoryV1("t3");

    @BeforeAll
    static void beforeAll(){
        //TODO note that it's not the correct way to test this code
        //who writes the code here should refactor it
        t1.setFree();
        t2.setFree();
        t3.setOwner(1);

        t1.addNUnits(10);
        t2.addNUnits(10);

        territories.add(t1);
        territories.add(t2);
        territories.add(t3);
    }

    @Test
    void readValidAction() {
        assertNull(PlayerInput.readValidAction(new Scanner("D\n"), player, territories));
        assertNotNull(PlayerInput.readValidAction(new Scanner("A\n1\n1\n1\nD\n"), player, territories));

        // invalid + attack
        Action action1 = PlayerInput.readValidAction(new Scanner("c\n" + "a\n1\n1\n10\nd\n"), player, territories);
        assertTrue(action1 instanceof AttackAction);

        AttackAction a1 = new AttackAction("t1", "t3", 0, 10);
        assertEquals(a1, action1);

        // invalid + move
        Action action2 = PlayerInput.readValidAction(new Scanner("c\n" + "m\n2\n1\n5\nd\n"), player, territories);
        assertTrue(action2 instanceof MoveAction);

        MoveAction a2 = new MoveAction("t2", "t1", 0, 5);
        assertEquals(a2, action2);
    }

    @Test
    void readAction() {
        String s1 = "1\n1\n10\n";
        Scanner sc1 = new Scanner(s1);
        Action action1 = PlayerInput.readAction(sc1, 0, "A", territories);
        AttackAction a1 = new AttackAction("t1", "t3", 0, 10);

        assertEquals(action1, a1);

        String s2 = "1\n2\n10\n";
        Scanner sc2 = new Scanner(s2);
        Action action2 = PlayerInput.readAction(sc2, 0, "M", territories);

        MoveAction m1 = new MoveAction("t1", "t2", 0, 10);

        assertEquals(action2, m1);
    }

    @Test
    void testReadValidInt(){
        assertEquals(5,
                PlayerInput.readValidInt(
                        new Scanner("abc\n0\n11\n5"),
                        1,
                        10)
        );
    }

    @Test
    void testGroupTerritory(){
        Map<Integer, Territory> territoryOwn = new HashMap<>();
        Map<Integer, Territory> territoryOther = new HashMap<>();
        PlayerInput.groupTerritory(territories, player.getPlayerID(), territoryOwn, territoryOther);
        assertEquals(2, territoryOwn.size());
        assertEquals(1, territoryOther.size());
        assertTrue(territoryOther.containsValue(t3));
    }
}