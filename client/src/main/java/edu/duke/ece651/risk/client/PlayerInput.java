package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * the class deal with player input
 */
public class PlayerInput {
    public static void read(InputStream inputStream, Player player, ActionList actList) throws IOException {
        while (true) {
            Instruction.actInfo(player.getPlayerName());
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
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String insn = in.readLine();
        return insn;
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
        Instruction.srcInfo();
        String src = readInput(inputStream);
        Instruction.dstInfo();
        String dst = readInput(inputStream);

        //read unit
        Instruction.unitInfo();
        String unit = readInput(inputStream);

        if(!Format.isNumeric(unit)) {
            System.out.println("invalid unit number");
            return;
        }

        int unitNum  = Integer.parseInt(unit);
        switch (currAct) {
            case "A":
                AttackAction a = new AttackAction(src, dst, player_id, unitNum);
                actionList.addActions("A", a);
                break;
            case "M":
                MoveAction m = new MoveAction(src, dst, player_id, unitNum);
                actionList.addActions("M", m);
                break;
        }

    }




}
