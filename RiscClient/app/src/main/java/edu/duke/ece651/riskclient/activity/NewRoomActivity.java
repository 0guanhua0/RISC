package edu.duke.ece651.riskclient.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.duke.ece651.riskclient.MapAdapter;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.WorldMap;

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
        TextInputEditText etRoomName = findViewById(R.id.et_room_name);

        Button btCreate = findViewById(R.id.bt_create);
        btCreate.setOnClickListener(v -> {
            String roomName = Objects.requireNonNull(etRoomName.getText()).toString();
            if (roomName.isEmpty()){
                showToastUI(NewRoomActivity.this, "Room name can't be empty");
                return;
            }
            if (selectedMap == null){
                showToastUI(NewRoomActivity.this, "Please select a map.");
                return;
            }
            // TODO: send the new room info to server and receive response
            showToastUI(NewRoomActivity.this, "Create new room successful.");
            Intent intent = new Intent(NewRoomActivity.this, PlayGameActivity.class);
            startActivity(intent);
            // kill current activity, user can't go back
            finish();
        });

        ImageView imgMap = findViewById(R.id.img_map);
        imgMap.setImageResource(R.drawable.risk_img);

        tvMapName = findViewById(R.id.tv_map_name);

        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        // TODO: replace the fake data with real one
        List<WorldMap> maps = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            maps.add(new WorldMap("a" + i));
        }

        mapAdapter = new MapAdapter();
        mapAdapter.setListener(position -> {
            selectedMap = maps.get(position);
            tvMapName.setText(selectedMap.getName());
        });
        RecyclerView rvMapList = findViewById(R.id.rv_map_list);
        rvMapList.setHasFixedSize(true);
        rvMapList.setLayoutManager(new LinearLayoutManager(this));
        rvMapList.setAdapter(mapAdapter);

        mapAdapter.setMaps(maps);
    }
}
