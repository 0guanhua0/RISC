package edu.duke.ece651.riskclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import edu.duke.ece651.risk.shared.RoomInfo;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.activity.NewRoomActivity;
import edu.duke.ece651.riskclient.activity.PlayGameActivity;
import edu.duke.ece651.riskclient.activity.WaitGameActivity;
import edu.duke.ece651.riskclient.adapter.RoomAdapter;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.listener.onResultListener;

import static edu.duke.ece651.riskclient.RiskApplication.initGameSocket;
import static edu.duke.ece651.riskclient.RiskApplication.setRoom;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.createNewRoom;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.getRoomList;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    /**
     * UI variable
     */
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvRoomInfo;

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
            // initial the global game socket before start or join a game
            initGameSocket(new onResultListener() {
                @Override
                public void onFailure(String error) {

                }

                @Override
                public void onSuccessful() {
                    // should use the game socket only after successfully initialize it
                    // return the result of choose room
                    createNewRoom(new onResultListener() {
                        @Override
                        public void onFailure(String error) {

                        }

                        @Override
                        public void onSuccessful() {
                            Intent intent = new Intent(getActivity(), NewRoomActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            });
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
            // initial the global game socket before join or back a game
            initGameSocket(new onResultListener() {
                @Override
                public void onFailure(String error) {

                }

                @Override
                public void onSuccessful() {
                    Intent intent = null;
                    if (isInRoom){
                        // if click the room already in, redirect to PlayGame page
                        intent = new Intent(getActivity(), PlayGameActivity.class);

                    }else {
                        // if want to join a room, redirect to WaitGame page
                        intent = new Intent(getActivity(), WaitGameActivity.class);
                    }
                    startActivity(intent);
                }
            });
        });

        RecyclerView rcRoomList = view.findViewById(R.id.rv_room_list);
        rcRoomList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcRoomList.setHasFixedSize(true);
        rcRoomList.setAdapter(roomAdapter);

        swipeRefreshLayout = view.findViewById(R.id.swr_layout);
        swipeRefreshLayout.setOnRefreshListener(this::updateData);

        tvRoomInfo = view.findViewById(R.id.tv_no_room);
        tvRoomInfo.setVisibility(View.INVISIBLE);
    }

    private void updateData(){
        getRoomList(isInRoom, new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                showToastUI(getActivity(), error);
            }

            @Override
            public void onSuccessful(Object o) {
                showToastUI(getActivity(), "update successful");
                getActivity().runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (o instanceof List<?>){
                        roomAdapter.setRooms((List<RoomInfo>) o);
                    }
                    if (roomAdapter.getItemCount() == 0){
                        tvRoomInfo.setVisibility(View.VISIBLE);
                        tvRoomInfo.setText(isInRoom ? R.string.home_no_room_in : R.string.home_no_room_wait);
                    }else {
                        tvRoomInfo.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }
}
