package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackAction;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionListTest {
    ActionList aL = new ActionList();

    @Test
    void addActions() {
        HashMap<String, List<Action>> tmp =  aL.getActions();

        List<Action> a = tmp.get(Constant.ACTION_ATTACK);
        assertTrue(a.isEmpty());
        List<Action> m = tmp.get(Constant.ACTION_MOVE);
        assertTrue(m.isEmpty());

        AttackAction attackAction1 = new AttackAction("a", "B", 0, 0);
        aL.addAction(Constant.ACTION_ATTACK, attackAction1);
        a = tmp.get(Constant.ACTION_ATTACK);
        assertTrue(a.contains(attackAction1));
    }
}