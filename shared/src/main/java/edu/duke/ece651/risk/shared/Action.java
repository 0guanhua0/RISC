package edu.duke.ece651.risk.shared;

/**
 * The interface for all actions.
 * Represent the action user perform(e.g. move, attack...).
 */
public interface Action {
    /**
     * Check the validation of current action.
     * @return true if valid
     */
    boolean isValid();

    /**
     * Perform the action.
     */
    void perform();
<<<<<<< HEAD

=======
>>>>>>> 8e74d9de8c2d1b8aa837937bd7e8c689b6a99f54
}
