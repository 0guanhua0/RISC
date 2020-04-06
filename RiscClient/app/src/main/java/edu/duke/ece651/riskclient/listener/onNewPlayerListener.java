package edu.duke.ece651.riskclient.listener;

import edu.duke.ece651.riskclient.objects.SimplePlayer;

public interface onNewPlayerListener {
    /**
     * One new player enter the game.
     * @param player newly entered player
     */
    void onNewPlayer(SimplePlayer player);

    /**
     * all players already join the game
     */
    void onAllPlayer();

    /**
     * error case
     */
    void onFailure(String error);
}
