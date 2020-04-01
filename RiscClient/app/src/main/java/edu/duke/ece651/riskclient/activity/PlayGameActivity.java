package edu.duke.ece651.riskclient.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import edu.duke.ece651.riskclient.R;

import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class PlayGameActivity extends AppCompatActivity {
    private static final String ROOM_NAME = "edu.duke.ece651.riskclient.playgame.roomname";

    private String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        // TODO: pass room name inside
        roomName = "Room Name Here";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(roomName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ROOM_NAME, roomName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        roomName = savedInstanceState.getString(ROOM_NAME);
        getSupportActionBar().setTitle(roomName);
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
