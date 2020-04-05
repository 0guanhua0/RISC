package edu.duke.ece651.riskclient.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.duke.ece651.riskclient.objects.Player;
import edu.duke.ece651.riskclient.R;

import static edu.duke.ece651.riskclient.RiskApplication.getPlayerName;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordFragment extends Fragment {

    private TextView tvUserName;

    public PasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UpdateFragment.
     */
    public static PasswordFragment newInstance() {
        PasswordFragment fragment = new PasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password, container, false);
        tvUserName = view.findViewById(R.id.tv_user_name);
        setUpUI();
        return view;
    }

    private void setUpUI(){
        tvUserName.setText(getPlayerName());
        // TODO: implement logic here
    }
}
