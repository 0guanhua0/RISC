package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.Constant;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * the class deal with player input
 */
public class PlayerInput {
    public static void read(InputStream inputStream, Player player, ActionList actList) throws IOException {
        while (true) {
            InsPrompt.actInfo(player.getPlayerName());
            //read
            String str = readInput(inputStream);
            //D: done
            if (str.equals("D")) {
                break;
            }
            //M/A
            if (str.equals("A") || str.equals("M")) {
                readAction(inputStream, player.getPlayerId(), str, actList);
            }
            else {
                System.out.println("invalid input");
            }

        }
    }


    //read from input
    public static String readInput(InputStream inputStream) throws IOException {
        Scanner scanner = new Scanner(inputStream);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNext()) {
            sb.append(scanner.nextLine());
        }
        return sb.toString();
    }

    /**
     * read player action
     * @param inputStream System.in
     * @param player_id player id
     * @param currAct curr action
     * @param actionList list store aciton
     * @throws IOException io exception
     */
    public static void readAction(InputStream inputStream, int player_id, String currAct, ActionList actionList) throws IOException {
        InsPrompt.srcInfo();
        String src = readInput(inputStream);
        InsPrompt.dstInfo();
        String dst = readInput(inputStream);

        //read unit
        InsPrompt.unitInfo();
        String unit = readInput(inputStream);

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
