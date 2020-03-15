package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

class FormatTest {

    @Test
    void isNumeric() {
        assert ( Format.isNumeric("0") );
        assert ( !Format.isNumeric("A") );
    }
}