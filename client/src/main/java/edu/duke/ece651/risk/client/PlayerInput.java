package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.action.Action;
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
     * @param scanner scanner
     * @param player interacting user
     * @return Action object, null represent Done
     */
    public static Action readValidAction(Scanner scanner, Player<String> player) {
        while (true) {
            InsPrompt.actInfo(player);
            //readAction
            String str = scanner.nextLine().toUpperCase();
            System.out.println(str);
            //D: done
            switch (str) {
                case "D":
                    // TODO: here we may need a DoneAction to indicate is done
                    return null;
                //M/A
                case "A":
                case "M":
                    Action action = readAction(scanner, player.getPlayerID(), str);
                    if (action != null){
                        return action;
                    }
                    break;
                default:
                    System.out.println("invalid input");
                    break;
            }
        }
    }

    /**
     * readAction player action
     * @param scanner scanner
     * @param playerId player id
     * @param currAct curr action
     */
    public static Action readAction(Scanner scanner, int playerId, String currAct) {
        InsPrompt.srcInfo();
        String src = scanner.nextLine().toUpperCase();
        InsPrompt.dstInfo();
        String dst = scanner.nextLine().toUpperCase();

        //readAction unit
        InsPrompt.unitInfo();
        String unit = scanner.nextLine();

        if(!Format.isNumeric(unit)) {
            System.out.println("invalid unit number");
            return null;
        }
        int unitNum  = Integer.parseInt(unit);

        return currAct.equals("A") ?
                new AttackAction(src, dst, playerId, unitNum) :
                new MoveAction(src, dst, playerId, unitNum);
    }

    /**
     * This function will ask user to input a valid int number.
     * @param scanner scanner
     * @param min the min value of input number, inclusive
     * @param max the max value of input number, inclusive
     * @return the number use input
     */
    public static int readValidInt(Scanner scanner, int min, int max){
        while (true){
            String num = scanner.nextLine();
            if (Format.isNumeric(num)){
                int n = Integer.parseInt(num);
                if (n >= min && n <= max){
                    return n;
                }
            }
            System.out.println("Invalid option, try again.");
        }
    }
}
