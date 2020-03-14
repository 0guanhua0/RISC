package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatTest {

    @Test
    void isNumeric() {
        Format.isNumeric("0");
        Format.isNumeric("A");
    }
}