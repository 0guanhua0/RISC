package edu.duke.ece651.riskclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

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
    private Button btLogin;

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

        btLogin = (Button)findViewById(R.id.bt_login);
        btLogin.setOnClickListener(listener -> {
            btLogin.setClickable(false);
            userName = Objects.requireNonNull(etName.getText()).toString().trim();
            userPassword = Objects.requireNonNull(etPassWord.getText()).toString().trim();
            // TODO: use socket to authenticate user
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            intent.putExtra(MainActivity.LOGIN_NAME, userName);
//            intent.putExtra(MainActivity.LOGIN_POSITION, userPosition);
//            intent.putExtra(MainActivity.LOGIN_PERMISSION, userPermission);
//            startActivity(intent);
//              saveData();
//            finish();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });

        loadUserData();
    }

    public void saveUserData(){
        SharedPreferences preferences = getSharedPreferences("login_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", userName);
        editor.putString("password", userPassword);
        editor.apply();
    }

    public void loadUserData(){
        SharedPreferences preferences = getSharedPreferences("login_data", MODE_PRIVATE);
        userName = preferences.getString("name", "");
        userPassword = preferences.getString("password", "");
        etName.setText(userName);
        etPassWord.setText(userPassword);
    }
}
