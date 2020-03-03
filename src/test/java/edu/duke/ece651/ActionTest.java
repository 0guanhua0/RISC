package edu.duke.ece651;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ActionTest {

    @Test
    public void testMoveValid(){
        Action action = new MoveAction("A", "B");
        assertTrue(action.isValid());
        action = new MoveAction("A", "A");
        assertFalse(action.isValid());
    }

    @Test
    public void testMovePerform(){
        // TODO: for now it just calls the method, add real code after implementation
        Action action = new MoveAction("A", "B");
        action.perform();
    }

    @Test
    public void testMoveEqual(){
        MoveAction action1 = new MoveAction("A", "B");
        MoveAction action2 = new MoveAction("A", "B");
        MoveAction action3 = new MoveAction("A", "C");
        assertEquals(action1, action2);
        assertNotEquals(action1, action3);
    }

    @Test
    public void testAttackValid(){
        Action action = new AttackAction("A", "B");
        assertTrue(action.isValid());
        action = new AttackAction("A", "A");
        assertFalse(action.isValid());
    }

    @Test
    public void testAttackPerform(){
        // TODO: for now it just calls the method, add real code after implementation
        Action action = new AttackAction("A", "B");
        action.perform();
    }

    @Test
    public void testAttackEqual(){
        AttackAction action1 = new AttackAction("A", "B");
        AttackAction action2 = new AttackAction("A", "B");
        AttackAction action3 = new AttackAction("A", "C");
        assertEquals(action1, action2);
        assertNotEquals(action1, action3);
    }
} 
