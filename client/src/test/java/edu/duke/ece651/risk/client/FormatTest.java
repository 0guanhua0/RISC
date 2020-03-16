package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class FormatTest {

    @Test
    void isNumeric() {
        assertFalse(Format.isNumeric(null));
        assert ( Format.isNumeric("0") );
        assert ( !Format.isNumeric("A") );
    }
}