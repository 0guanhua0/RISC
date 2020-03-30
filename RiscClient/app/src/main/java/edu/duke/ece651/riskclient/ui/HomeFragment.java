package edu.duke.ece651.riskclient.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.duke.ece651.riskclient.Player;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.activity.NewRoomActivity;

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

    private Player player;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fab = view.findViewById(R.id.fab);
        setUpUI();
        return view;
    }

    private void setUpUI(){
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewRoomActivity.class);
            startActivity(intent);
        });
    }
}
