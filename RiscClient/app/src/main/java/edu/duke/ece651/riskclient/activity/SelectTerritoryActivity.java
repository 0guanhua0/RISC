package edu.duke.ece651.riskclient.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import edu.duke.ece651.risk.shared.ToClientMsg.ClientSelect;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.adapter.TerritoryGroupAdapter;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import pl.polak.clicknumberpicker.ClickNumberPickerView;

import static edu.duke.ece651.riskclient.Constant.MAP_NAME_TO_RESOURCE_ID;
import static edu.duke.ece651.riskclient.RiskApplication.recv;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class SelectTerritoryActivity extends AppCompatActivity {

    /**
     * UI variable
     */
    private LinearLayout layoutSelect;
    private LinearLayout layoutAssign;
    private TextView tvUnitsInfo;
    private ImageView imgMap;

    private boolean finishSelect;
    private TerritoryGroupAdapter territoryGroupAdapter;
    private Set<String> selectedGroup;
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
//                ClientSelect select = (ClientSelect) object;
//                unitTotal = select.getUnitsTotal();
//                unitLeft = unitTotal;
//                WorldMap map = select.getMap();
//                // TODO: set resource based on the map name
//                imgMap.setImageResource(MAP_NAME_TO_RESOURCE_ID.get(map.getName()));
//                territoryGroupAdapter.setTerritories(new ArrayList<>(map.getGroups().keySet()));

                unitTotal = 10;
                unitLeft = unitTotal;
                List<Set<String>> groups = new ArrayList<>();
                for (int i = 0; i < 30; i++){
                    Set<String> group = new HashSet<>();
                    group.add(i + "t1");
                    group.add(i + "t2");
                    group.add(i + "t3");
                    groups.add(group);
                }
                // set default selected group
                selectedGroup = groups.get(0);
                territoryGroupAdapter.setTerritories(groups);
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
                // TODO: verify group validation before switch
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
            }else {
                if (unitLeft > 0){
                    showToastUI(this, "Please assign all units before submit.");
                    return;
                }
                if (unitLeft < 0){
                    showToastUI(this, "You assign more units then you have.");
                    return;
                }
                // TODO: send selecting info to server
                Intent intent = new Intent(SelectTerritoryActivity.this, PlayGameActivity.class);
                startActivity(intent);
                finish();
            }
        });

        setUpSelectTerritory();
    }

    /**
     * set up the UI for selecting territory
     */
    private void setUpSelectTerritory(){
        // TODO: update image based on map name
        imgMap = findViewById(R.id.img_map);

        RecyclerView rvTerritories = findViewById(R.id.rv_territory_group);

        territoryGroupAdapter = new TerritoryGroupAdapter();
        territoryGroupAdapter.setListener(position -> {
            selectedGroup = territoryGroupAdapter.getGroup(position);
            showToastUI(SelectTerritoryActivity.this, "you click item");
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
        tvUnitsInfo = findViewById(R.id.tv_units_info);
        updateUnitInfo();
        LinearLayout layout = findViewById(R.id.layout_assign_units);
        for (int i = 0; i < 5; i++){
            layout.addView(inflateTerritory("territory " + i, layout));
        }
    }

    private View inflateTerritory(String territoryName, ViewGroup parent){
        View view = getLayoutInflater().inflate(R.layout.listitem_territory_units_picker, parent, false);
        TextView name = view.findViewById(R.id.tv_territory_name);
        name.setText(territoryName);

        ClickNumberPickerView unitsAssigned = view.findViewById(R.id.np_units_picker);
        unitsAssigned.setPickerValue(1);
        unitLeft--;
        updateUnitInfo();
        unitsAssigned.setClickNumberPickerListener((previousValue, currentValue, pickerClickType) -> {
            unitLeft -= (currentValue - previousValue);
            if (unitLeft < 0){
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
