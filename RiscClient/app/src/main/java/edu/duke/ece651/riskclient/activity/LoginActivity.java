package edu.duke.ece651.riskclient.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import edu.duke.ece651.riskclient.objects.Player;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onResultListener;

import static edu.duke.ece651.riskclient.Constant.USER_NAME;
import static edu.duke.ece651.riskclient.Constant.USER_PASSWORD;
import static edu.duke.ece651.riskclient.RiskApplication.setPlayer;
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
    TextInputLayout tilName;
    TextInputLayout tilPassword;
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

        setUpUI();

        loadUserData();
    }

    private void setUpUI(){
        setUpEditText();
        setUpButton();
    }

    private void setUpEditText(){
        tilName = findViewById(R.id.til_name);
        tilPassword = findViewById(R.id.til_password);

        etName = findViewById(R.id.et_name);
        etPassWord = findViewById(R.id.et_password);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setUpButton(){
        Button btLogin = (Button)findViewById(R.id.bt_login);
        btLogin.setOnClickListener(view -> {
            userName = Objects.requireNonNull(etName.getText()).toString().trim();
            userPassword = Objects.requireNonNull(etPassWord.getText()).toString().trim();

            if (userName.isEmpty()){
                tilName.setError("User name can't be empty");
                return;
            }
            if (userPassword.isEmpty()){
                tilPassword.setError("Password can't be empty");
                return;
            }

            // make the button un-clickable(prevent multiple request)
            btLogin.setClickable(false);

            authUser(new Player(userName, userPassword), new onResultListener() {
                @Override
                public void onFailure(String error) {
                    btLogin.setClickable(true);
                    showToastUI(LoginActivity.this, error);
                    Log.e(TAG, error);
                }

                @Override
                public void onSuccessful(Object o) {
                    btLogin.setClickable(true);
                    showToastUI(LoginActivity.this, "Login successful.");
                    // initialize and set the global player object(only after successfully verify)
                    Player player = new Player(1, userName, userPassword);
                    setPlayer(player);
                    // switch to MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
    }

    private void saveUserData(){
        SharedPreferences preferences = getSharedPreferences("login_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_NAME, userName);
        editor.putString(USER_PASSWORD, userPassword);
        editor.apply();
    }

    private void loadUserData(){
        SharedPreferences preferences = getSharedPreferences("login_data", MODE_PRIVATE);
        userName = preferences.getString(USER_NAME, "");
        userPassword = preferences.getString(USER_PASSWORD, "");
        etName.setText(userName);
        etPassWord.setText(userPassword);
    }
}
