package edu.duke.ece651.riskclient.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import edu.duke.ece651.riskclient.MapAdapter;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.WorldMap;

public class NewRoomActivity extends AppCompatActivity {

    private MapAdapter mapAdapter;

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

        TextInputEditText etRoomName = findViewById(R.id.et_room_name);

        Button btCreate = findViewById(R.id.bt_create);

        ImageView imgMap = findViewById(R.id.img_map);
        imgMap.setImageResource(R.drawable.risk_img);

        TextView tvMapName = findViewById(R.id.tv_map_name);

        mapAdapter = new MapAdapter();
        mapAdapter.setListener(mapName -> {
            tvMapName.setText(String.format("Map name: %s", mapName));
        });
        RecyclerView rvMapList = findViewById(R.id.rv_map_list);
        rvMapList.setHasFixedSize(true);
        rvMapList.setLayoutManager(new LinearLayoutManager(this));
        rvMapList.setAdapter(mapAdapter);

        List<WorldMap> maps = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            maps.add(new WorldMap("a" + i));
        }
        mapAdapter.setMaps(maps);
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
}
