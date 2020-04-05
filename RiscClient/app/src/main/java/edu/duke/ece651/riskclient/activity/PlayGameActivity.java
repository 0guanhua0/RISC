package edu.duke.ece651.riskclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryV2;
import edu.duke.ece651.risk.shared.map.Unit;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.adapter.TerritoryAdapter;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.listener.onSendListener;

import static edu.duke.ece651.risk.shared.Constant.ACTION_DONE;
import static edu.duke.ece651.risk.shared.Constant.ROUND_OVER;
import static edu.duke.ece651.riskclient.RiskApplication.getRoomName;
import static edu.duke.ece651.riskclient.RiskApplication.recv;
import static edu.duke.ece651.riskclient.RiskApplication.send;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class PlayGameActivity extends AppCompatActivity {
    private static final int ACTION_MOVE_ATTACK = 1;
    private static final int ACTION_UPGRADE = 2;

    private List<Territory> territories;
    private TerritoryAdapter territoryAdapter;

    /**
     * UI variable
     */
    private TextView tvActionInfo;
    private Button btMoveAttack;
    private Button btUpgrade;
    private Button btDone;

    /**
     * Variable
     */
    private List<Action> performedActions;
    private boolean isRoundOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(getRoomName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        performedActions = new ArrayList<>();
        isRoundOver = false;

        setUpUI();
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
        btMoveAttack = findViewById(R.id.bt_move_attack);
        btUpgrade = findViewById(R.id.bt_upgrade);
        btDone = findViewById(R.id.bt_done);

        btMoveAttack.setOnClickListener(v -> {
            Intent intent = new Intent(PlayGameActivity.this, MoveAttackActivity.class);
            startActivityForResult(intent, ACTION_MOVE_ATTACK);
        });

        btUpgrade.setOnClickListener(v -> {
            Intent intent = new Intent(PlayGameActivity.this, UpgradeActivity.class);
            startActivityForResult(intent, ACTION_UPGRADE);
        });

        btDone.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Finish this round");
            builder.setMessage("once you finish this round, you can't undo it");
            builder.setPositiveButton("Finish", (dialog, which) -> {
                roundOver();
            });
            builder.setNegativeButton("Cancel", ((dialog, which) -> {
                // do nothing
            }));
            builder.show();
        });

        tvActionInfo = findViewById(R.id.tv_action_info);
        tvActionInfo.setMovementMethod(new ScrollingMovementMethod());
        showActions();
        setUpTerritoryList();
    }

    private void setUpTerritoryList(){
        RecyclerView rvTerritoryList = findViewById(R.id.rv_territory_list);

        territories = new ArrayList<>();
        for (int i = 0; i < 30; i++){
            territories.add(new TerritoryV2("t" + i, 1, 1, 1));
        }

        territoryAdapter = new TerritoryAdapter();
        territoryAdapter.setListener(position -> {
            showTerritoryDetailDialog(territoryAdapter.getTerritory(position));
        });

        rvTerritoryList.setLayoutManager(new LinearLayoutManager(PlayGameActivity.this));
        rvTerritoryList.setHasFixedSize(true);
        rvTerritoryList.setAdapter(territoryAdapter);

        territoryAdapter.setTerritories(territories);

        tvActionInfo = findViewById(R.id.tv_action_info);
    }

    /**
     * Show the detail information of one territory.
     * @param territory target territory
     */
    private void showTerritoryDetailDialog(Territory territory){
        // generate the detail info of one territory
        StringBuilder detailInfo = new StringBuilder();
        detailInfo.append("Resource Info:\n");
        detailInfo.append("Food yield: ").append(territory.getFoodYield()).append("\n");
        detailInfo.append("Tech yield: ").append(territory.getTechYield()).append("\n");
        detailInfo.append("Units Info:\n");
        Map<Integer, List<Unit>> units = territory.getUnitGroup();
        if (units.isEmpty()){
            detailInfo.append("no units on this territory\n");
        }
        else {
            for (Map.Entry<Integer, List<Unit>> entry : units.entrySet()){
                detailInfo.append(entry.getValue().size()).append(" units with level ").append(entry.getKey()).append("\n");
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detail Info");
        builder.setMessage(detailInfo.toString());
        builder.show();
    }

    /**
     * The function will be executed at the end of each round.
     */
    private void roundOver(){
        isRoundOver = true;
        // disable all buttons
        setAllButtonClickable(false);
        send(ACTION_DONE, new onSendListener() {
            @Override
            public void onFailure(String error) {
                showToastUI(PlayGameActivity.this, "Network problem, please retry.");
                setAllButtonClickable(true);
            }

            @Override
            public void onSuccessful() {
                // successfully indicate we are finish, waiting for the attack result
                receiveAttackResult();
            }
        });
    }

    /**
     * Since the requirement require send the result of each attack action to *all* players,
     * we use a while loop to keep receiving all results until OVER
     */
    private void receiveAttackResult(){
        StringBuilder results = new StringBuilder();
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {

            }

            @Override
            public void onSuccessful(Object object) {
                tvActionInfo.setText("");
                if (object instanceof String){
                    String result = (String) object;
                    // received all attack result, start a new round
                    if (result.equals(ROUND_OVER)){
                        isRoundOver = false;
                        setAllButtonClickable(true);
                        showToastUI(PlayGameActivity.this, "Start new round.");
                    }else {
                        results.append(object);
                        tvActionInfo.setText(results.toString());
                    }
                }
            }
        });
    }

    /**
     * Set the clickable property of all buttons in this page.
     * @param isClickable is clickable or not
     */
    private void setAllButtonClickable(boolean isClickable){
        btMoveAttack.setClickable(isClickable);
        btUpgrade.setClickable(isClickable);
        btDone.setClickable(isClickable);
    }

    /**
     * This function will format and show the list of actions user already successfully performed.
     */
    private void showActions(){
        StringBuilder builder = new StringBuilder();
        builder.append("Actions performed:\n");
        if (performedActions.isEmpty()){
            builder.append("no action performed for now");
        }else {
            for (Action action : performedActions){
                builder.append(action.toString());
            }
        }
        tvActionInfo.setText(builder);
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
