package edu.duke.ece651.riskclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import edu.duke.ece651.risk.shared.Room;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.activity.NewRoomActivity;
import edu.duke.ece651.riskclient.activity.PlayGameActivity;
import edu.duke.ece651.riskclient.activity.WaitGameActivity;
import edu.duke.ece651.riskclient.adapter.RoomAdapter;
import edu.duke.ece651.riskclient.listener.onReceiveListener;

import static edu.duke.ece651.riskclient.RiskApplication.getRoomName;
import static edu.duke.ece651.riskclient.RiskApplication.setRoom;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.getRoomList;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Variable
     */
    private boolean isInRoom;
    private RoomAdapter roomAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInRoom = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setUpUI(view);
        updateData();
        return view;
    }

    private void setUpUI(View view){
        FloatingActionButton fab = view.findViewById(R.id.fab);
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
            isInRoom = (id == R.id.chip_room_in);
            updateData();
        });

        roomAdapter = new RoomAdapter();
        roomAdapter.setListener(position -> {
            setRoom(roomAdapter.getRoom(position));
            // TODO: remove this before delivery
            showToastUI(getActivity(), "you click " + getRoomName());
            Intent intent = null;
            if (isInRoom){
                // if click the room already in, redirect to PlayGame page
                intent = new Intent(getActivity(), PlayGameActivity.class);

            }else {
                // if want to join a room, redirect to WaitGame page
                intent = new Intent(getActivity(), WaitGameActivity.class);
            }
            startActivity(intent);
        });

        RecyclerView rcRoomList = view.findViewById(R.id.rv_room_list);
        rcRoomList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcRoomList.setHasFixedSize(true);
        rcRoomList.setAdapter(roomAdapter);

        swipeRefreshLayout = view.findViewById(R.id.swr_layout);
        swipeRefreshLayout.setOnRefreshListener(this::updateData);
    }

    private void updateData(){
        getRoomList(isInRoom, new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                showToastUI(getActivity(), error);
            }

            @Override
            public void onSuccessful(Object o) {
                swipeRefreshLayout.setRefreshing(false);
                if (o != null){
                    roomAdapter.setRooms((List<Room>) o);
                }
            }
        });
    }
}
