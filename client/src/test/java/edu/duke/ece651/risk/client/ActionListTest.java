package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.action.AttackAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionListTest {
    ActionList aL = new ActionList();

    @Test
    void addActions() {
        aL.getActions();
        AttackAction a = new AttackAction("a", "B", 0, 0);
        aL.addActions("A", a);
    }
}