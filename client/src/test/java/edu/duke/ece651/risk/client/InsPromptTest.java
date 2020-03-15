package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsPromptTest {

    @Test
    void Info() {
        InsPrompt.actInfo("A");
        InsPrompt.selfInfo("A");
        InsPrompt.srcInfo();
        InsPrompt.dstInfo();
        InsPrompt.unitInfo();
    }
}