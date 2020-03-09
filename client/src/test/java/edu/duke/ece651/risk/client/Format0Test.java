package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Format0Test {

    @Test
    void check() {
        Format tmp = new Format0();
        System.out.println(tmp.check("A"));
        System.out.println(tmp.check("B"));
    }
}