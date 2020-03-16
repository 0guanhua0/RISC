package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

class PlayerInputTest {
    static Player player = new Player(0, "A");
    ActionList aL = new ActionList();


    @Test
    void read() {
        String s0 = "D";
        Scanner sc0 = new Scanner(s0);
        PlayerInput.read(sc0, player, aL);



        String s1 = "A\nB\n10\n";
        Scanner sc1 = new Scanner(s1);
        PlayerInput.readAction(sc1, 0, "A", aL);
        AttackAction a1 = new AttackAction("A", "B", 0, 10);

        List<Action> actionList = aL.getActions().get(Constant.ACTION_ATTACK);
        Action action0 = actionList.get(0);
        assert (action0.equals(a1));



        String s2 = "K\nD";
        Scanner sc2 = new Scanner(s2);
        PlayerInput.read(sc2, player, aL);

    }


    @Test
    void readAction() {
        String s0 = "A\nB\nA\n";
        Scanner sc0 = new Scanner(s0);
        PlayerInput.readAction(sc0, 0, "A", aL);



        String s1 = "A\nB\n10\n";
        Scanner sc1 = new Scanner(s1);
        PlayerInput.readAction(sc1, 0, "A", aL);
        AttackAction a1 = new AttackAction("A", "B", 0, 10);

        List<Action> actionList = aL.getActions().get(Constant.ACTION_ATTACK);
        Action action0 = actionList.get(0);
        assert (action0.equals(a1));





        String s2 = "A\nB\n10\n";
        Scanner sc2 = new Scanner(s2);
        PlayerInput.readAction(sc2, 0, "M", aL);

        MoveAction m1 = new MoveAction("A", "B", 0, 10);
        actionList = aL.getActions().get(Constant.ACTION_MOVE);
        Action action1 = actionList.get(0);


        assert (action1.equals(m1));



    }
}