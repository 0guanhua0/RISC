package edu.duke.ece651.riskclient.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import edu.duke.ece651.riskclient.R;

import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class PlayGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Play Game");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                goBack();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goBack(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayGameActivity.this);
        builder.setPositiveButton("Exit", (dialog1, which) -> {
            showToastUI(PlayGameActivity.this, "Exit");
        });
        builder.setNegativeButton("Leave", (dialog2, which) -> {
            showToastUI(PlayGameActivity.this, "Leave");
        });
        builder.setMessage("Do you want to exit the game or exit the game?");
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog12 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(
                            getResources().getColor(R.color.colorPrimary)
                    );
        });
        dialog.show();
        // TODO: communicate with the server, send the exit info
        onBackPressed();
    }
}
