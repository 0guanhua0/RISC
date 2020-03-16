package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;

import java.util.Scanner;

/**
 * the class deal with player input
 */
public class PlayerInput {
    /**
     * Ask user for action type input. e.g.
     * D(one)
     * A(ttack)
     * M(ove)
     * Q(uit)
     * @param scanner scanner
     * @param player interacting user
     * @param actList action list
     * @return false when user want ot quit the game
     */
    public static boolean read(Scanner scanner, Player player, ActionList actList) {
        while (true) {
            InsPrompt.actInfo(player.getPlayerName());
            //read
            String str = scanner.nextLine().toUpperCase();
            System.out.println(str);
            //D: done
            switch (str) {
                case "D":
                    return true;
                //M/A
                case "A":
                case "M":
                    readAction(scanner, player.getPlayerId(), str, actList);
                    break;
                case "Q":
                    return false;
                default:
                    System.out.println("invalid input");
                    break;
            }
        }
    }

    /**
     * read player action
     * @param scanner scanner
     * @param player_id player id
     * @param currAct curr action
     * @param actionList list store aciton
     */
    public static void readAction(Scanner scanner, int player_id, String currAct, ActionList actionList) {
        InsPrompt.srcInfo();
        String src = scanner.nextLine().toUpperCase();
        InsPrompt.dstInfo();
        String dst = scanner.nextLine().toUpperCase();

        //read unit
        InsPrompt.unitInfo();
        String unit = scanner.nextLine();

        if(!Format.isNumeric(unit)) {
            System.out.println("invalid unit number");
            return;
        }
        int unitNum  = Integer.parseInt(unit);
        switch (currAct) {
            case "A":
                AttackAction a = new AttackAction(src, dst, player_id, unitNum);
                actionList.addAction(Constant.ACTION_ATTACK, a);
                break;
            case "M":
                MoveAction m = new MoveAction(src, dst, player_id, unitNum);
                actionList.addAction(Constant.ACTION_MOVE, m);
                break;
        }
    }
}
