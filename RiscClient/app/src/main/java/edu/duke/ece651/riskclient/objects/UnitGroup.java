package edu.duke.ece651.riskclient.objects;

public class UnitGroup {
    private int level;
    private int number;
    private boolean isAllay;

    public UnitGroup(int level, int number, boolean isAllay) {
        this.level = level;
        this.number = number;
        this.isAllay = isAllay;
    }

    public int getLevel() {
        return level;
    }

    public int getNumber() {
        return number;
    }

    public boolean isAllay() {
        return isAllay;
    }
}
