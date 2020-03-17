package edu.duke.ece651.risk.client;

/**
 * store system instruction
 */
class InsPrompt {

    public static void actInfo(String player) {
        System.out.println( "You are the " + player + " player, what would you like to do?\n" +
                " (M)ove\n" +
                " (A)ttack\n" +
                " (D)one\n" +
                " (Q)uit");
    }


    public static void selfInfo(String player) {
        System.out.println(player + " player:");
        System.out.println("-------------");

    }

    public static void srcInfo() {
        System.out.println("input source territory");
    }

    public static void dstInfo() {
        System.out.println("input destination territory");
    }

    public static void unitInfo() {
        System.out.println("input unit number");
    }


}