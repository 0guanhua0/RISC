package edu.duke.ece651.riskclient.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackAction;
import edu.duke.ece651.risk.shared.action.MoveAction;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.TerritoryImpl;
import edu.duke.ece651.risk.shared.map.Unit;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.adapter.UnitAdapter;
import edu.duke.ece651.riskclient.listener.onResultListener;
import edu.duke.ece651.riskclient.objects.UnitGroup;

import static edu.duke.ece651.risk.shared.Constant.UNIT_NAME;
import static edu.duke.ece651.riskclient.Constant.ACTION_PERFORMED;
import static edu.duke.ece651.riskclient.RiskApplication.getPlayerID;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_CURRENT_PLAYER;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_FOOD_RESOURCE;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_IS_MOVE;
import static edu.duke.ece651.riskclient.activity.PlayGameActivity.DATA_PLAYING_MAP;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.sendAction;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class MoveAttackActivity extends AppCompatActivity {

    /**
     * UI variable
     */
    private TextView tvUnitsInfo;

    /**
     * Variable
     */
    private UnitAdapter srcUnitAdapter;
    private UnitAdapter destUnitAdapter;
    private ArrayAdapter<String> srcTerritoryAdapter;
    private ArrayAdapter<String> destTerritoryAdapter;
    private boolean isMove;
    private WorldMap<String> map;
    private Player<String> player;
    private int foodResource;
    // territory info
    // territories own by current player(and his/her allay)
    List<String> territoryOwn;
    // territories own by other players(except current player but include ally)
    List<String> territoryOther;
    // parameters of the action
    private String srcTerritory;
    private String destTerritory;
    private Map<Integer, Integer> units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_attack);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            map = (WorldMap<String>) bundle.getSerializable(DATA_PLAYING_MAP);
            player = (Player<String>) bundle.getSerializable(DATA_CURRENT_PLAYER);
            foodResource = bundle.getInt(DATA_FOOD_RESOURCE);
            isMove = bundle.getBoolean(DATA_IS_MOVE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Move/Attack");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        territoryOwn = new ArrayList<>();
        territoryOther = new ArrayList<>();
        groupTerritory();

        srcTerritory = "";
        destTerritory = "";
        units = new TreeMap<>();
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
        tvResource.setText(String.format(Locale.US, "Total food resource(before this action): %d", foodResource));

        Button btConfirm = findViewById(R.id.bt_confirm);
        Button btDecline = findViewById(R.id.bt_decline);

        btConfirm.setOnClickListener(v -> {
            if (validateAction()){
                Action action = null;
                if (isMove){
                    action = new MoveAction(srcTerritory, destTerritory, getPlayerID(), units);
                }else {
                    action = new AttackAction(srcTerritory, destTerritory, getPlayerID(), units);
                }
                Action finalAction = action;
                sendAction(finalAction, new onResultListener() {
                    @Override
                    public void onFailure(String error) {
                        // either invalid action or networking problem
                        showToastUI(MoveAttackActivity.this, error);
                        // clear all input once action invalid
                        units.clear();
                        runOnUiThread(() -> refreshUnitsInfo());
                    }

                    @Override
                    public void onSuccessful() {
                        // valid action, return to play game page
                        Intent data = new Intent();
                        if (finalAction instanceof MoveAction){
                            data.putExtra(ACTION_PERFORMED, (MoveAction) finalAction);
                        }else {
                            data.putExtra(ACTION_PERFORMED, (AttackAction) finalAction);
                        }
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
            }
        });
        btDecline.setOnClickListener(v -> {
            onBackPressed();
        });

        ImageView imgSendUnit = findViewById(R.id.img_add_unit);
        imgSendUnit.setOnClickListener(v -> {
            sendUnits();
        });

        tvUnitsInfo = findViewById(R.id.tv_units_info);
        tvUnitsInfo.setMovementMethod(new ScrollingMovementMethod());
        tvUnitsInfo.setText("");

        setUpSrcTerritory();
        setUpDestTerritory();
    }

    /**
     * Popup a window to ask the user input units info
     */
    private void sendUnits(){
        LayoutInflater layoutInflater = getLayoutInflater();

        Territory t = map.getTerritory(srcTerritory);
        int totalUnits = 0;
        List<String> unitLevel = new ArrayList<>();
        for (Map.Entry<Integer, List<Unit>> entry : t.getUnitGroup().entrySet()){
            unitLevel.add(String.valueOf(entry.getKey()));
            totalUnits += entry.getValue().size();
        }
        // sort the level
        unitLevel.sort(String::compareTo);
        ArrayAdapter<String> adapterLevel =
                new ArrayAdapter<>(
                        MoveAttackActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        unitLevel);

        List<String> unitNumber = new ArrayList<>();
        // constraint the max number of units you can send
        for (int i = 1; i <= totalUnits; i++){
            unitNumber.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapterNumber =
                new ArrayAdapter<>(
                        MoveAttackActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        unitNumber);

        View view = layoutInflater.inflate(R.layout.view_add_units, null);
        // level
        TextInputLayout tlLevel = view.findViewById(R.id.layout_level);
        AutoCompleteTextView dpLevel = tlLevel.findViewById(R.id.dd_input);
        tlLevel.setHint("Unit Level");
        dpLevel.setAdapter(adapterLevel);
        dpLevel.setText(adapterLevel.getItem(0), false);

        // number
        TextInputLayout tlNumber = view.findViewById(R.id.layout_number);
        AutoCompleteTextView dpNumber = tlNumber.findViewById(R.id.dd_input);
        tlNumber.setHint("Unit Numbers");
        dpNumber.setAdapter(adapterNumber);
        dpNumber.setText(adapterNumber.getItem(0), false);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Add Units");
        mBuilder.setView(view);
        mBuilder.setPositiveButton("Confirm", ((dialogInterface, i) -> {
            Integer level = Integer.parseInt(dpLevel.getText().toString());
            Integer number = Integer.parseInt(dpNumber.getText().toString());
            if (units.containsKey(level)){
                int old = units.get(level);
                units.replace(level, old + number);
            }else {
                units.put(level, number);
            }
            refreshUnitsInfo();
        }));
        mBuilder.setNegativeButton("Cancel", ((dialogInterface, i) -> {
        }));
        mBuilder.create().show();
    }

    private void setUpSrcTerritory(){
        // find the parent view of territory-unit-list
        View view = findViewById(R.id.src_territory);
        // setup drop down
        TextInputLayout layout = view.findViewById(R.id.dropdown_layout);
        layout.setHint("Src Territory");
        srcTerritoryAdapter =
                new ArrayAdapter<>(
                        MoveAttackActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        new ArrayList<>(territoryOwn));

        srcTerritory = srcTerritoryAdapter.getItem(0);

        AutoCompleteTextView dropdownSrcTerritory = view.findViewById(R.id.dd_input);
        dropdownSrcTerritory.setAdapter(srcTerritoryAdapter);
        dropdownSrcTerritory.setText(srcTerritory, false);
        dropdownSrcTerritory.setOnItemClickListener((parent, v, position, id) -> {
            srcTerritory = srcTerritoryAdapter.getItem(position);
            updateUnitList(true);
            // each time change src territory, you need to clear all units specify before
            units.clear();
            refreshUnitsInfo();
        });

        // setup recycler view
        RecyclerView rvUnitList = view.findViewById(R.id.rv_unit_list);

        srcUnitAdapter = new UnitAdapter();
        srcUnitAdapter.setListener(position -> {
            UnitGroup unit = srcUnitAdapter.getUnitGroup(position);
        });
        rvUnitList.setLayoutManager(new LinearLayoutManager(MoveAttackActivity.this));
        rvUnitList.setHasFixedSize(true);
        rvUnitList.setAdapter(srcUnitAdapter);
        // set default unit list
        updateUnitList(true);
    }

    private void setUpDestTerritory(){
        View view = findViewById(R.id.dest_territory);
        // setup drop down
        TextInputLayout layout = view.findViewById(R.id.dropdown_layout);
        layout.setHint("Dest Territory");

        destTerritoryAdapter =
                new ArrayAdapter<>(
                        MoveAttackActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        new ArrayList<>(isMove ? territoryOwn : territoryOther));

        destTerritory = destTerritoryAdapter.getItem(0);

        AutoCompleteTextView dropdownDestTerritory = view.findViewById(R.id.dd_input);
        dropdownDestTerritory.setAdapter(destTerritoryAdapter);
        dropdownDestTerritory.setText(destTerritory, false);
        dropdownDestTerritory.setOnItemClickListener((parent, v, position, id) -> {
            destTerritory = destTerritoryAdapter.getItem(position);
            updateUnitList(false);
        });

        // setup recycler view
        RecyclerView rvUnitList = view.findViewById(R.id.rv_unit_list);

        destUnitAdapter = new UnitAdapter();
        destUnitAdapter.setListener(position -> {
            UnitGroup unit = destUnitAdapter.getUnitGroup(position);
        });
        rvUnitList.setLayoutManager(new LinearLayoutManager(MoveAttackActivity.this));
        rvUnitList.setHasFixedSize(true);
        rvUnitList.setAdapter(destUnitAdapter);
        updateUnitList(false);
    }

    private void updateUnitList(boolean isSrc){
        if (isSrc){
            Territory t = map.getTerritory(srcTerritory);
            List<UnitGroup> unitGroups = new ArrayList<>();
            for (Map.Entry<Integer, List<Unit>> entry : t.getUnitGroup().entrySet()){
                unitGroups.add(new UnitGroup(entry.getKey(), entry.getValue().size(), false));
            }
            for (Map.Entry<Integer, List<Unit>> entry : t.getAllyUnitGroup().entrySet()){
                unitGroups.add(new UnitGroup(entry.getKey(), entry.getValue().size(), true));
            }
            srcUnitAdapter.setUnits(unitGroups);
        }else {
            Territory t = map.getTerritory(destTerritory);
            List<UnitGroup> unitGroups = new ArrayList<>();
            for (Map.Entry<Integer, List<Unit>> entry : t.getUnitGroup().entrySet()){
                unitGroups.add(new UnitGroup(entry.getKey(), entry.getValue().size(), false));
            }
            for (Map.Entry<Integer, List<Unit>> entry : t.getAllyUnitGroup().entrySet()){
                unitGroups.add(new UnitGroup(entry.getKey(), entry.getValue().size(), true));
            }
            destUnitAdapter.setUnits(unitGroups);
        }
    }

    private void refreshUnitsInfo(){
        StringBuilder builder = new StringBuilder();
        // key: level; value: number
        for (Map.Entry<Integer, Integer> entry : units.entrySet()){
            builder.append(entry.getValue()).append(" ").append(UNIT_NAME.get(entry.getKey())).append("\n");
        }
        tvUnitsInfo.setText(builder.toString());
    }

    private boolean validateAction(){
        if (units.isEmpty()){
            showToastUI(MoveAttackActivity.this, "You send 0 unit.");
            return false;
        }
        if (srcTerritory.equals(destTerritory)){
            showToastUI(MoveAttackActivity.this, "Src and dest are the same.");
            return false;
        }
        return true;
    }

    private void groupTerritory(){
        List<Territory> territories = new ArrayList<>(map.getAtlas().values());
        // territory own by ally should add in both tOwn & tOther
        // since we can
        // 1. move to ally's territory
        // 2. attack ally's territory
        // we use two for loop to guarantee territory own by yourself comes first and then your ally
        for (Territory territory : territories){
            if (territory.getOwner() == player.getId()){
                territoryOwn.add(territory.getName());
            }else {
                territoryOther.add(territory.getName());
            }
        }
        for (Territory territory : territories){
            if (territory.getOwner() == player.getAlly().getId()){
                territoryOwn.add(territory.getName());
            }
        }
    }
}
