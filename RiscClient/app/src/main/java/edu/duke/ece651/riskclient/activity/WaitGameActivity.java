package edu.duke.ece651.riskclient.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.duke.ece651.riskclient.R;

public class WaitGameActivity extends AppCompatActivity {

    private String roomName;
    private int playerTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_game);

        // TODO: should pass the player number into this activity
        playerTotal = new Random(System.currentTimeMillis()).nextInt(5) + 1;
        roomName = "room1";

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(roomName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayout llPlayer = findViewById(R.id.ll_player);

        for (int i = 0; i < playerTotal; i ++){
            View playerView = getLayoutInflater().inflate(R.layout.listitem_player, llPlayer, false);
            llPlayer.addView(playerView);
        }

        // TODO: remove this testing code
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(WaitGameActivity.this, PlayGameActivity.class);
                startActivity(intent);
            }
        }, 100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // TODO: if the room owner want to cancel the room
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
