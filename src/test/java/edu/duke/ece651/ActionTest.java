package edu.duke.ece651;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ActionTest {

    @Test
    public void testConstructor(){
        Action action = new Action("attack", "A", "B");
        assertEquals("attack", action.getActionType());
        assertEquals("A", action.getSrc());
        assertEquals("B", action.getDest());
    }

    @Test
    public void testGetSetActionType() {
        Action action = new Action();
        action.setActionType("move");
        assertEquals("move", action.getActionType());
    }
    
    @Test
    public void testGetSetSrc() {
        Action action = new Action();
        action.setSrc("territory A");
        assertEquals("territory A", action.getSrc());
    }
    
    @Test
    public void testGetSetDest() {
        Action action = new Action();
        action.setDest("territory B");
        assertEquals("territory B", action.getDest());
    }
    
    @Test
    public void testMain() { 
        
    }
    

} 
