package edu.duke.ece651.riskclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import static edu.duke.ece651.riskclient.Constant.SUCCESSFUL;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.addUser;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_signup);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextInputEditText etName = findViewById(R.id.et_signup_name);
        TextInputEditText etPassword = findViewById(R.id.et_signup_password);

        Button signUp = findViewById(R.id.bt_signup);
        signUp.setOnClickListener(view -> {
            String name = Objects.requireNonNull(etName.getText()).toString();
            String password = Objects.requireNonNull(etPassword.getText()).toString();
            String result = addUser(new Player(name, password));
            showToastUI(SignUpActivity.this, result);
            if (result.equals(SUCCESSFUL)){
                // sign successful
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }else {
                // failure
                showToastUI(SignUpActivity.this, result);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
