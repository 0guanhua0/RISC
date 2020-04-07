package edu.duke.ece651.riskclient.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import edu.duke.ece651.risk.shared.ToClientMsg.ClientSelect;
import edu.duke.ece651.risk.shared.ToServerMsg.ServerSelect;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.adapter.TerritoryGroupAdapter;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.listener.onResultListener;
import pl.polak.clicknumberpicker.ClickNumberPickerView;

import static edu.duke.ece651.riskclient.Constant.MAP_NAME_TO_RESOURCE_ID;
import static edu.duke.ece651.riskclient.RiskApplication.recv;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.verifyAssignUnits;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.verifySelectGroup;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class SelectTerritoryActivity extends AppCompatActivity {

    private final static String TAG = SelectTerritoryActivity.class.getSimpleName();

    /**
     * UI variable
     */
    private LinearLayout layoutSelect;
    private LinearLayout layoutAssign;
    private TextView tvUnitsInfo;
    private ImageView imgMap;

    /**
     * Variable
     */
    private boolean finishSelect;
    private TerritoryGroupAdapter territoryGroupAdapter;
    private Set<String> selectedGroup;
    private Map<String, Integer> unitsAssigned;
    private int unitTotal;
    private int unitLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_territory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Init Game");
        }

        finishSelect = false;

        setUpUI();
        init();
    }

    /**
     * Receive the select info from server and update UI.
     */
    private void init(){
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {

            }

            @Override
            public void onSuccessful(Object object) {
                ClientSelect select = (ClientSelect) object;
                unitTotal = select.getUnitsTotal();
                unitLeft = unitTotal;
                WorldMap map = select.getMap();
                // TODO: set resource based on the map name
                runOnUiThread(() -> {
                    imgMap.setImageResource(MAP_NAME_TO_RESOURCE_ID.get(map.getName()));
                    List<Set<String>> groups = new ArrayList<>(map.getGroups().keySet());
                    // set default selected group
                    selectedGroup = groups.get(0);
                    territoryGroupAdapter.setTerritories(groups);
                });
            }
        });
    }

    /**
     * set up the common UI between two stages
     */
    private void setUpUI(){
        layoutSelect = findViewById(R.id.layout_select_territory);
        layoutAssign = findViewById(R.id.layout_assign_units);
        layoutSelect.setVisibility(View.VISIBLE);
        layoutAssign.setVisibility(View.GONE);

        Button btNext = findViewById(R.id.bt_next);
        btNext.setText(R.string.select_territory_next);
        btNext.setOnClickListener(v -> {
            if (selectedGroup == null){
                showToastUI(SelectTerritoryActivity.this, "Please select a group of territories first.");
                return;
            }
            if (!finishSelect){
                // select territory
                verifySelectGroup(selectedGroup, new onResultListener() {
                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "verify group: " + error);
                        // invalid group selection
                        showToastUI(SelectTerritoryActivity.this, error);
                    }

                    @Override
                    public void onSuccessful() {
                        runOnUiThread(() -> {
                            finishSelect = true;
                            btNext.setText(R.string.select_territory_finish);
                            layoutSelect.animate()
                                    .alpha(0.0f)
                                    .setDuration(500)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            layoutSelect.setVisibility(View.GONE);
                                            layoutAssign.setVisibility(View.VISIBLE);
                                        }
                                    });
                            setUpAssignUnits();
                        });
                    }
                });
            }else {
                // assign units
                if (unitLeft > 0){
                    showToastUI(this, "Please assign all units before submit.");
                    return;
                }
                if (unitLeft < 0){
                    showToastUI(this, "You assign more units then you have.");
                    return;
                }
                ServerSelect serverSelect = new ServerSelect(unitsAssigned);
                verifyAssignUnits(serverSelect, new onResultListener() {
                    @Override
                    public void onFailure(String error) {
                        showToastUI(SelectTerritoryActivity.this, error);
                        Log.e(TAG, "verifyAssignUnits: " + error);
                    }

                    @Override
                    public void onSuccessful() {
                        Intent intent = new Intent(SelectTerritoryActivity.this, PlayGameActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        setUpSelectTerritory();
    }

    /**
     * set up the UI for selecting territory
     */
    private void setUpSelectTerritory(){
        imgMap = findViewById(R.id.img_map);

        RecyclerView rvTerritories = findViewById(R.id.rv_territory_group);

        territoryGroupAdapter = new TerritoryGroupAdapter();
        territoryGroupAdapter.setListener(position -> {
            selectedGroup = territoryGroupAdapter.getGroup(position);
        });

        rvTerritories.setLayoutManager(new LinearLayoutManager(this));
        rvTerritories.setHasFixedSize(true);
        rvTerritories.setAdapter(territoryGroupAdapter);
    }

    /* ====== below are the functions responsible for assign units ====== */

    /**
     * set up the UI for assigning units
     */
    private void setUpAssignUnits(){
        unitsAssigned = new HashMap<>();
        tvUnitsInfo = findViewById(R.id.tv_units_info);
        updateUnitInfo();
        LinearLayout layout = findViewById(R.id.layout_assign_units);
        for (String tName : selectedGroup){
            layout.addView(inflateTerritory(tName, layout));
            // assign 1 basic unit to each territory
            unitsAssigned.put(tName, 1);
        }
    }

    private View inflateTerritory(String territoryName, ViewGroup parent){
        View view = getLayoutInflater().inflate(R.layout.listitem_territory_units_picker, parent, false);
        TextView name = view.findViewById(R.id.tv_territory_name);
        name.setText(territoryName);

        ClickNumberPickerView numberPicker = view.findViewById(R.id.np_units_picker);
        numberPicker.setPickerValue(1);
        unitLeft--;
        updateUnitInfo();
        numberPicker.setClickNumberPickerListener((previousValue, currentValue, pickerClickType) -> {
            unitLeft -= (currentValue - previousValue);
            unitsAssigned.replace(territoryName, (int) currentValue);
            if (unitLeft < 0){
                // a better way is change the value back automatically, but this library doesn't support that
                showToastUI(this, "You assign more units than you have.");
            }
            updateUnitInfo();
        });
        return view;
    }

    private void updateUnitInfo(){
        tvUnitsInfo.setText(String.format(Locale.US, "Units you have: %d total(%d left)", unitTotal, unitLeft));
    }
}
