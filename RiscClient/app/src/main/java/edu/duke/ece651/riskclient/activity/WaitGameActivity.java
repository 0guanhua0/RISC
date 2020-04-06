package edu.duke.ece651.riskclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import edu.duke.ece651.risk.shared.RoomInfo;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onNewPlayerListener;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.objects.SimplePlayer;

import static edu.duke.ece651.risk.shared.Constant.PLAYER_ID;
import static edu.duke.ece651.riskclient.RiskApplication.getRoomName;
import static edu.duke.ece651.riskclient.RiskApplication.recv;
import static edu.duke.ece651.riskclient.RiskApplication.setPlayerID;
import static edu.duke.ece651.riskclient.RiskApplication.setRoom;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.waitAllPlayers;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class WaitGameActivity extends AppCompatActivity {
    private final static String TAG = WaitGameActivity.class.getSimpleName();

    private List<TextView> tvPlayers;
    private int playerIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(getRoomName());
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // get the player info first, and then get the room info
        initPlayerInfo();
        getRoomInfo();
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

    private void initPlayerInfo(){
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {

            }

            @Override
            public void onSuccessful(Object object) {
                try {
                    String playerInfo = (String) object;
                    JSONObject jsonObject = new JSONObject(playerInfo);
                    setPlayerID(jsonObject.optInt(PLAYER_ID, 1));
                }catch (JSONException e){
                    Log.e(TAG, "initPlayerInfo: " + e.toString());
                }
            }
        });
    }

    // each time enter this activity, should fetch the latest room info from the server
    private void getRoomInfo(){
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {

            }

            @Override
            public void onSuccessful(Object object) {
                runOnUiThread(() -> {
                    RoomInfo info = (RoomInfo) object;
                    setRoom(info);
                    showToastUI(WaitGameActivity.this, "player total " + info.getPlayerNeedTotal());
                    tvPlayers = new ArrayList<>();
                    // dynamically initialize a list of player
                    LinearLayout llPlayer = findViewById(R.id.ll_player);
                    for (int i = 0; i < info.getPlayerNeedTotal(); i ++){
                        View playerView = getLayoutInflater().inflate(R.layout.listitem_player, llPlayer, false);
                        // store the text view
                        tvPlayers.add(playerView.findViewById(R.id.tv_player_name));
                        llPlayer.addView(playerView);
                    }

                    playerIn = 0;
                    for (String playerName : info.getPlayerNames()){
                        tvPlayers.get(playerIn).setText(String.format(Locale.US, "player %d: %s", playerIn + 1, playerName));
                        playerIn++;
                    }
                    // wait for other players only after initialize the UI
                    waitPlayers();
                });
            }
        });
    }

    private void waitPlayers(){
        // wait for other players
        waitAllPlayers(new onNewPlayerListener() {
            @Override
            public void onNewPlayer(SimplePlayer player) {
                runOnUiThread(() -> {
                    addPlayer(player.getName());
                });
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
