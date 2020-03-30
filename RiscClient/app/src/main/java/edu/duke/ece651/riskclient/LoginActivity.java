package edu.duke.ece651.riskclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import edu.duke.ece651.riskclient.utils.HTTPUtils;

import static edu.duke.ece651.riskclient.Constant.SUCCESSFUL;
import static edu.duke.ece651.riskclient.Constant.USER_NAME;
import static edu.duke.ece651.riskclient.Constant.USER_PASSWORD;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.authUser;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

/**
 * @author xkw
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * UI variable
     */
    private TextInputEditText etName;
    private TextInputEditText etPassWord;

    /**
     * variable
     */
    private String userName;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName = (TextInputEditText) findViewById(R.id.et_name);
        etPassWord = (TextInputEditText) findViewById(R.id.et_password);

        Button btLogin = (Button)findViewById(R.id.bt_login);
        btLogin.setOnClickListener(view -> {
            btLogin.setClickable(false);
            userName = Objects.requireNonNull(etName.getText()).toString().trim();
            userPassword = Objects.requireNonNull(etPassWord.getText()).toString().trim();
            authUser(new Player(userName, userPassword), new onResultListener() {
                @Override
                public void onFailure(String error) {
                    btLogin.setClickable(true);
                    showToastUI(LoginActivity.this, error);
                    Log.e(TAG, error);
                }

                @Override
                public void onSuccessful() {
                    btLogin.setClickable(true);
                    showToastUI(LoginActivity.this, "Login successful.");
                    // login successful
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.LOGIN_NAME, userName);
                    // TODO: pass the user id to Main
                    intent.putExtra(MainActivity.LOGIN_ID, 1);
                    startActivity(intent);
                    // save user name & password
                    saveUserData();
                    // kill login activity(don't need to go back this page
                    finish();
                }
            });
        });

        Button btSignUp = (Button) findViewById(R.id.tbt_signup);
        btSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        loadUserData();
    }

    public void saveUserData(){
        SharedPreferences preferences = getSharedPreferences("login_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_NAME, userName);
        editor.putString(USER_PASSWORD, userPassword);
        editor.apply();
    }

    public void loadUserData(){
        SharedPreferences preferences = getSharedPreferences("login_data", MODE_PRIVATE);
        userName = preferences.getString(USER_NAME, "");
        userPassword = preferences.getString(USER_PASSWORD, "");
        etName.setText(userName);
        etPassWord.setText(userPassword);
    }
}
