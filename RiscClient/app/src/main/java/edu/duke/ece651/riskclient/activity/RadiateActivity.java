package edu.duke.ece651.riskclient.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.action.RadiateAction;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.adapter.UnitAdapter;
import edu.duke.ece651.riskclient.listener.onResultListener;
import edu.duke.ece651.riskclient.objects.UnitGroup;

import static edu.duke.ece651.risk.shared.Constant.RADIATE_COST;
import static edu.duke.ece651.riskclient.Constant.ACTION_PERFORMED;
import static edu.duke.ece651.riskclient.RiskApplication.getPlayerID;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_CURRENT_PLAYER;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_CURRENT_TECH_LEVEL;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_IS_UPGRADE_MAX;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_PLAYING_MAP;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_TECH_RESOURCE;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.sendAction;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class RadiateActivity extends AppCompatActivity {

    private static final String NONE = "enemy: none";

    private Player<String> player;
    private WorldMap<String> map;
    private int techResource;
    private ArrayAdapter<String> targetTerritoryAdapter;
    // target territory, can't not perform radiate to
    // 1. own territory
    // 2. ally's territory
    List<String> targetTerritories;
    // parameters of the action
    private String targetTerritory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiate);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            map = (WorldMap<String>) bundle.getSerializable(DATA_PLAYING_MAP);
            player = (Player<String>) bundle.getSerializable(DATA_CURRENT_PLAYER);
            techResource = bundle.getInt(DATA_TECH_RESOURCE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Radiate");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        targetTerritories = new ArrayList<>();
        for (Territory territory : map.getAtlas().values()){
            if (territory.getOwner() != player.getId() && !(player.getAlly() != null && territory.getOwner() == player.getAlly().getId())){
                targetTerritories.add(String.format("enemy: %s", territory.getName()));
            }
        }
        if (targetTerritories.size() == 0){
            targetTerritories.add(NONE);
        }

        targetTerritory = "";
        setUpUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpUI(){
        TextView tvResource = findViewById(R.id.tv_resource);
        tvResource.setText(String.format(Locale.US, "Total tech resource(before this action): %d\nThis action will cost you: %d", techResource, RADIATE_COST));

        Button btConfirm = findViewById(R.id.bt_confirm);
        Button btDecline = findViewById(R.id.bt_decline);

        btConfirm.setOnClickListener(v -> {
            if (validateAction()){
                Action action = new RadiateAction(targetTerritory);
                sendAction(action, new onResultListener() {
                    @Override
                    public void onFailure(String error) {
                        // either invalid action or networking problem
                        showToastUI(RadiateActivity.this, error);
                    }

                    @Override
                    public void onSuccessful() {
                        // valid action, return to play game page
                        Intent data = new Intent();
                        data.putExtra(ACTION_PERFORMED, action);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
            }
        });
        btDecline.setOnClickListener(v -> {
            onBackPressed();
        });

        setUpTargetList();
    }

    private void setUpTargetList(){
        TextInputLayout layout = findViewById(R.id.dd_target_territory);
        layout.setHint("Target Territory");

        targetTerritoryAdapter =
                new ArrayAdapter<>(
                        RadiateActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        targetTerritories);

        targetTerritory = targetTerritoryAdapter.getItem(0).split(":")[1].trim();

        AutoCompleteTextView dropdownAction =
                layout.findViewById(R.id.dd_input);
        dropdownAction.setAdapter(targetTerritoryAdapter);
        dropdownAction.setText(targetTerritory, false);
        dropdownAction.setOnItemClickListener((parent, v, position, id) -> {
            targetTerritory = targetTerritoryAdapter.getItem(position).split(":")[1].trim();
            dropdownAction.setText(targetTerritory, false);
        });
    }

    private boolean validateAction(){
        if (targetTerritory.equals("none")){
            showToastUI(RadiateActivity.this, "No such territory");
        }
        return player.getTechNum() >= RADIATE_COST && map.hasTerritory(targetTerritory);
    }
}
