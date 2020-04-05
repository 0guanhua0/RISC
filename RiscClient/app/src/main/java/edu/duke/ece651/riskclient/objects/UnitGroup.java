package edu.duke.ece651.riskclient.objects;

public class UnitGroup {
    private int level;
    private int number;

    public UnitGroup(int level, int number) {
        this.level = level;
        this.number = number;
    }

    public int getLevel() {
        return level;
    }

    public int getNumber() {
        return number;
    }
}
