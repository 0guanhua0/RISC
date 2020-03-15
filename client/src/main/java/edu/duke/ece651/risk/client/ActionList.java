package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.action.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * record user action
 */
public class ActionList {
    private HashMap<String, List<Action>> actions;

    public ActionList() {
        actions = new HashMap();
        ArrayList a = new ArrayList();
        ArrayList m = new ArrayList();
        actions.put(Constant.ACTION_ATTACK, a);
        actions.put(Constant.ACTION_MOVE, m);
    }

    /**
     * getter
     * @return actions
     */
    public HashMap<String, List<Action>> getActions() {
        return actions;
    }

    /**
     * add Action
     * @param s type
     * @param a action
     */
    public void addAction(String s, Action a) {
        List<Action> act = actions.get(s);
        act.add(a);
        actions.put(s, act);
    }
}
