package edu.duke.ece651.riskclient.listener;

/**
 * This listener is used to receive continues info stream(in String) which will have a terminate sign.
 */
public interface onRecvInfoListener {
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
