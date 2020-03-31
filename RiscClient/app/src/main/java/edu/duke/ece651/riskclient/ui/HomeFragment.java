package edu.duke.ece651.riskclient.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.duke.ece651.riskclient.adapter.RoomAdapter;
import edu.duke.ece651.riskclient.listener.onClickListener;
import edu.duke.ece651.riskclient.objects.Player;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.activity.NewRoomActivity;
import edu.duke.ece651.riskclient.objects.Room;

import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ITEM_PLAYER = "player";

    /**
     * UI variable
     */
    private FloatingActionButton fab;

    /**
     * Variable
     */
    private Player player;
    private boolean isEmptyRoom;
    private RoomAdapter roomAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player player object
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(Player player) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_PLAYER, player);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            player = (Player) getArguments().getSerializable(ARG_ITEM_PLAYER);
        }
        isEmptyRoom = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setUpUI(view);
        return view;
    }

    private void setUpUI(View view){
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewRoomActivity.class);
            startActivity(intent);
        });

        ChipGroup cgRoom = view.findViewById(R.id.cg_room_type);
        cgRoom.setOnCheckedChangeListener((chipGroup, id) -> {
            if (id == R.id.chip_room_in){
                showToastUI(getActivity(), "click room in");
            }else {
                showToastUI(getActivity(), "click room all");
            }
            isEmptyRoom = (id == R.id.chip_room_in);
            updateData();
        });

        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < 30; i++){
            rooms.add(new Room("room" + (i + 1)));
        }

        roomAdapter = new RoomAdapter();
        roomAdapter.setListener(position -> {
            Room room = rooms.get(position);
            showToastUI(getActivity(), "you click" + room.getRoomName());
        });

        RecyclerView rcRoomList = view.findViewById(R.id.rv_room_list);
        rcRoomList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcRoomList.setHasFixedSize(true);
        rcRoomList.setAdapter(roomAdapter);

        roomAdapter.setRooms(rooms);
    }

    private void updateData(){

    }
}
