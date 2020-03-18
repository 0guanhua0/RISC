package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormatTest {

    @Test
    void isNumeric() {
        assertFalse(Format.isNumeric(null));
        assertTrue(Format.isNumeric("10"));
        assertTrue(Format.isNumeric("0"));
        assertFalse(Format.isNumeric("A"));
    }
}