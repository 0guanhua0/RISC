package edu.duke.ece651.riskclient.listener;

public interface onSendListener {
    /**
     * Error happen when sendign the message.
     * @param error error message
     */
    void onFailure(String error);

    /**
     * Successfully send the message.
     */
    void onSuccessful();
}