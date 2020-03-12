package edu.duke.ece651.risk.client;

class Instruction0 implements Instruction {

    public void actInfo(String player) {
        System.out.println("You are the " + player + " player, what would you like to do?\n" +
                " (M)ove\n" +
                " (A)ttack\n" +
                " (D)one");
    }


    public void selfInfo(String player) {
        System.out.println(player + " player:");
        System.out.println("-------------");

    }



}