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
import java.util.HashMap;
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
import edu.duke.ece651.riskclient.listener.onRecvInfoListener;
import edu.duke.ece651.riskclient.listener.onResultListener;

import static edu.duke.ece651.risk.shared.Constant.ACTION_DONE;
import static edu.duke.ece651.risk.shared.Constant.GAME_OVER;
import static edu.duke.ece651.risk.shared.Constant.RADIATE_LEVEL;
import static edu.duke.ece651.risk.shared.Constant.SPY_COST;
import static edu.duke.ece651.riskclient.Constant.ACTION_PERFORMED;
import static edu.duke.ece651.riskclient.Constant.MAP_NAME_TO_RESOURCE_ID;
import static edu.duke.ece651.riskclient.RiskApplication.getPlayerID;
import static edu.duke.ece651.riskclient.RiskApplication.getRoomName;
import static edu.duke.ece651.riskclient.RiskApplication.isAudience;
import static edu.duke.ece651.riskclient.RiskApplication.recv;
import static edu.duke.ece651.riskclient.RiskApplication.send;
import static edu.duke.ece651.riskclient.RiskApplication.setPlayerID;
import static edu.duke.ece651.riskclient.RiskApplication.startChatThread;
import static edu.duke.ece651.riskclient.RiskApplication.stopChatThread;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.recvActionInfo;
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
    private static final String TYPE_SPY = "spy";
    private static final String TYPE_RADIATION = "radiation";
    private static final String TYPE_DONE = "done";

    private static final int REQUEST_ACTION_MOVE = 1;
    private static final int REQUEST_ACTION_ATTACK = 2;
    private static final int REQUEST_ACTION_UPGRADE_MAX = 3;
    private static final int REQUEST_ACTION_UPGRADE_UNIT = 4;
    private static final int REQUEST_ACTION_ALLIANCE = 5;
    private static final int REQUEST_ACTION_SPY = 6;
    private static final int REQUEST_ACTION_RADIATION = 7;

    /**
     * UI variable
     */
    private TextView tvRoundNum;
    private TextView tvPlayerInfo;
    private TextView tvAllyInfo;
    private TextView tvActionInfo;
    private Button btPerform;
    private ImageView imgMap;
    private TextInputLayout tiActionDrop;

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
        // these two functions use two separate sockets, can perform them in parallel
        // receive player + round info
        receiveLatestInfo();
        // only player can chat
        if (!isAudience()){
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_game_menu, menu);
        if (isAudience()){
            menu.findItem(R.id.chat_room).setVisible(false);
        }else {
            menu.findItem(R.id.chat_room).setVisible(true);
        }
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
            case REQUEST_ACTION_RADIATION:
                if (resultCode == RESULT_OK){
                    Action action = (Action) data.getSerializableExtra(ACTION_PERFORMED);
                    if (action != null){
                        // server check that the action is valid, so we perform it to update current map
                        // NOTE: this only update the copy of the map, we will still get the latest map from server at the beginning of each term
                        action.perform(new WorldState(player, map));
                        performedActions.add(action);
                        territoryAdapter.notifyDataSetChanged();
                        showPerformedActions();
                        showPlayerInfo();
                    }
                }
                break;
            case REQUEST_ACTION_SPY:
                if (resultCode == RESULT_OK){
                    Action action = (Action) data.getSerializableExtra(ACTION_PERFORMED);
                    if (action != null){
                        // since we can't perform spy action on the client side, we simply subtract the tech resource
                        player.useTech(SPY_COST);
                        player.setIsSpying();
                        performedActions.add(action);
                        showPerformedActions();
                        showPlayerInfo();
                    }
                }
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
                case TYPE_ATTACK:
                    intent.setComponent(new ComponentName(PlayGameActivity.this, MoveAttackActivity.class));
                    bundle.putBoolean(DATA_IS_MOVE, actionType.equals(TYPE_MOVE));
                    bundle.putSerializable(DATA_PLAYING_MAP, map);
                    bundle.putSerializable(DATA_CURRENT_PLAYER, player);
                    bundle.putInt(DATA_FOOD_RESOURCE, player.getFoodNum());
                    requestCode = actionType.equals(TYPE_MOVE) ? REQUEST_ACTION_MOVE : REQUEST_ACTION_ATTACK;
                    break;
                case TYPE_UPGRADE_UNIT:
                case TYPE_UPGRADE_MAX:
                    intent.setComponent(new ComponentName(PlayGameActivity.this, UpgradeActivity.class));
                    bundle.putBoolean(DATA_IS_UPGRADE_MAX, actionType.equals(TYPE_UPGRADE_MAX));
                    bundle.putSerializable(DATA_PLAYING_MAP, map);
                    bundle.putInt(DATA_TECH_RESOURCE, player.getTechNum());
                    bundle.putInt(DATA_CURRENT_TECH_LEVEL, player.getTechLevel());
                    requestCode = actionType.equals(TYPE_UPGRADE_MAX) ? REQUEST_ACTION_UPGRADE_MAX : REQUEST_ACTION_UPGRADE_UNIT;
                    break;
                case TYPE_ALLIANCE:
                    // alliance is relatively simple action, don't need a new page
                    showAllianceDialog();
                return;
                case TYPE_SPY:
                    intent.setComponent(new ComponentName(PlayGameActivity.this, SpyActivity.class));
                    bundle.putSerializable(DATA_CURRENT_PLAYER, player);
                    bundle.putSerializable(DATA_ALL_PLAYERS, allPlayers);
                    bundle.putInt(DATA_TECH_RESOURCE, player.getTechNum());
                    requestCode = REQUEST_ACTION_SPY;
                    break;
                case TYPE_RADIATION:
                    intent.setComponent(new ComponentName(PlayGameActivity.this, RadiateActivity.class));
                    bundle.putSerializable(DATA_CURRENT_PLAYER, player);
                    bundle.putSerializable(DATA_PLAYING_MAP, map);
                    bundle.putInt(DATA_TECH_RESOURCE, player.getTechNum());
                    requestCode = REQUEST_ACTION_RADIATION;
                    break;
                case TYPE_DONE:
                    showDoneActionDialog();
                    return;
            }
            intent.putExtras(bundle);
            startActivityForResult(intent, requestCode);
        });

        tvPlayerInfo = findViewById(R.id.tv_player_info);
        tvPlayerInfo.setText("Please wait other players to finish assigning units...");

        tvAllyInfo = findViewById(R.id.tv_ally_info);
        tvAllyInfo.setText("");

        tvRoundNum = findViewById(R.id.tv_round_number);
        tvRoundNum.setText(String.valueOf(roundNum));

        imgMap = findViewById(R.id.img_map);

        tvActionInfo = findViewById(R.id.tv_action_info);
        tvActionInfo.setMovementMethod(new ScrollingMovementMethod());

        setUpTerritoryList();

        if (isAudience()){
            btPerform.setVisibility(View.GONE);
            tvPlayerInfo.setVisibility(View.GONE);
            tvAllyInfo.setVisibility(View.GONE);
        }else {
            showPerformedActions();
            // only show action drop down when current user is a player
            setUpActionDropdown();
        }
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
     * Update the territory list based on the latest map.
     */
    private void updateTerritories(){
        List<Territory> territories = new ArrayList<>();
        for (Map.Entry<String, Territory> entry : map.getAtlas().entrySet()){
            territories.add(entry.getValue());
        }
        territoryAdapter.setTerritories(territories);
    }

    /**
     * This function will be called only at each time user join the game(e.g. first time or reconnect to it).
     * It will receive the latest info from the server: player list + roundInfo
     */
    private void receiveLatestInfo(){
        // 1. receive player list
        // 2. receive new round info
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "receiveLatestInfo: " + error);
                showToastUI(PlayGameActivity.this, error);
                setAllButtonClickable(true);
            }

            @Override
            public void onSuccessful(Object object) {
                if (object instanceof ArrayList){
                    allPlayers = (ArrayList<SPlayer>) object;
                    Map<Integer, String> idToName = new HashMap<>();
                    for (SPlayer sPlayer : allPlayers){
                        idToName.put(sPlayer.getId(), sPlayer.getName());
                    }
                    territoryAdapter.setIdToName(idToName);
                    // only support alliance action for 3 or more players
                    // TODO: uncomment this before release
//                if (allPlayers.size() < 3){
//                    actionAdapter.remove(TYPE_ALLIANCE);
//                }
                    newRound();
                }else {
                    Log.e(TAG, "receiveLatestInfo expects ArrayList but is " + object);
                    // keep receiving in-case we receive some out-dated data
                    receiveLatestInfo();
                }
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
                Log.e(TAG, "newRound: " + error);
                showToastUI(PlayGameActivity.this, error);
                setAllButtonClickable(true);
            }

            @Override
            public void onSuccessful(Object object) {
                if (object instanceof RoundInfo){
                    RoundInfo info = (RoundInfo) object;
                    roundNum = info.getRoundNum();
                    map = info.getMap();
                    player = info.getPlayer();

                    if (!isAudience()){
                        setPlayerID(player.getId());
                        if (player.getTechLevel() >= RADIATE_LEVEL){
                            actionAdapter.remove(TYPE_RADIATION);
                            actionAdapter.add(TYPE_RADIATION);
                        }
                        // clear all actions in the last round
                        performedActions.clear();
                        checkLose();
                    }
                    showToastUI(PlayGameActivity.this, String.format(Locale.US,"start round %d", roundNum));
                    updateUI();
                }else {
                    Log.e(TAG, "newRound expects RoundInfo but is " + object);
                }
            }
        });
    }

    /**
     * This function will update all UI in play game page, should be called at the beginning of each round.
     * (i.e. each time receive the round info)
     */
    private void updateUI(){
        runOnUiThread(() -> {
            // first decide whether current user is a player or audience
            if (isAudience()){
                hideAllInput();
                receiveActionInfo();
            }else {
                // current user is a player
                if (isLose){
                    // is user lose, hide all inputs
                    hideAllInput();
                    if (!hasShowDialog){
                        showLoseDialog();
                        hasShowDialog = true;
                    }
                    // do nothing, just waiting for attack result
                    receiveAttackResult();
                }else {
                    // otherwise, set all button clickable
                    setAllButtonClickable(true);
                }
                // as long as the user is in the room, we should update these info
                // set the round number
                tvRoundNum.setText(String.valueOf(roundNum));
                // set the map image
                imgMap.setImageResource(MAP_NAME_TO_RESOURCE_ID.get(map.getName()));
                // only update player info when this user is player
                showPlayerInfo();
            }
            // update territory list
            updateTerritories();
        });
    }

    /**
     * Since the requirement require send the result of each attack action to *all* players,
     * we use a while loop to keep receiving all results until OVER
     */
    private void receiveAttackResult(){
        tvActionInfo.append("Attack result:\n");
        recvAttackResult(new onRecvInfoListener() {
            @Override
            public void onNewResult(String result) {
                runOnUiThread(() -> {
                    tvActionInfo.append(String.format("%s\n", result));
                });
            }

            @Override
            public void onOver() {
                // server has resolved all combats, go check whether the game is finish
                checkGameEnd();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "receiveAttackResult: " + error);
                showToastUI(PlayGameActivity.this, error);
                setAllButtonClickable(true);
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
                Log.e(TAG, "checkGameEnd: " + error);
                showToastUI(PlayGameActivity.this, error);
                setAllButtonClickable(true);
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
                }else {
                    Log.e(TAG, "checkGameEnd expects String but is " + object);
                }
            }
        });
    }

    /**
     * Hide all input(button & dropdown), call this function once the player is lose(or is audience).
     */
    private void hideAllInput(){
        btPerform.setVisibility(View.GONE);
        tiActionDrop.setVisibility(View.GONE);
    }

    private void endGame(){
        recv(new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "endGame" + error);
                showToastUI(PlayGameActivity.this, error);
            }

            @Override
            public void onSuccessful(Object object) {
                if (object instanceof String){
                    String winnerInfo = (String) object;
                    // dialog is a UI operation
                    runOnUiThread(() -> {
                        showEndGameDialog(winnerInfo);
                    });
                }else {
                    Log.e(TAG, "endGame expects String but is " + object);
                }
            }
        });
    }

    private void goBack(){
        if (isLose){
            showLeaveRoomDialog();
        }else {
            stopChatThread();
            // if not lose, can go out and come back as you want
            onBackPressed();
        }
    }

    /* ====== functions only for audience ====== */

    /**
     * This function will receive the performed action info of all players.
     */
    private void receiveActionInfo(){
        tvActionInfo.append(String.format(Locale.US, "** round %d **", roundNum));
        recvActionInfo(new onRecvInfoListener() {
            @Override
            public void onNewResult(String result) {
                runOnUiThread(() -> {
                    tvActionInfo.append(String.format("%s\n", result));
                });
            }

            @Override
            public void onOver() {
                // all players finish their round, go to receive the attack result
                receiveAttackResult();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "receiveActionInfo: " + error);
                showToastUI(PlayGameActivity.this, error);
            }
        });
    }

    /* ====== end ====== */

    /* ====== functions only for player(not audience) ====== */

    /**
     * Set up the action type drop down.
     */
    private void setUpActionDropdown(){
        tiActionDrop = findViewById(R.id.action_dropdown);
        tiActionDrop.setVisibility(View.VISIBLE);
        tiActionDrop.setHint("Action Type");

        List<String> actions = new ArrayList<>(Arrays.asList(TYPE_MOVE, TYPE_ATTACK, TYPE_UPGRADE_UNIT, TYPE_UPGRADE_MAX, TYPE_ALLIANCE, TYPE_SPY, TYPE_DONE));
        actionAdapter =
                new ArrayAdapter<>(
                        PlayGameActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        actions);

        // set up default choice
        actionType = actionAdapter.getItem(0);

        AutoCompleteTextView dropdownAction = tiActionDrop.findViewById(R.id.dd_input);
        dropdownAction.setAdapter(actionAdapter);
        dropdownAction.setText(actionAdapter.getItem(0), false);
        dropdownAction.setOnItemClickListener((parent, v, position, id) -> {
            actionType = actionAdapter.getItem(position);
        });
    }

    /**
     * This function will show the player info(e.g. max tech level, resource, ally).
     */
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
        // ally info
        if (player.getAlly() == null){
            tvAllyInfo.setText("Allying with \"no body yet\"");
        }else {
            tvAllyInfo.setText(String.format("Allying with %s", player.getAlly().getName()));
        }
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

    /**
     * Set the clickable property of all buttons in this page.
     * @param isClickable is clickable or not
     */
    private void setAllButtonClickable(boolean isClickable){
        btPerform.setClickable(isClickable);
    }

    /**
     * The function will be executed at the end of each round(after user click DONE).
     */
    private void roundOver(){
        // disable all buttons
        setAllButtonClickable(false);
        send(ACTION_DONE, new onResultListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "roundOver: " + error);
                showToastUI(PlayGameActivity.this, error);
                setAllButtonClickable(true);
            }

            @Override
            public void onSuccessful() {
                showToastUI(PlayGameActivity.this, String.format(Locale.US, "finish round %d", roundNum));
                // successfully finish this round, wait for the attack result
                receiveAttackResult();
            }
        });
    }

    /**
     * Check whether a player is lose.
     */
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

    /* ====== end ====== */

    /* ====== different kinds of dialogs ====== */

    /**
     * Show a dialog to ask user for confirmation of DONE.
     */
    private void showDoneActionDialog(){
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
    }

    /**
     * Show the detail information of one territory.
     * @param territory target territory
     */
    private void showTerritoryDetailDialog(Territory territory){
        // generate the detail info of one territory
        StringBuilder detailInfo = new StringBuilder();
        detailInfo.append("Resource Info:\n");
        if (territory.isRadiated()){
            detailInfo.append("Food yield: 0\n");
            detailInfo.append("Tech yield: 0\n");
        }else {
            detailInfo.append("Food yield: ").append(territory.getFoodYield()).append("\n");
            detailInfo.append("Tech yield: ").append(territory.getTechYield()).append("\n");
        }
        detailInfo.append("Owner Units Info:\n");
        Map<Integer, List<Unit>> units = territory.getUnitGroup();
        if (units.isEmpty()){
            detailInfo.append("no units on this territory\n");
        }
        else {
            for (Map.Entry<Integer, List<Unit>> entry : units.entrySet()){
                detailInfo.append(entry.getValue().size()).append(" units with level ").append(entry.getKey()).append("\n");
            }
        }

        detailInfo.append("Ally Units Info:\n");
        Map<Integer, List<Unit>> allyUnits = territory.getAllyUnitGroup();
        if (allyUnits.isEmpty()){
            detailInfo.append("no ally units on this territory\n");
        }
        else {
            for (Map.Entry<Integer, List<Unit>> entry : allyUnits.entrySet()){
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
            showToastUI(PlayGameActivity.this, "make a request to form alliance with " + name);
            for (SPlayer p : allPlayers){
                if (name.equals(p.getName())){
                    // construct and send the ally action
                    AllyAction action = new AllyAction(p.getId(), p.getName());
                    sendAction(action, new onResultListener() {
                        @Override
                        public void onFailure(String error) {
                            // either invalid action or networking problem
                            showToastUI(PlayGameActivity.this, error);
                            Log.e(TAG, "alliance fail: " + error);
                        }

                        @Override
                        public void onSuccessful() {
                            runOnUiThread(() -> {
                                // valid action
                                performedActions.add(action);
                                showPerformedActions();
                            });
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
     * Show a dialog to tell the player he/she is lose, and ask whether keep watching.
     */
    private void showLoseDialog(){
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
    }

    /**
     * Show a dialog to display the winner info and then go back to home page.
     * @param winnerInfo winner info
     */
    private void showEndGameDialog(String winnerInfo){
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
    }

    /**
     * Show a dialog to ask user for confirmation of leave room.
     */
    private void showLeaveRoomDialog(){
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
    }
}
