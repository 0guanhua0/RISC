package edu.duke.ece651.risk.client;

class Format0 implements Format {
    public static Boolean check(String action) {
        return (action.equals("M") || action.equals("A") || action.equals("D"));
    }
}