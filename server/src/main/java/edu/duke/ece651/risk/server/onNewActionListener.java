package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.action.Action;

public interface onNewActionListener {
    /**
     * <playerName> perform an <action>
     * @param playerName player name
     * @param action action performed
     */
    void newAction(String playerName, Action action);

    /**
     * <playerName> finish his/her round
     * @param playerName player name
     */
    void finishRound(String playerName);
}
