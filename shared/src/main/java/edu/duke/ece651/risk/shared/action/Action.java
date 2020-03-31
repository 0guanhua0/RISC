package edu.duke.ece651.risk.shared.action;

import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.map.WorldMap;

/**
 * The interface for all actions.
 * Represent the action user perform(e.g. move, attack...).
 */
public interface Action {
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
}
