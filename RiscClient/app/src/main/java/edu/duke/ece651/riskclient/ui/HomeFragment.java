package edu.duke.ece651.riskclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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

import static edu.duke.ece651.riskclient.RiskApplication.getPlayerName;
import static edu.duke.ece651.riskclient.RiskApplication.initGameSocket;
import static edu.duke.ece651.riskclient.RiskApplication.setAudience;
import static edu.duke.ece651.riskclient.RiskApplication.setRoom;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.audienceGame;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.backGame;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.createNewRoom;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.getRoomList;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.joinGame;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToast;
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
        return view;
    }

    @Override
    public void onResume() {
        updateData();
        super.onResume();
    }

    private void setUpUI(View view){
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> clickFAB());

        ChipGroup cgRoom = view.findViewById(R.id.cg_room_type);
        cgRoom.setOnCheckedChangeListener((chipGroup, id) -> {
            isInRoom = (id == R.id.chip_room_in);
            updateData();
        });

        roomAdapter = new RoomAdapter();
        roomAdapter.setListener(position -> clickRoom(roomAdapter.getRoom(position)));

        RecyclerView rcRoomList = view.findViewById(R.id.rv_room_list);
        rcRoomList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcRoomList.setHasFixedSize(true);
        rcRoomList.setAdapter(roomAdapter);

        swipeRefreshLayout = view.findViewById(R.id.swr_layout);
        swipeRefreshLayout.setOnRefreshListener(this::updateData);

        tvRoomInfo = view.findViewById(R.id.tv_no_room);
        tvRoomInfo.setVisibility(View.INVISIBLE);
    }

    /**
     * Handle the click event of the FAB.
     */
    private void clickFAB(){
        // initial the global game socket before start or join a game
        initGameSocket(new onResultListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "fab: " + error);
            }

            @Override
            public void onSuccessful() {
                // should use the game socket only after successfully initialize it
                // return the result of choose room
                createNewRoom(new onResultListener() {
                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "createNewRoom: " + error);
                    }

                    @Override
                    public void onSuccessful() {
                        Intent intent = new Intent(getActivity(), NewRoomActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    /**
     * Handle the event of click a specific room.
     * @param roomInfo roomInfo of the clicked room
     */
    private void clickRoom(RoomInfo roomInfo){
        setRoom(roomInfo);
        if (isInRoom){
            reconnectRoom();
        }else if (!roomInfo.hasStarted()){
            joinRoom();
        }else {
            checkAudience(roomInfo);
        }
    }

    private void checkAudience(RoomInfo roomInfo){
        if (roomInfo.getPlayerNames().contains(getPlayerName())){
            showToast("You already in this room, please reconnect rather than audience.");
        }else {
            // show the audience dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Important info");
            builder.setMessage("This room has already started, you can only join as an audience. Do you still want to join?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                audienceRoom();
            });
            builder.setNegativeButton("no", ((dialog, which) -> {
                // do nothing
            }));
            builder.show();
        }
    }

    /**
     * User want to join in a room(as player).
     */
    private void joinRoom(){
        initGameSocket(new onResultListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "join room: " + error);
            }

            @Override
            public void onSuccessful() {
                joinGame(new onResultListener() {
                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "join game: " + error);
                    }

                    @Override
                    public void onSuccessful() {
                        setAudience(false);
                        Intent intent = new Intent(getActivity(), WaitGameActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    /**
     * User want to reconnect to a room.
     */
    private void reconnectRoom(){
        initGameSocket(new onResultListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "reconnect room: " + error);
            }

            @Override
            public void onSuccessful() {
                backGame(new onResultListener() {
                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "back room: " + error);
                    }

                    @Override
                    public void onSuccessful() {
                        setAudience(false);
                        Intent intent = new Intent(getActivity(), PlayGameActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    /**
     * User want to audience a room.
     */
    private void audienceRoom(){
        initGameSocket(new onResultListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "audience room: " + error);
            }

            @Override
            public void onSuccessful() {
                audienceGame(new onResultListener() {
                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "audience room: " + error);
                    }

                    @Override
                    public void onSuccessful() {
                        setAudience(true);
                        Intent intent = new Intent(getActivity(), PlayGameActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
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
