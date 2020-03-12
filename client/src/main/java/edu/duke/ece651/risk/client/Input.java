package edu.duke.ece651.risk.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Input {
    //read from input
    public String readInput(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String insn = in.readLine();
        return insn;
    }

    /**
     *
     * @param inputString
     * @return check if done
     */
    public Boolean isDone(String inputString)
    {
        return inputString.equals("D");
    }

    /**
     *
     * @param inputString
     * @return check if move
     */
    public Boolean isMove(String inputString)
    {
        return inputString.equals("M");
    }

    /**
     *
     * @param inputString
     * @return check if attack
     */
    public Boolean isAttack(String inputString)
    {
        return inputString.equals("A");
    }
}
