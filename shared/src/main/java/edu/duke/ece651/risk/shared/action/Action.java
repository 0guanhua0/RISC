package edu.duke.ece651.risk.shared.action;

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
    boolean isValid(WorldMap<?> worldMap);

    /**
     * Perform the action.
     * @return true if perform successful
     */
    boolean perform(WorldMap<?> worldMap);
}
