package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstructionTest {

    @Test
    void actInfo() {
        Instruction ins = new Instruction0();
        ins.selfInfo("A");
        ins.actInfo("A");
    }

    
}