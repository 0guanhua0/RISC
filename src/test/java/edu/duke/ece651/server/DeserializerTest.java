package edu.duke.ece651.server;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import edu.duke.ece651.Action;
import edu.duke.ece651.AttackAction;
import edu.duke.ece651.MoveAction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
