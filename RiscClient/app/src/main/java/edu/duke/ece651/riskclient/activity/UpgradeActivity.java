package edu.duke.ece651.riskclient.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.UpMaxTechAction;
import edu.duke.ece651.risk.shared.action.UpUnitAction;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.Unit;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.adapter.UnitAdapter;
import edu.duke.ece651.riskclient.listener.onResultListener;
import edu.duke.ece651.riskclient.objects.UnitGroup;

import static edu.duke.ece651.risk.shared.Constant.TECH_MAP;
import static edu.duke.ece651.risk.shared.Constant.UP_UNIT_COST;
import static edu.duke.ece651.riskclient.Constant.ACTION_PERFORMED;
import static edu.duke.ece651.riskclient.RiskApplication.getPlayerID;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_CURRENT_TECH_LEVEL;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_IS_UPGRADE_MAX;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_PLAYING_MAP;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_TECH_RESOURCE;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.sendAction;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class UpgradeActivity extends AppCompatActivity {
    private final static String TAG = UpgradeActivity.class.getSimpleName();

    private TextView tvTotalCost;

    private UnitAdapter srcUnitAdapter;
    private ArrayAdapter<String> srcTerritoryAdapter;
    private ArrayAdapter<String> fromLevelAdapter;
    private ArrayAdapter<String> toLevelAdapter;
    private AutoCompleteTextView dropdownSrcTerritory;
    private WorldMap<String> map;
    private int techResource;
    private int currentTechLevel;
    private boolean isUpgradeMax;
    // action parameters
    private String srcTerritory;
    private int unitLevelFrom;
    private int unitLevelTo;
    private int unitNum;

    List<String> territoryOwn;
    // levels of all units in this territory
    List<String> territoryUnitLevel;
    // the max number of any level of units in this territory
    List<String> territoryUnitNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            map = (WorldMap<String>) bundle.getSerializable(DATA_PLAYING_MAP);
            techResource = bundle.getInt(DATA_TECH_RESOURCE);
            isUpgradeMax = bundle.getBoolean(DATA_IS_UPGRADE_MAX);
            currentTechLevel = bundle.getInt(DATA_CURRENT_TECH_LEVEL);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Upgrade");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // get all territory own
        territoryOwn = new ArrayList<>();
        List<Territory> territories = new ArrayList<>(map.getAtlas().values());
        for (Territory territory : territories){
            if (territory.getOwner() == getPlayerID()){
                territoryOwn.add(territory.getName());
            }
        }

        unitLevelFrom = 0;
        unitLevelTo = 0;
        unitNum = 0;

        // setup default value
        srcTerritory = territoryOwn.get(0);
        territoryUnitLevel = new ArrayList<>();
        territoryUnitNumber = new ArrayList<>();

        updateUnitDropDown();

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
        tvResource.setText(String.format(Locale.US, "Total tech resource(before this action): %d", techResource));

        // decide to show which part of the layout
        ConstraintLayout layoutUpgradeUnit = findViewById(R.id.layout_upgrade_unit);
        ConstraintLayout layoutUpgradeMax = findViewById(R.id.layout_upgrade_max);
        if (isUpgradeMax){
            layoutUpgradeUnit.setVisibility(View.GONE);
            layoutUpgradeMax.setVisibility(View.VISIBLE);
            setupUpgradeMax();
        }else {
            layoutUpgradeUnit.setVisibility(View.VISIBLE);
            layoutUpgradeMax.setVisibility(View.GONE);
            setUpUpgradeUnit();
        }

        Button btConfirm = findViewById(R.id.bt_confirm);
        Button btDecline = findViewById(R.id.bt_decline);

        btConfirm.setOnClickListener(v -> {
            if (isUpgradeMax){
                UpMaxTechAction action = new UpMaxTechAction();
                sendActionToValid(action);
            }else {
                if (validateAction()){
                    UpUnitAction action = new UpUnitAction(
                            srcTerritory,
                            unitLevelFrom,
                            unitLevelTo,
                            getPlayerID(),
                            unitNum
                    );
                    sendActionToValid(action);
                }
            }
        });
        btDecline.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setUpUpgradeUnit(){
        tvTotalCost = findViewById(R.id.tv_total_cost);
        tvTotalCost.setText("");
        setUpSrcTerritory();
        setUpFromLevelDropdown();
        setUpToLevelDropdown();
        setUpNumberDropdown();
        updateTotalCost();
    }

    private void setupUpgradeMax(){
        TextView tvCurrentLevel = findViewById(R.id.tv_current_level);
        tvCurrentLevel.setText(String.format(Locale.US, "Current tech level: %d", currentTechLevel));

        TextView tvCostToNext = findViewById(R.id.tv_cost_to_next);
        tvCostToNext.setText(String.format(Locale.US, "Cost to upgrade to next: %d", TECH_MAP.get(currentTechLevel)));
    }

    private void setUpSrcTerritory(){
        // find the parent view of territory-unit-list
        View view = findViewById(R.id.src_territory);
        // setup drop down
        TextInputLayout layout = view.findViewById(R.id.dropdown_layout);
        layout.setHint("Territory");
        srcTerritoryAdapter =
                new ArrayAdapter<>(
                        UpgradeActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        territoryOwn);

        srcTerritory = srcTerritoryAdapter.getItem(0);

        dropdownSrcTerritory =
                view.findViewById(R.id.dd_input);
        dropdownSrcTerritory.setAdapter(srcTerritoryAdapter);
        dropdownSrcTerritory.setText(srcTerritory, false);
        dropdownSrcTerritory.setOnItemClickListener((parent, v, position, id) -> {
            srcTerritory = srcTerritoryAdapter.getItem(position);
            updateUnitList();
            updateUnitDropDown();
        });

        // setup recycler view
        RecyclerView rvUnitList = view.findViewById(R.id.rv_unit_list);

        srcUnitAdapter = new UnitAdapter();
        srcUnitAdapter.setListener(position -> {
            UnitGroup unit = srcUnitAdapter.getUnitGroup(position);
        });
        rvUnitList.setLayoutManager(new LinearLayoutManager(UpgradeActivity.this));
        rvUnitList.setHasFixedSize(true);
        rvUnitList.setAdapter(srcUnitAdapter);
        // set default unit list
        updateUnitList();
    }

    private void setUpFromLevelDropdown(){
        TextInputLayout layout = findViewById(R.id.dd_unit_level_from);
        layout.setHint("From Level");

        fromLevelAdapter =
                new ArrayAdapter<>(
                        UpgradeActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        territoryUnitLevel);

        unitLevelFrom = Integer.parseInt(fromLevelAdapter.getItem(0));

        AutoCompleteTextView dropdownAction =
                layout.findViewById(R.id.dd_input);
        dropdownAction.setAdapter(fromLevelAdapter);
        dropdownAction.setText(fromLevelAdapter.getItem(0), false);
        dropdownAction.setOnItemClickListener((parent, v, position, id) -> {
            unitLevelFrom = Integer.parseInt(fromLevelAdapter.getItem(position));
            updateTotalCost();
        });
    }

    private void setUpToLevelDropdown(){
        TextInputLayout layout = findViewById(R.id.dd_unit_level_to);
        layout.setHint("To Level");

        List<String> toLevel = new ArrayList<>();
        // the max to level will be constraint by current tech level
        for (int i = unitLevelFrom + 1; i <= currentTechLevel; i++){
            toLevel.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        UpgradeActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        toLevel);

        unitLevelTo = Integer.parseInt(adapter.getItem(0));

        AutoCompleteTextView dropdownAction =
                layout.findViewById(R.id.dd_input);
        dropdownAction.setAdapter(adapter);
        dropdownAction.setText(adapter.getItem(0), false);
        dropdownAction.setOnItemClickListener((parent, v, position, id) -> {
            unitLevelTo = Integer.parseInt(adapter.getItem(position));
            updateTotalCost();
        });
    }

    private void setUpNumberDropdown(){
        TextInputLayout layout = findViewById(R.id.dd_unit_number);
        layout.setHint("Number of Units");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        UpgradeActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        territoryUnitNumber);

        unitNum = Integer.parseInt(adapter.getItem(0));

        AutoCompleteTextView dropdownAction =
                layout.findViewById(R.id.dd_input);
        dropdownAction.setAdapter(adapter);
        dropdownAction.setText(adapter.getItem(0), false);
        dropdownAction.setOnItemClickListener((parent, v, position, id) -> {
            unitNum = Integer.parseInt(adapter.getItem(position));
            updateTotalCost();
        });
    }

    private void updateUnitList(){
        Territory t = map.getTerritory(srcTerritory);
        List<UnitGroup> unitGroups = new ArrayList<>();
        for (Map.Entry<Integer, List<Unit>> entry : t.getUnitGroup().entrySet()){
            unitGroups.add(new UnitGroup(entry.getKey(), entry.getValue().size()));
        }
        srcUnitAdapter.setUnits(unitGroups);
    }

    /**
     * This function will update the drop down content of from & to level, and number of units
     */
    private void updateUnitDropDown(){
        // clear the old data
        territoryUnitLevel.clear();
        territoryUnitNumber.clear();
        // add new one
        Territory t = map.getTerritory(srcTerritory);
        int maxNumber = 0;
        for (Map.Entry<Integer, List<Unit>> entry : t.getUnitGroup().entrySet()){
            territoryUnitLevel.add(String.valueOf(entry.getKey()));
            maxNumber = Math.max(entry.getValue().size(), maxNumber);
        }
        territoryUnitLevel.sort(String::compareTo);
        for (int i = 1; i <= maxNumber; i++){
            territoryUnitNumber.add(String.valueOf(i));
        }
    }

    private void updateTotalCost() {
        int delta = UP_UNIT_COST.get(unitLevelTo) - UP_UNIT_COST.get(unitLevelFrom);
        int total = delta * unitNum;
        tvTotalCost.setText(String.valueOf(total));
    }

    private <T extends Action & Serializable> void sendActionToValid(T action){
        sendAction(action, new onResultListener() {
            @Override
            public void onFailure(String error) {
                // either invalid action or networking problem
                showToastUI(UpgradeActivity.this, error);
                Log.e(TAG, "sendAction: " + error);
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

    private boolean validateAction(){
        if (unitLevelTo <= unitLevelFrom){
            showToastUI(UpgradeActivity.this, "You can't downgrade(or unchange) the unit.");
            return false;
        }
        Territory t = map.getTerritory(srcTerritory);
        if (!t.getUnitGroup().containsKey(unitLevelFrom)){
            return false;
        }else {
            return unitNum <= Objects.requireNonNull(t.getUnitGroup().get(unitLevelFrom)).size();
        }
    }
}
