package edu.duke.ece651.riskclient.listener;

public interface onRecvAttackResultListener {
    /**
     * receive one attack result
     * @param result attack result
     */
    void onNewResult(String result);

    /**
     * receive all attack result
     */
    void onOver();

    /**
     * error case
     */
    void onFailure(String error);
}
