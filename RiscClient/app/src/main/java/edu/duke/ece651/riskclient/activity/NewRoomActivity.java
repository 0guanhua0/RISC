package edu.duke.ece651.riskclient.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import edu.duke.ece651.risk.shared.Room;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.map.WorldMapV2;
import edu.duke.ece651.riskclient.adapter.MapAdapter;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.listener.onResultListener;

import static edu.duke.ece651.riskclient.RiskApplication.recv;
import static edu.duke.ece651.riskclient.RiskApplication.setRoom;
import static edu.duke.ece651.riskclient.activity.WaitGameActivity.PLAYER_CNT;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.createNewRoom;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class NewRoomActivity extends AppCompatActivity {

    private MapAdapter mapAdapter;
    private WorldMap selectedMap;

    /**
     * UI variable
     */
    TextView tvMapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Add Room");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setUpUI();
        updateMaps();
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
        TextInputLayout tilRoomName = findViewById(R.id.til_room_name);
        TextInputEditText etRoomName = findViewById(R.id.et_room_name);

        etRoomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // remove the error message
                tilRoomName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button btCreate = findViewById(R.id.bt_create);
        btCreate.setOnClickListener(v -> {
            String roomName = Objects.requireNonNull(etRoomName.getText()).toString();
            if (roomName.isEmpty()){
                tilRoomName.setError("Room name can't be empty");
                return;
            }
            if (selectedMap == null){
                showToastUI(NewRoomActivity.this, "Please select a map.");
                return;
            }
            newRoom(roomName);
        });

        ImageView imgMap = findViewById(R.id.img_map);
        imgMap.setImageResource(R.drawable.risk_img);

        tvMapName = findViewById(R.id.tv_map_name);

        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        mapAdapter = new MapAdapter();
        mapAdapter.setListener(position -> {
            selectedMap = mapAdapter.getMap(position);
            tvMapName.setText(selectedMap.getName());
        });
        RecyclerView rvMapList = findViewById(R.id.rv_map_list);
        rvMapList.setHasFixedSize(true);
        rvMapList.setLayoutManager(new LinearLayoutManager(this));
        rvMapList.setAdapter(mapAdapter);
    }

    private void updateMaps(){
        // fake data
        List<WorldMap> maps = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            Map<String, Set<String>> m = new HashMap<String, Set<String>>() {{
                put("a", new HashSet<String>());
                put("b", new HashSet<String>());
                put("c", new HashSet<String>());
            }};
            Map<Set<String>,Boolean> groups = new HashMap<Set<String>, Boolean>(){{
                put(new HashSet<String>(Arrays.asList("a")),false);
                put(new HashSet<String>(Arrays.asList("b")),false);
                put(new HashSet<String>(Arrays.asList("c")),false);
            }};
            Map<String,Integer> sizes = new HashMap<String, Integer>(){{
                put("a",2);
                put("b",2);
                put("c",2);
            }};

            Map<String,Integer> food = new HashMap<String, Integer>(){{
                put("a",3);
                put("b",3);
                put("c",3);
            }};

            Map<String,Integer> tech = new HashMap<String, Integer>(){{
                put("a",4);
                put("b",4);
                put("c",4);
            }};
            List<String> colorList = new ArrayList<String>(Arrays.asList("red", "blue", "black"));
            maps.add(new WorldMapV2<String>(m, colorList,groups,sizes,food,tech));
        }
        mapAdapter.setMaps(maps);
        // set default selected map
        selectedMap = mapAdapter.getMap(0);
        tvMapName.setText(selectedMap.getName());
//        recv(new onReceiveListener() {
//            @Override
//            public void onFailure(String error) {
//
//            }
//
//            @Override
//            public void onSuccessful(Object object) {
//                MapDataBase<String> mapDB = (MapDataBase<String>) object;
//                Map<String, WorldMap<String>> allMaps = mapDB.getAllMaps();
//
//                mapAdapter.setMaps(new ArrayList<>(allMaps.values()));
//                // set default selected map
//                selectedMap = mapAdapter.getMap(0);
//            }
//        });
    }

    private void newRoom(String roomName){
        createNewRoom(roomName, selectedMap.getName(), new onResultListener() {
            @Override
            public void onFailure(String error) {

            }

            @Override
            public void onSuccessful() {
                setRoom(new Room(1, roomName));
                showToastUI(NewRoomActivity.this, "Create new room successful.");
                Intent intent = new Intent(NewRoomActivity.this, WaitGameActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(PLAYER_CNT, selectedMap.getColorList().size());
                startActivity(intent, bundle);
                // kill current activity, user can't go back
                finish();
            }
        });
    }
}
