package edu.duke.ece651.risk.client;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * the class deal with player input
 */
public class PlayerInput {
    public static void read(InputStream inputStream, Player player, HashMap<String, List<Action>> actions) throws IOException {
        while (true) {
            Instruction0.actInfo(player.getPlayerName());
            //read
            String str = readInput(inputStream);
            //D: done
            if (str.equals("D")) {
                break;
            }
            //M/A
            if (str.equals("A") || str.equals("M")) {
                readAction(inputStream, player.getPlayerId(), str, actions);
            }

        }
    }


    //read from input
    public static String readInput(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String insn = in.readLine();
        return insn;
    }

    public static void readAction(InputStream inputStream, int player_id, String currAct, HashMap<String, List<Action>> actions) throws IOException {
        Instruction0.srcInfo();
        String src = readInput(inputStream);
        Instruction0.dstInfo();
        String dst = readInput(inputStream);

        //read unit
        Instruction0.unitInfo();
        String unit = readInput(inputStream);
        if (!isNumeric(unit)) {
            System.out.println("invalid unit number");
        }

        int unitNum = Integer.parseInt(unit);

        switch (currAct) {
            case "A":
                AttackAction a = new AttackAction(src, dst, player_id, unitNum);
                List<Action> act = actions.get("A");
                act.add(a);
                actions.put("A", act);
                break;
            case "M":
                MoveAction m = new MoveAction(src, dst, player_id, unitNum);
                List<Action> actM = actions.get("M");
                actM.add(m);
                actions.put("M", actM);
                break;
        }





    }

    /**
     * regular expression to check is number or not
     */
    private static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }


}
