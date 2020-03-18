package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static edu.duke.ece651.risk.shared.Constant.ACTION_ATTACK;
import static edu.duke.ece651.risk.shared.Constant.ACTION_MOVE;
import static org.junit.jupiter.api.Assertions.*;

class PlayerInputTest {
    static Player<String> player = new Player<>();

    @Test
    void read() {
        assertNull(PlayerInput.readValidAction(new Scanner("D\n"), player));

        // invalid + attack(a->b, 10) + move(c->d, 5) + done
        Action action1 = PlayerInput.readValidAction(new Scanner("c\n" + "a\na\nb\n10\nd\n"), player);
        assertTrue(action1 instanceof AttackAction);

        AttackAction a1 = new AttackAction("A", "B", 0, 10);

        assert (a1.equals(action1));
    }


    @Test
    void readAction() {
        ActionList aL = new ActionList();

        String s0 = "A\nB\nA\n";
        Scanner sc0 = new Scanner(s0);
        assertNull(PlayerInput.readAction(sc0, 0, "A"));

        String s1 = "A\nB\n10\n";
        Scanner sc1 = new Scanner(s1);
        Action action1 = PlayerInput.readAction(sc1, 0, "A");
        AttackAction a1 = new AttackAction("A", "B", 0, 10);

        assertEquals(action1, a1);


        String s2 = "A\nB\n10\n";
        Scanner sc2 = new Scanner(s2);
        Action action2 = PlayerInput.readAction(sc2, 0, "M");

        MoveAction m1 = new MoveAction("A", "B", 0, 10);

        assertEquals(action2, m1);



    }
}