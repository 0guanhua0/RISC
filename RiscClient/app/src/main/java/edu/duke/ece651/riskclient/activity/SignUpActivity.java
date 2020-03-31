package edu.duke.ece651.riskclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import edu.duke.ece651.riskclient.objects.Player;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onResultListener;

import static edu.duke.ece651.riskclient.utils.HTTPUtils.addUser;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Sign Up");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextInputEditText etName = findViewById(R.id.et_signup_name);
        TextInputEditText etPassword = findViewById(R.id.et_signup_password1);
        TextInputEditText etPassword2 = findViewById(R.id.et_signup_password2);

        Button signUp = findViewById(R.id.bt_signup);
        signUp.setOnClickListener(view -> {

            // maybe add a progress dialog here
            /*ProgressDialog dialog = ProgressDialog.show(this, "Info", "signing up", false, true, dialog1 -> {
                // on cancel listener
                signUp.setClickable(true);
            });
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (dialog.isShowing()){
                        dialog.cancel();
                    }
                    showToastUI(SignUpActivity.this, "close in timer");
                }
            }, 8000);*/
            signUp.setClickable(false);
            String name = Objects.requireNonNull(etName.getText()).toString();
            String password = Objects.requireNonNull(etPassword.getText()).toString();
            String password2 = Objects.requireNonNull(etPassword2.getText()).toString();
            if (password.equals(password2)){
                addUser(new Player(name, password), new onResultListener() {
                    @Override
                    public void onFailure(String error) {
                        signUp.setClickable(true);
                        showToastUI(SignUpActivity.this, error);
                    }

                    @Override
                    public void onSuccessful() {
                        signUp.setClickable(true);
                        showToastUI(SignUpActivity.this, "Sign up successful.");
                        // sign up successful
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
            }else {
                showToastUI(SignUpActivity.this, "Two password mismatch.");
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
