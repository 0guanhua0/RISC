package edu.duke.ece651.riskclient.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import edu.duke.ece651.risk.shared.ToClientMsg.RoundInfo;
import edu.duke.ece651.risk.shared.WorldState;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AllyAction;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.Unit;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.SPlayer;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.adapter.TerritoryAdapter;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.listener.onRecvAttackResultListener;
import edu.duke.ece651.riskclient.listener.onResultListener;

import static edu.duke.ece651.risk.shared.Constant.ACTION_DONE;
import static edu.duke.ece651.risk.shared.Constant.GAME_OVER;
import static edu.duke.ece651.riskclient.Constant.ACTION_PERFORMED;
import static edu.duke.ece651.riskclient.Constant.MAP_NAME_TO_RESOURCE_ID;
import static edu.duke.ece651.riskclient.Constant.NETWORK_PROBLEM;
import static edu.duke.ece651.riskclient.RiskApplication.getPlayerID;
import static edu.duke.ece651.riskclient.RiskApplication.getRoomName;
import static edu.duke.ece651.riskclient.RiskApplication.recv;
import static edu.duke.ece651.riskclient.RiskApplication.send;
import static edu.duke.ece651.riskclient.RiskApplication.setPlayerID;
import static edu.duke.ece651.riskclient.RiskApplication.startChatThread;
import static edu.duke.ece651.riskclient.RiskApplication.stopChatThread;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.recvAttackResult;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.sendAction;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class PlayGameActivity extends AppCompatActivity {
    private static final String TAG = PlayGameActivity.class.getSimpleName();

    public static final String DATA_PLAYING_MAP = "playingMap";
    public static final String DATA_FOOD_RESOURCE = "food";
    public static final String DATA_TECH_RESOURCE = "tech";
    public static final String DATA_CURRENT_TECH_LEVEL = "currentTechLevel";
    public static final String DATA_IS_MOVE = "isMove";
    public static final String DATA_IS_UPGRADE_MAX = "isUpgradeMax";
    public static final String DATA_ALL_PLAYERS = "allPlayers";
    public static final String DATA_CURRENT_PLAYER = "currentPlayer";

    private static final String TYPE_MOVE = "move";
    private static final String TYPE_ATTACK = "attack";
    private static final String TYPE_UPGRADE_UNIT = "upgrade unit";
    private static final String TYPE_UPGRADE_MAX = "upgrade max tech";
    private static final String TYPE_ALLIANCE = "form alliance";
    private static final String TYPE_DONE = "done";

    private static final int REQUEST_ACTION_MOVE = 1;
    private static final int REQUEST_ACTION_ATTACK = 2;
    private static final int REQUEST_ACTION_UPGRADE_MAX = 3;
    private static final int REQUEST_ACTION_UPGRADE_UNIT = 4;
    private static final int REQUEST_ACTION_ALLIANCE = 5;

    /**
     * UI variable
     */
    private TextView tvRoundNum;
    private TextView tvPlayerInfo;
    private TextView tvActionInfo;
    private Button btPerform;
    private ImageView imgMap;

    /**
     * Variable
     */
    private TerritoryAdapter territoryAdapter;
    private ArrayAdapter<String> actionAdapter;
    private List<Action> performedActions;
    private WorldMap<String> map;
    private Player<String> player;
    // we need to pass this list to ChatActivity, so use ArrayList here(List is not Serializable)
    private ArrayList<SPlayer> allPlayers;
    private int roundNum;
    private boolean isLose;
    private boolean hasShowDialog;
    private String actionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(getRoomName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        performedActions = new ArrayList<>();
        allPlayers = new ArrayList<>();
        roundNum = 1;
        isLose = false;
        hasShowDialog = false;
        actionType = TYPE_MOVE;

        setUpUI();
        // make sure user can't do anything before we receive the first round data
        setAllButtonClickable(false);
        // these two function use separate sockets, can perform them in parallel
        receiveLatestInfo();
        // connect to the chat room & start receiving incoming message(store into DB)
        startChatThread(new onResultListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "start chat thread: " + error);
            }

            @Override
            public void onSuccessful() {
                showToastUI(PlayGameActivity.this, "Successfully connect to the chat room.");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_game_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                goBack();
                break;
            case R.id.chat_room:
                Intent intent = new Intent(PlayGameActivity.this, ChatActivity.class);
                Bundle data = new Bundle();
                data.putSerializable(DATA_ALL_PLAYERS, allPlayers);
                data.putSerializable(DATA_CURRENT_PLAYER, player);
                intent.putExtras(data);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_ACTION_MOVE:
            case REQUEST_ACTION_ATTACK:
            case REQUEST_ACTION_UPGRADE_UNIT:
            case REQUEST_ACTION_UPGRADE_MAX:
            case REQUEST_ACTION_ALLIANCE:
                if (resultCode == RESULT_OK){
                    Action action = (Action) data.getSerializableExtra(ACTION_PERFORMED);
                    // server check that the action is valid, so we perform it to update current map
                    // NOTE: this only update the copy of the map, we will still get the latest map from server at the beginning of each term
                    action.perform(new WorldState(player, map));
                    performedActions.add(action);
                    showPerformedActions();
                    showPlayerInfo();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpUI(){
        btPerform = findViewById(R.id.bt_perform);
        btPerform.setOnClickListener(v -> {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            int requestCode = -1;
            switch (actionType){
                case TYPE_MOVE:
                    intent.setComponent(new ComponentName(PlayGameActivity.this, MoveAttackActivity.class));
                    bundle.putBoolean(DATA_IS_MOVE, true);
                    bundle.putSerializable(DATA_PLAYING_MAP, map);
                    bundle.putInt(DATA_FOOD_RESOURCE, player.getFoodNum());
                    requestCode = REQUEST_ACTION_MOVE;
                    break;
                case TYPE_ATTACK:
                    intent.setComponent(new ComponentName(PlayGameActivity.this, MoveAttackActivity.class));
                    bundle.putBoolean(DATA_IS_MOVE, false);
                    bundle.putSerializable(DATA_PLAYING_MAP, map);
                    bundle.putInt(DATA_FOOD_RESOURCE, player.getFoodNum());
                    requestCode = REQUEST_ACTION_ATTACK;
                    break;
                case TYPE_UPGRADE_UNIT:
                    intent.setComponent(new ComponentName(PlayGameActivity.this, UpgradeActivity.class));
                    bundle.putBoolean(DATA_IS_UPGRADE_MAX, false);
                    bundle.putSerializable(DATA_PLAYING_MAP, map);
                    bundle.putInt(DATA_TECH_RESOURCE, player.getTechNum());
                    bundle.putInt(DATA_CURRENT_TECH_LEVEL, player.getTechLevel());
                    requestCode = REQUEST_ACTION_UPGRADE_UNIT;
                    break;
                case TYPE_UPGRADE_MAX:
                    intent.setComponent(new ComponentName(PlayGameActivity.this, UpgradeActivity.class));
                    bundle.putBoolean(DATA_IS_UPGRADE_MAX, true);
                    bundle.putSerializable(DATA_PLAYING_MAP, map);
                    bundle.putInt(DATA_TECH_RESOURCE, player.getTechNum());
                    bundle.putInt(DATA_CURRENT_TECH_LEVEL, player.getTechLevel());
                    requestCode = REQUEST_ACTION_UPGRADE_MAX;
                    break;
                case TYPE_ALLIANCE:
                    // alliance is relatively simple action, don't need a new page
                    showAllianceDialog();
                    return;
                case TYPE_DONE:
                    // pop up a dialog to ask confirm
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Finish this round");
                    builder.setMessage("once you finish this round, you can't undo it");
                    builder.setPositiveButton("Finish", (dialog, which) -> {
                        roundOver();
                    });
                    builder.setNegativeButton("Cancel", ((dialog, which) -> {
                        // do nothing
                    }));
                    builder.show();
                    return;
            }
            intent.putExtras(bundle);
            startActivityForResult(intent, requestCode);
        });

        tvPlayerInfo = findViewById(R.id.tv_player_info);
        tvPlayerInfo.setText("Please wait other players to finish assigning units...");

        tvRoundNum = findViewById(R.id.tv_round_number);
        tvRoundNum.setText(String.valueOf(roundNum));

        imgMap = findViewById(R.id.img_map);

        tvActionInfo = findViewById(R.id.tv_action_info);
        tvActionInfo.setMovementMethod(new ScrollingMovementMethod());

        showPerformedActions();
        setUpActionDropdown();
        setUpTerritoryList();
    }

    /**
     * Set up the recycler view of territory list.
     */
    private void setUpTerritoryList(){
        RecyclerView rvTerritoryList = findViewById(R.id.rv_territory_list);

        territoryAdapter = new TerritoryAdapter();
        territoryAdapter.setListener(position -> {
            showTerritoryDetailDialog(territoryAdapter.getTerritory(position));
        });

        rvTerritoryList.setLayoutManager(new LinearLayoutManager(PlayGameActivity.this));
        rvTerritoryList.setHasFixedSize(true);
        rvTerritoryList.setAdapter(territoryAdapter);
    }

    /**
     * Set up the action type drop down.
     */
    private void setUpActionDropdown(){
        TextInputLayout layout = findViewById(R.id.action_dropdown);
        layout.setHint("Action Type");

        List<String> actions = new ArrayList<>(Arrays.asList(TYPE_MOVE, TYPE_ATTACK, TYPE_UPGRADE_UNIT, TYPE_UPGRADE_MAX, TYPE_ALLIANCE, TYPE_DONE));
        actionAdapter =
                new ArrayAdapter<>(
                        PlayGameActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        actions);

        // set up default choice
        actionType = actionAdapter.getItem(0);

        AutoCompleteTextView dropdownAction = layout.findViewById(R.id.dd_input);
        dropdownAction.setAdapter(actionAdapter);
        dropdownAction.setText(actionAdapter.getItem(0), false);
        dropdownAction.setOnItemClickListener((parent, v, position, id) -> {
            actionType = actionAdapter.getItem(position);
        });
    }

    /**
     * Show the detail information of one territory.
     * @param territory target territory
     */
    private void showTerritoryDetailDialog(Territory territory){
        // generate the detail info of one territory
        StringBuilder detailInfo = new StringBuilder();
        detailInfo.append("Resource Info:\n");
        detailInfo.append("Food yield: ").append(territory.getFoodYield()).append("\n");
        detailInfo.append("Tech yield: ").append(territory.getTechYield()).append("\n");
        detailInfo.append("Units Info:\n");
        Map<Integer, List<Unit>> units = territory.getUnitGroup();
        if (units.isEmpty()){
            detailInfo.append("no units on this territory\n");
        }
        else {
            for (Map.Entry<Integer, List<Unit>> entry : units.entrySet()){
                detailInfo.append(entry.getValue().size()).append(" units with level ").append(entry.getKey()).append("\n");
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detail Info");
        builder.setMessage(detailInfo.toString());
        builder.show();
    }

    /**
     * Show a dialog to ask user for alliance information(aka only alliance id is enough).
     */
    private void showAllianceDialog(){
        LayoutInflater layoutInflater = getLayoutInflater();

        List<String> allianceName = new ArrayList<>();
        for (SPlayer p : allPlayers){
            if (!p.getName().equals(player.getName())){
                allianceName.add(p.getName());
            }
        }

        ArrayAdapter<String> adapterName =
                new ArrayAdapter<>(
                        PlayGameActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        allianceName);

        View view = layoutInflater.inflate(R.layout.view_form_alliance, null);
        // level
        TextInputLayout tlAlliance = view.findViewById(R.id.layout_alliance);
        AutoCompleteTextView dpAlliance = tlAlliance.findViewById(R.id.dd_input);
        tlAlliance.setHint("Alliance name");
        dpAlliance.setAdapter(adapterName);
        dpAlliance.setText(adapterName.getItem(0), false);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Form alliance");
        mBuilder.setView(view);
        mBuilder.setPositiveButton("Confirm", ((dialogInterface, i) -> {
            String name = dpAlliance.getText().toString();
            showToastUI(PlayGameActivity.this, "form alliance with " + name);
            for (SPlayer p : allPlayers){
                if (name.equals(p.getName())){
                    // construct and send the ally action
                    AllyAction action = new AllyAction(p.getId());
                    sendAction(action, new onResultListener() {
                        @Override
                        public void onFailure(String error) {
                            // either invalid action or networking problem
                            showToastUI(PlayGameActivity.this, error);
                            Log.e(TAG, "alliance fail: " + error);
                        }

                        @Override
                        public void onSuccessful() {
                            // valid action
                            performedActions.add(action);
                            showPerformedActions();
                        }
                    });
                }
            }
        }));
        mBuilder.setNegativeButton("Cancel", ((dialogInterface, i) -> {
        }));
        mBuilder.create().show();
    }

    /**
     * The function will be executed at the end of each round.
     */
    private void roundOver(){
        // disable all buttons
        setAllButtonClickable(false);
        send(ACTION_DONE, new onResultListener() {
            @Override
            public void onFailure(String error) {
                showToastUI(PlayGameActivity.this, NETWORK_PROBLEM);
                setAllButtonClickable(true);
            }

            @Override
            public void onSuccessful() {
                // successfully indicate we are finish, waiting for the attack result
                receiveAttackResult();
            }
        });
    }

    /**
     * Since the requirement require send the result of each attack action to *all* players,
     * we use a while loop to keep receiving all results until OVER
     */
    private void receiveAttackResult(){
        StringBuilder results = new StringBuilder();
        recvAttackResult(new onRecvAttackResultListener() {
            @Override
            public void onNewResult(String result) {
                runOnUiThread(() -> {
                    results.append(result).append("\n");
                    tvActionInfo.setText(results.toString());
                });
            }

            @Override
            public void onOver() {
                checkGameEnd();
            }

            @Override
            public void onFailure(String error) {
                showToastUI(PlayGameActivity.this, NETWORK_PROBLEM);
                runOnUiThread(() -> {
                    setAllButtonClickable(true);
                });
            }
        });
    }

    /**
     * This function should be called at the end of each round, to check whether the game is finished.
     */
    private void checkGameEnd(){
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                showToastUI(PlayGameActivity.this, NETWORK_PROBLEM);
                runOnUiThread(() -> {
                    setAllButtonClickable(true);
                });
            }

            @Override
            public void onSuccessful(Object object) {
                if (object instanceof String){
                    String result = (String) object;
                    if (result.equals(GAME_OVER)){
                        endGame();
                    }else {
                        newRound();
                    }
                }
            }
        });
    }

    /**
     * This function will be called each time user join the game(e.g. first time or reconnect to it).
     * It will receive the latest info from the server: roundInfo + player list
     */
    private void receiveLatestInfo(){
        // 1. receive player list
        // 2. receive new round info
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                showToastUI(PlayGameActivity.this, error);
                setAllButtonClickable(true);
            }

            @Override
            public void onSuccessful(Object object) {
                allPlayers = (ArrayList<SPlayer>) object;
                // only support alliance action for 3 or more players
                if (allPlayers.size() < 3){
                    actionAdapter.remove(TYPE_ALLIANCE);
                }
                newRound();
            }
        });
    }

    /**
     * This function will receive the new round info from server.
     */
    private void newRound(){
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                showToastUI(PlayGameActivity.this, error);
                setAllButtonClickable(true);
            }

            @Override
            public void onSuccessful(Object object) {
                if (object instanceof RoundInfo) {
                    System.out.println("recv round info obj");
                }
                else {
                    System.out.println("not round obj");
                }
                RoundInfo info = (RoundInfo) object;
                roundNum = info.getRoundNum();
                System.out.println(roundNum);
                map = info.getMap();
                player = info.getPlayer();
                setPlayerID(player.getId());
                // clear all actions in the last round
                performedActions.clear();
                showToastUI(PlayGameActivity.this, String.format(Locale.US,"start round %d", roundNum));
                checkLose();
                updateUI();
            }
        });
    }

    /**
     * This function will update all UI in play game page, should be called each time received the latest round info.
     */
    private void updateUI(){
        runOnUiThread(() -> {
            if (isLose){
                // is user lose, hide all button
                setAllButtonHidden();
                if (!hasShowDialog){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlayGameActivity.this);
                    builder.setPositiveButton("Yes", (dialog1, which) -> {
                        showToastUI(PlayGameActivity.this, "you lose");
                    });
                    builder.setNegativeButton("No", (dialog2, which) -> {
                        onBackPressed();
                    });
                    builder.setTitle("You Lose");
                    builder.setMessage("Do you want to keep watching the game?");
                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(dialog12 -> {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(
                                        getResources().getColor(R.color.colorPrimary)
                                );
                    });
                    dialog.show();
                    hasShowDialog = true;
                }
                // do nothing, just waiting for attack result
                receiveAttackResult();
            }else {
                // set all button clickable, let user input
                setAllButtonClickable(true);
            }
            // as long as the user is in the room, we should update these info
            // set the round number
            tvRoundNum.setText(String.valueOf(roundNum));
            // set the map image
            imgMap.setImageResource(MAP_NAME_TO_RESOURCE_ID.get(map.getName()));
            // update player info
            showPlayerInfo();
            // update territory list
            showTerritories();
        });
    }

    /**
     * Update the territory list based on the latest map.
     */
    private void showTerritories(){
        List<Territory> territories = new ArrayList<>();
        for (Map.Entry<String, Territory> entry : map.getAtlas().entrySet()){
            territories.add(entry.getValue());
        }
        territoryAdapter.setTerritories(territories);
    }

    private void showPlayerInfo(){
        StringBuilder builder = new StringBuilder();
        builder.append("Player ").append(player.getName())
                .append("(id: ").append(player.getId()).append(")")
                .append("   ").append("Max Tech Level: ").append(player.getTechLevel())
                .append("\n");
        builder.append("Food resource: ").append(player.getFoodNum())
                .append("; Tech resource: ").append(player.getTechNum())
                .append("\n");
        tvPlayerInfo.setText(builder);
    }

    /**
     * Set the clickable property of all buttons in this page.
     * @param isClickable is clickable or not
     */
    private void setAllButtonClickable(boolean isClickable){
        btPerform.setClickable(isClickable);
    }

    /**
     * Hide all button, call this function once the player is lose.
     */
    private void setAllButtonHidden(){
        btPerform.setVisibility(View.GONE);
    }

    /**
     * This function will format and show the list of actions user already successfully performed.
     */
    private void showPerformedActions(){
        StringBuilder builder = new StringBuilder();
        builder.append("Actions performed:\n");
        if (performedActions.isEmpty()){
            builder.append("no action performed for now");
        }else {
            int index = 1;
            for (Action action : performedActions){
                builder.append(index).append(". ").append(action.toString()).append("\n");
                index ++;
            }
        }
        tvActionInfo.setText(builder);
    }

    private void endGame(){
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "endGame" + error);
            }

            @Override
            public void onSuccessful(Object object) {
                String winnerInfo = (String) object;
                // dialog is a UI operation
                runOnUiThread(() -> {
                    // popup the game result and close the game after 3 seconds
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlayGameActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Result");
                    builder.setMessage(winnerInfo + "\n(this page will closed after 3 seconds)");
                    builder.show();

                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // kill this activity
                            finish();
                        }
                    }, 3000);
                });
            }
        });
    }

    private void checkLose(){
        List<Territory> territories = new ArrayList<>(map.getAtlas().values());
        for (Territory territory : territories){
            if (territory.getOwner() == getPlayerID()){
                isLose = false;
                return;
            }
        }
        isLose = true;
    }

    // probably want to extract this into constant
    private void goBack(){
        if (isLose){
            AlertDialog.Builder builder = new AlertDialog.Builder(PlayGameActivity.this);
            builder.setPositiveButton("Yes", (dialog1, which) -> {
                stopChatThread();
                onBackPressed();
            });
            builder.setNegativeButton("No", (dialog2, which) -> {
            });

            builder.setTitle("Do you want to leave the game?");
            builder.setMessage("Since you already lose, once you leave, you can't join this game again.");
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialog12 -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(
                                getResources().getColor(R.color.colorPrimary)
                        );
            });
            dialog.show();
        }else {
            stopChatThread();
            // if not lose, can go out and come back as you want
            onBackPressed();
        }
    }
}
