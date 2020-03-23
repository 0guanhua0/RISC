package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.Territory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * @param territories all territory
     * @return Action object, null represent Done
     */
    public static Action readValidAction(Scanner scanner, Player<String> player, List<Territory> territories) {
        while (true) {
            InsPrompt.actInfo(player);

            String str = scanner.nextLine().toUpperCase();
            // D: done
            switch (str) {
                case "D":
                    return null;
                // M/A
                case "A":
                case "M":
                    return readAction(scanner, player.getPlayerID(), str, territories);
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
    public static Action readAction(Scanner scanner, int playerId, String currAct, List<Territory> territories) {
        Map<Integer, Territory> territoryOwn = new HashMap<>();
        Map<Integer, Territory> territoryOther = new HashMap<>();
        groupTerritory(territories, playerId, territoryOwn, territoryOther);

        /* ====== read source info ====== */
        // the source territory can only be own by current player
        InsPrompt.srcInfo(territoryOwn);
        int srcIndex = readValidInt(scanner, 1, territoryOwn.size());
        String src = territoryOwn.get(srcIndex).getName();

        /* ====== read destination info ====== */
        String dst = "";
        if (currAct.equals("A")){
            // the destination of attack can only be territories own by other player
            InsPrompt.dstInfo(territoryOther);
            int dstIndex = readValidInt(scanner, 1, territoryOther.size());
            dst = territoryOther.get(dstIndex).getName();
        }else {
            // the destination of move can only be territories own by current player
            InsPrompt.dstInfo(territoryOwn);
            int dstIndex = readValidInt(scanner, 1, territoryOwn.size());
            dst = territoryOwn.get(dstIndex).getName();
        }

        /* ====== read units info ====== */
        InsPrompt.unitInfo();
        int unitNum = readValidInt(scanner, 1, territoryOwn.get(srcIndex).getUnitsNum());

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

    /**
     * This function will group territories into own by current player and not.
     * The key of both map is index, the value is territory name.
     * Use these two map to facilitate the process of select territory.
     * @param territories all territories
     * @param playerID the ID of current player
     * @param territoryOwn territories current player own
     * @param territoryOther territories own by other players
     */
    static void groupTerritory(
            List<Territory> territories,
            int playerID,
            Map<Integer, Territory> territoryOwn,
            Map<Integer, Territory> territoryOther
    ){
        int indexOwn = 1;
        int indexOther = 1;
        for (Territory territory : territories){
            if (territory.getOwner() == playerID){
                territoryOwn.put(indexOwn, territory);
                indexOwn++;
            }else {
                territoryOther.put(indexOther, territory);
                indexOther++;
            }
        }
    }
}
