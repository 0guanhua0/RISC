package edu.duke.ece651.risk.shared;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeserializerTest { 

    @Test
    public void testDeserializeActions() {
        HashMap<String, List<Action>> actions = new HashMap<>();
        List<Action> moveActions = new ArrayList<>();
        List<Action> attackActions = new ArrayList<>();

        moveActions.add(new MoveAction("A", "B"));
        moveActions.add(new MoveAction("C", "F"));
        moveActions.add(new MoveAction("R", "y"));
        attackActions.add(new AttackAction("A", "B"));

        actions.put("move", moveActions);
        actions.put("attack", attackActions);

        assertEquals(actions, Deserializer.deserializeActions(new Gson().toJson(actions)));
    }

} 
