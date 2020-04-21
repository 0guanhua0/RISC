package edu.duke.ece651.riskclient.listener;

import java.util.List;

import edu.duke.ece651.risk.shared.action.Action;

public interface onSpyListener {
    /**
     * receive the spy result
     * @param result spy result
     */
    void onSpyResult(List<Action> result);

    /**
     * successfully perform the action
     */
    void onSuccessful();

    /**
     * error case
     */
    void onFailure(String error);
}
