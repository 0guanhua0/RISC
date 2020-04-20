package edu.duke.ece651.riskclient.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.SpyAction;
import edu.duke.ece651.risk.shared.action.UpMaxTechAction;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.SPlayer;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.adapter.UnitAdapter;
import edu.duke.ece651.riskclient.listener.onSpyListener;
import edu.duke.ece651.riskclient.objects.UnitGroup;

import static edu.duke.ece651.risk.shared.Constant.SPY_COST;
import static edu.duke.ece651.riskclient.Constant.ACTION_PERFORMED;
import static edu.duke.ece651.riskclient.RiskApplication.getPlayerName;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_TECH_RESOURCE;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.sendSpyAction;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class SpyActivity extends AppCompatActivity {

    private TextView tvSpyResult;

    private int techResource;
    private Player<String> currentPlayer;
    private String spyTargetName;
    private List<String> playerNames;
    private Map<String, Integer> nameToID;
    private SpyAction action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spy);

        playerNames = new ArrayList<>();
        nameToID = new HashMap<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            techResource = bundle.getInt(DATA_TECH_RESOURCE);
            currentPlayer = (Player<String>) bundle.getSerializable(PlayGameActivity.DATA_CURRENT_PLAYER);
            ArrayList<SPlayer> players = (ArrayList<SPlayer>) bundle.get(PlayGameActivity.DATA_ALL_PLAYERS);
            if (players != null){
                for (SPlayer player : players){
                    // exclude sender himself
                    if (!player.getName().equals(currentPlayer.getName())){
                        playerNames.add(player.getName());
                        nameToID.put(player.getName(), player.getId());
                    }
                }
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Spy");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setUpUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpUI() {
        tvSpyResult = findViewById(R.id.tv_spy_result);
        tvSpyResult.setMovementMethod(new ScrollingMovementMethod());

        TextView tvResource = findViewById(R.id.tv_resource);
        tvResource.setText(String.format(Locale.US, "Total tech resource(before this action): %d", techResource));

        setUpPlayerDropdown();

        Button btConfirm = findViewById(R.id.bt_confirm);
        Button btDecline = findViewById(R.id.bt_decline);

        btConfirm.setOnClickListener(v -> {
            if (validateAction()) {
                SpyAction tmpAction = new SpyAction(nameToID.get(spyTargetName));

                sendSpyAction(tmpAction, new onSpyListener() {
                    @Override
                    public void onSpyResult(List<Action> actions) {
                        runOnUiThread(() -> {
                            StringBuilder result = new StringBuilder();
                            result.append(String.format("All actions player %s performed(until now).\n", spyTargetName));
                            int index = 1;
                            for (Action action : actions){
                                result.append(index).append(". ").append(actionToString(action)).append("\n");
                                index++;
                            }
                            tvSpyResult.setText(result.toString());
                        });
                    }

                    @Override
                    public void onSuccessful() {
                        showToastUI(SpyActivity.this, String.format("That's all actions player %s performed.", spyTargetName));
                        action = tmpAction;
                    }

                    @Override
                    public void onFailure(String error) {
                        // either invalid action or networking problem
                        showToastUI(SpyActivity.this, error);
                    }
                });
            }
        });
        btDecline.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setUpPlayerDropdown(){
        // setup drop down
        TextInputLayout layout = findViewById(R.id.dropdown_layout);
        layout.setHint("Spy target");
        ArrayAdapter<String> playerAdapter =
                new ArrayAdapter<>(
                        SpyActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        new ArrayList<>(playerNames));

        spyTargetName = playerAdapter.getItem(0);

        AutoCompleteTextView dropdownPlayerName =
                layout.findViewById(R.id.dd_input);
        dropdownPlayerName.setAdapter(playerAdapter);
        dropdownPlayerName.setText(spyTargetName, false);
        dropdownPlayerName.setOnItemClickListener((parent, v, position, id) -> {
            spyTargetName = playerAdapter.getItem(position);
        });
    }

    private boolean validateAction() {
        if (!currentPlayer.isSpying() && techResource < SPY_COST){
            showToastUI(SpyActivity.this, "You don't have enough resource.");
            return false;
        }
        return true;
    }

    private String actionToString(Action action){
        if (action instanceof UpMaxTechAction){
            return String.format("Player %s upgrade his/her max tech level", spyTargetName);
        }else {
            return action.toString();
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(ACTION_PERFORMED, action);
        setResult(RESULT_OK, data);
        finish();
    }
}
