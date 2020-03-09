package edu.duke.ece651.risk.client;

public interface Display {
    public void showMap();
}

class Display0 implements Display {
    public void showMap() {
        System.out.println("10 units in Narnia (next to: Elantris, Midkemia)");

    }
}