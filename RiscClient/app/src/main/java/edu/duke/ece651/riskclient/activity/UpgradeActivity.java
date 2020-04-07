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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import static edu.duke.ece651.risk.shared.Constant.UP_UNIT_COST;
import static edu.duke.ece651.riskclient.Constant.ACTION_PERFORMED;
import static edu.duke.ece651.riskclient.RiskApplication.getPlayerID;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.PLAYING_MAP;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.TECH_RESOURCE;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.sendAction;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class UpgradeActivity extends AppCompatActivity {
    private final static String TAG = UpgradeActivity.class.getSimpleName();

    private TextView tvTotalCost;

    private UnitAdapter srcUnitAdapter;
    private ArrayAdapter<String> srcTerritoryAdapter;
    private AutoCompleteTextView dropdownSrcTerritory;
    private WorldMap<String> map;
    private int techResource;
    // action parameters
    private String srcTerritory;
    private int unitLevelFrom;
    private int unitLevelTo;
    private int unitNum;

    List<String> territoryOwn;
    List<String> territoryUnitLevel;
    List<String> territoryUnitNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            map = (WorldMap<String>) bundle.getSerializable(PLAYING_MAP);
            techResource = bundle.getInt(TECH_RESOURCE);
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

        Territory t = map.getTerritory(srcTerritory);
        List<Integer> level = new ArrayList<>(t.getUnitGroup().keySet());
        level.sort(Integer::compareTo);
        for (Integer l : level){
            territoryUnitLevel.add(String.valueOf(l));
        }

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

        Button btUpgradeMax = findViewById(R.id.bt_max);
        Button btConfirm = findViewById(R.id.bt_confirm);
        Button btDecline = findViewById(R.id.bt_decline);

        btUpgradeMax.setOnClickListener(v -> {
            UpMaxTechAction action = new UpMaxTechAction();
            sendActionToValid(action);
        });

        btConfirm.setOnClickListener(v -> {
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
        });
        btDecline.setOnClickListener(v -> {
            onBackPressed();
        });

        tvTotalCost = findViewById(R.id.tv_total_cost);
        tvTotalCost.setText("");

        setUpSrcTerritory();
        setUpFromLevelDropdown();
        setUpToLevelDropdown();
        setUpNumberDropdown();
        updateTotalCost();
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
                        new ArrayList<>(territoryOwn));

        srcTerritory = srcTerritoryAdapter.getItem(0);

        dropdownSrcTerritory =
                view.findViewById(R.id.input);
        dropdownSrcTerritory.setAdapter(srcTerritoryAdapter);
        dropdownSrcTerritory.setText(srcTerritory, false);
        dropdownSrcTerritory.setOnItemClickListener((parent, v, position, id) -> {
            srcTerritory = srcTerritoryAdapter.getItem(position);
            updateUnitList();
        });

        // setup recycler view
        RecyclerView rvUnitList = view.findViewById(R.id.rv_unit_list);

        srcUnitAdapter = new UnitAdapter();
        srcUnitAdapter.setListener(position -> {
            UnitGroup unit = srcUnitAdapter.getUnitGroup(position);
            showToastUI(UpgradeActivity.this, "you click");
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

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        UpgradeActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        new ArrayList<>(territoryUnitLevel));

        unitLevelFrom = Integer.parseInt(adapter.getItem(0));

        AutoCompleteTextView dropdownAction =
                layout.findViewById(R.id.input);
        dropdownAction.setAdapter(adapter);
        dropdownAction.setText(adapter.getItem(0), false);
        dropdownAction.setOnItemClickListener((parent, v, position, id) -> {
            unitLevelFrom = Integer.parseInt(adapter.getItem(position));
            updateTotalCost();
        });
    }

    private void setUpToLevelDropdown(){
        TextInputLayout layout = findViewById(R.id.dd_unit_level_to);
        layout.setHint("To Level");

        List<String> toLevel = new ArrayList<>();
        for (int i = unitLevelFrom + 1; i <= 6; i++){
            toLevel.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        UpgradeActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        toLevel);

        unitLevelTo = Integer.parseInt(adapter.getItem(0));

        AutoCompleteTextView dropdownAction =
                layout.findViewById(R.id.input);
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

        List<String> number = new ArrayList<>();
        for (int i = 1; i <= 20; i++){
            number.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        UpgradeActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        number);

        unitNum = Integer.parseInt(adapter.getItem(0));

        AutoCompleteTextView dropdownAction =
                layout.findViewById(R.id.input);
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
        return true;
    }
}
