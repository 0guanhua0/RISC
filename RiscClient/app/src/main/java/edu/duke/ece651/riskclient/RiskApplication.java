package edu.duke.ece651.riskclient;

import android.app.Application;
import android.content.Context;

public class RiskApplication extends Application {
    private static final String TAG = RiskApplication.class.getSimpleName();

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        RiskApplication.context = getApplicationContext();
    }

    public static Context getContext() {
        return RiskApplication.context;
    }

}
