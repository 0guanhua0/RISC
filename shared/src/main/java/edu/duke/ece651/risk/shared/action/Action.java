package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.io.Serializable;

/**
 * The interface for all actions, it should be serializable(need to transfer between client and server).
 * Represent the action user perform(e.g. move, attack...).
 */
public interface Action extends Serializable {
    /**
     * Check the validation of current action.
     * @return true if valid
     */
    boolean isValid(WorldState worldState);

    /**
     * Perform the action.
     * @return true if perform successful
     */
    boolean perform(WorldState worldState);

    @Override
    String toString();
}
