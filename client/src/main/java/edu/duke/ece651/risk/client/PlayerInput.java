package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;

import java.util.Scanner;

/**
 * the class deal with player input
 */
public class PlayerInput {
    public static void read(Scanner scanner, Player player, ActionList actList) {
        while (true) {
            InsPrompt.actInfo(player.getPlayerName());
            //read
            String str = scanner.nextLine();
            System.out.println(str);
            //D: done
            if (str.equals("D")) {
                break;
            }
            //M/A
            if (str.equals("A") || str.equals("M")) {
                readAction(scanner, player.getPlayerId(), str, actList);
            }
            else {
                System.out.println("invalid input");
            }

        }
    }

    /**
     * read player action
     * @param player_id player id
     * @param currAct curr action
     * @param actionList list store aciton
     */
    public static void readAction(Scanner sc, int player_id, String currAct, ActionList actionList) {
        InsPrompt.srcInfo();
        String src = sc.nextLine();
        InsPrompt.dstInfo();
        String dst = sc.nextLine();

        //read unit
        InsPrompt.unitInfo();
        String unit = sc.nextLine();

        if(!Format.isNumeric(unit)) {
            System.out.println("invalid unit number");
            return;
        }
        int unitNum  = Integer.parseInt(unit);
        switch (currAct) {
            case "A":
                AttackAction a = new AttackAction(src, dst, player_id, unitNum);
                actionList.addAction(Constant.ACTION_ATTACK, a);
                return;
            case "M":
                MoveAction m = new MoveAction(src, dst, player_id, unitNum);
                actionList.addAction(Constant.ACTION_MOVE, m);
                return;
        }
    }
}
