package edu.duke.ece651.risk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstructionTest {

    @Test
    void actInfo() {
        Instruction.actInfo("A");
        Instruction.selfInfo("B");

        Instruction.srcInfo();
        Instruction.dstInfo();
        Instruction.unitInfo();
    }
}