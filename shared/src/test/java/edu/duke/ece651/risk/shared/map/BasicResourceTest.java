package edu.duke.ece651.risk.shared.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicResourceTest {

    @Test
    void testConstructor(){
        BasicResource basicResource = new BasicResource();
        assertEquals(0,basicResource.getRemain());
        BasicResource basicResource1 = new BasicResource(17);
        assertEquals(basicResource1.getRemain(),17);
    }

    @Test
    void addResource() {
        BasicResource basicResource = new BasicResource(12);
        basicResource.addResource(13);
        assertEquals(25,basicResource.getRemain());

    }

    @Test
    void useResource() {
        BasicResource basicResource = new BasicResource(20);
        basicResource.useResource(3);
        assertEquals(basicResource.getRemain(),17);
        assertThrows(IllegalArgumentException.class,()->{basicResource.useResource(18);});
    }
}