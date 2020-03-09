package edu.duke.ece651.risk.client;

public interface Format {
    public Boolean check(String action);
}

class Format0 implements Format {
    public Boolean check(String action) {
        return (action.equals("M") || action.equals("A") || action.equals("D"));
    }
}