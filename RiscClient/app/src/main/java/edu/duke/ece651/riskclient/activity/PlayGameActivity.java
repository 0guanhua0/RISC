package edu.duke.ece651.riskclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV2;
import edu.duke.ece651.risk.shared.map.Unit;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.adapter.TerritoryAdapter;
import edu.duke.ece651.riskclient.adapter.UnitAdapter;

import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class PlayGameActivity extends AppCompatActivity {
    private static final String ROOM_NAME = "edu.duke.ece651.riskclient.playgame.roomname";
    private static final int ACTION_MOVE_ATTACK = 1;
    private static final int ACTION_UPGRADE = 2;

    private String roomName;
    private List<Territory> territories;
    private TerritoryAdapter territoryAdapter;

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

        setUpUI();
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
        Objects.requireNonNull(getSupportActionBar()).setTitle(roomName);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case ACTION_MOVE_ATTACK:
            case ACTION_UPGRADE:
                if (resultCode == RESULT_OK){
                    // TODO: fetch the data
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpUI(){
        Button btMoveAttack = findViewById(R.id.bt_move_attack);
        Button btUpgrade = findViewById(R.id.bt_upgrade);
        Button btDone = findViewById(R.id.bt_done);

        btMoveAttack.setOnClickListener(v -> {
            Intent intent = new Intent(PlayGameActivity.this, MoveAttackActivity.class);
            startActivityForResult(intent, ACTION_MOVE_ATTACK);
        });

        btUpgrade.setOnClickListener(v -> {
            Intent intent = new Intent(PlayGameActivity.this, UpgradeActivity.class);
            startActivityForResult(intent, ACTION_UPGRADE);
        });

        btDone.setOnClickListener(v -> {
            // once click done, invalid all buttons
            btMoveAttack.setClickable(false);
            btUpgrade.setClickable(false);
            btDone.setClickable(false);
            // TODO: send data to server
            btMoveAttack.setClickable(true);
            btUpgrade.setClickable(true);
            btDone.setClickable(true);
        });

        RecyclerView rvTerritoryList = findViewById(R.id.rv_territory_list);

        territories = new ArrayList<>();
        for (int i = 0; i < 30; i++){
            territories.add(new TerritoryV2("t" + i, 1, 1, 1));
        }

        territoryAdapter = new TerritoryAdapter();
        territoryAdapter.setListener(position -> {
            Territory territory = territories.get(position);
            showToastUI(PlayGameActivity.this, territory.getName());
        });

        rvTerritoryList.setLayoutManager(new LinearLayoutManager(PlayGameActivity.this));
        rvTerritoryList.setHasFixedSize(true);
        rvTerritoryList.setAdapter(territoryAdapter);

        territoryAdapter.setTerritories(territories);
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
