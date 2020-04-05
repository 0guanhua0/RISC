package edu.duke.ece651.riskclient.listener;

public interface onReceiveListener {
    /**
     * Error happens when receiving.
     * @param error error message
     */
    void onFailure(String error);

    /**
     * Successfully receive the object.
     * @param object received object
     */
    void onSuccessful(Object object);
}