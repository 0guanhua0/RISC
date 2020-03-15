package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackAction;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

class ActionListTest {
    ActionList aL = new ActionList();

    @Test
    void addActions() {
        HashMap<String, List<Action>> tmp =  aL.getActions();

        List<Action> a = tmp.get(Constant.ACTION_ATTACK);
        assert (a.isEmpty());
        List<Action> m = tmp.get(Constant.ACTION_MOVE);
        assert (m.isEmpty());

        AttackAction attackAction = new AttackAction("a", "B", 0, 0);
        aL.addAction(Constant.ACTION_ATTACK, attackAction);
        a = tmp.get(Constant.ACTION_ATTACK);
        assert (a.contains(attackAction));
    }
}