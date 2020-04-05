package edu.duke.ece651.riskclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.duke.ece651.riskclient.R;

import static edu.duke.ece651.riskclient.RiskApplication.getRoomName;

public class WaitGameActivity extends AppCompatActivity {
    public final static String PLAYER_CNT = "playerCnt";

    private int playerTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_game);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            playerTotal = bundle.getInt(PLAYER_CNT);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(getRoomName());
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
                Intent intent = new Intent(WaitGameActivity.this, SelectTerritoryActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
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
