package edu.duke.ece651.riskclient;

import android.app.Application;
import android.content.Context;

import edu.duke.ece651.riskclient.objects.Player;

public class RiskApplication extends Application {
    private static final String TAG = RiskApplication.class.getSimpleName();

    private static Context context;
    private Player player;


    @Override
    public void onCreate() {
        super.onCreate();
        RiskApplication.context = getApplicationContext();
    }

    public static Context getContext() {
        return RiskApplication.context;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
