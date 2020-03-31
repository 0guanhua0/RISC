package edu.duke.ece651.riskclient.activity;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

    // probably want to extract this into constant
    private void goBack(){
        // TODO: change text to save & exit
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayGameActivity.this);
        builder.setPositiveButton("Save", (dialog1, which) -> {
            showToastUI(PlayGameActivity.this, "Sava room");
            // TODO: communicate with the server, send the exit info
            onBackPressed();
        });
        builder.setNegativeButton("Exit", (dialog2, which) -> {
            showToastUI(PlayGameActivity.this, "Exit room");
            // TODO: communicate with the server, send the exit info
            onBackPressed();
        });

        builder.setOnCancelListener(dialog -> showToastUI(PlayGameActivity.this, "cancel"));
        builder.setMessage("Do you want to save the game?");
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog12 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(
                            getResources().getColor(R.color.colorPrimary)
                    );
        });
        dialog.show();
    }
}
