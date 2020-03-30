package edu.duke.ece651.riskclient.utils;

import android.app.Activity;
import android.widget.Toast;

import static edu.duke.ece651.riskclient.RiskApplication.getContext;

/**
 * @author xkw
 */
public class UIUtils {

    public static void showToast(String toastMessage){
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    public static void showToastUI(Activity activity, String toastMessage){
        activity.runOnUiThread(() -> showToast(toastMessage));
    }
}
