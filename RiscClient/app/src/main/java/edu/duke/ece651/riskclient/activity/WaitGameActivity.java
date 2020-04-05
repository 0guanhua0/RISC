package edu.duke.ece651.riskclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onNewPlayerListener;
import edu.duke.ece651.riskclient.objects.Player;

import static edu.duke.ece651.riskclient.RiskApplication.getPlayerName;
import static edu.duke.ece651.riskclient.RiskApplication.getRoomName;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.waitAllPlayers;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class WaitGameActivity extends AppCompatActivity {
    public final static String PLAYER_CNT = "playerCnt";

    private List<TextView> tvPlayers;
    private int playerTotal;
    private int playerIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_game);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            playerTotal = bundle.getInt(PLAYER_CNT);
            showToastUI(WaitGameActivity.this, "total player " + playerTotal);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(getRoomName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvPlayers = new ArrayList<>();
        // dynamically initialize a list of player
        LinearLayout llPlayer = findViewById(R.id.ll_player);
        for (int i = 0; i < playerTotal; i ++){
            View playerView = getLayoutInflater().inflate(R.layout.listitem_player, llPlayer, false);
            // store the text view
            tvPlayers.add(playerView.findViewById(R.id.tv_player_name));
            llPlayer.addView(playerView);
        }

        // no player in yet
        playerIn = 0;
        // add current player into the room
        addPlayer(getPlayerName());
        // wait for other players
        waitAllPlayers(new onNewPlayerListener() {
            @Override
            public void onNewPlayer(Player player) {
                addPlayer(player.getName());
            }

            @Override
            public void onAllPlayer() {
                showToastUI(WaitGameActivity.this, "All players enter the room, the game will start shortly.");
                delayAndStartActivity();
            }

            @Override
            public void onFailure(String error) {

            }
        });
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

    private void addPlayer(String name){
        showToastUI(WaitGameActivity.this, name + " join the game");
        tvPlayers.get(playerIn).setText(String.format(Locale.US, "player %d: %s", playerIn + 1, name));
        playerIn++;
    }

    private void delayAndStartActivity(){
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
}
