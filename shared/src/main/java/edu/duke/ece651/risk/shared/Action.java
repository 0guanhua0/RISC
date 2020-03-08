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

    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
