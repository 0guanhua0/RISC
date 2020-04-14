package edu.duke.ece651.riskclient.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.SMessage;
import edu.duke.ece651.risk.shared.player.SPlayer;
import edu.duke.ece651.riskclient.CustomIncomingMessageViewHolder;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onInsertListener;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.objects.Message;
import edu.duke.ece651.riskclient.objects.SimplePlayer;

import static edu.duke.ece651.riskclient.RiskApplication.getRoomID;
import static edu.duke.ece651.riskclient.RiskApplication.registerMsgListener;
import static edu.duke.ece651.riskclient.RiskApplication.sendChat;
import static edu.duke.ece651.riskclient.utils.SQLUtils.deleteMessagesAsy;
import static edu.duke.ece651.riskclient.utils.SQLUtils.getAllMessagesAsy;
import static edu.duke.ece651.riskclient.utils.SQLUtils.insertMessageAsy;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class ChatActivity extends AppCompatActivity
        implements MessagesListAdapter.OnLoadMoreListener,
        MessageInput.InputListener,
        MessagesListAdapter.SelectionListener{

    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final String EVERYONE = "Everyone";

    /**
     * UI variable for chat
     */
    protected MessagesListAdapter<Message> messagesAdapter;
    private MessagesList messagesList;

    /**
     * Variable
     */
    private Player<String> sender;
    private String toPlayerName;
    private List<String> playerNames;
    private Map<String, Integer> nameToID;

    private Menu menu;
    private int selectionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Chat Room");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        playerNames = new ArrayList<>();
        nameToID = new HashMap<>();
        nameToID.put(EVERYONE, -1);
        playerNames.add(EVERYONE);

        Bundle data = getIntent().getExtras();
        if (data != null){
            sender = (Player<String>) data.getSerializable(PlayGameActivity.DATA_CURRENT_PLAYER);
            ArrayList<SPlayer> players = (ArrayList<SPlayer>) data.get(PlayGameActivity.DATA_ALL_PLAYERS);
            if (players != null){
                for (SPlayer player : players){
                    // exclude sender himself
                    if (!player.getName().equals(sender.getName())){
                        playerNames.add(player.getName());
                        nameToID.put(player.getName(), player.getId());
                    }
                }
            }
        }

        setUpUI();
        // load history message
        getAllMessagesAsy(getRoomID(), new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "load history message: " + error);
            }

            @Override
            public void onSuccessful(Object object) {
                runOnUiThread(() -> {
                    messagesAdapter.addToEnd((List<Message>) object, false);
                });
            }
        });

        // register a message listener
        registerMsgListener(message -> runOnUiThread(() -> {
            messagesAdapter.addToStart(message, true);
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_activity_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_delete:
                deleteMessagesAsy(messagesAdapter.getSelectedMessages());
                messagesAdapter.deleteSelectedMessages();
                break;
            case R.id.action_copy:
                messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
                showToastUI(ChatActivity.this, "Message copied");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0){
            super.onBackPressed();
        }else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {

    }

    /**
     * This function will be called each time user click the send button.
     * @param input user input
     * @return true indicate send message successfully(will clear the input); false, nothing happen
     */
    @Override
    public boolean onSubmit(CharSequence input) {
        Message newMessage = new Message(0, getRoomID(), new SimplePlayer(sender.getId(), sender.getName()), input.toString());
        // insert into database
        insertMessageAsy(newMessage, id -> {
            // update message ID
            newMessage.setId(id);
            runOnUiThread(() -> {
                showToastUI(ChatActivity.this, input.toString());
                messagesAdapter.addToStart(newMessage, true);
            });
            // send the message to server
            SMessage message = new SMessage((int)id, sender.getId(), nameToID.get(toPlayerName), sender.getName(), input.toString());
            sendChat(message);
        });
        return true;
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
        menu.findItem(R.id.action_delete).setVisible(count > 0);
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    private void setUpUI(){
        messagesList = findViewById(R.id.messagesList);
        initAdapter();
        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);

        setUpToPlayer();
    }

    private void initAdapter() {
        // only override the incoming text message layout(add the sender name)
        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(
                        CustomIncomingMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message);

        // we use player name as the user ID of each message
        messagesAdapter = new MessagesListAdapter<>(sender.getName(), holdersConfig, null);
        messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.enableSelectionMode(this);
        messagesList.setAdapter(messagesAdapter);
    }

    /**
     * This function will setup the dropdown menu for "to player".
     */
    private void setUpToPlayer(){
        // setup drop down
        TextInputLayout layout = findViewById(R.id.dd_to_player);
        layout.setHint("To:");
        ArrayAdapter<String> toPlayerAdapter =
                new ArrayAdapter<>(
                        ChatActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        playerNames);

        toPlayerName = toPlayerAdapter.getItem(0);

        AutoCompleteTextView dropdownToPlayer = layout.findViewById(R.id.dd_input);
        dropdownToPlayer.setAdapter(toPlayerAdapter);
        dropdownToPlayer.setText(toPlayerName, false);
        dropdownToPlayer.setOnItemClickListener((parent, v, position, id) -> {
            toPlayerName = toPlayerAdapter.getItem(position);
        });
    }

    /**
     * Copyright, sample project from open source library ChatKit
     */
    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return new MessagesListAdapter.Formatter<Message>() {
            @Override
            public String format(Message message) {
                String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(message.getCreatedAt());

                String text = message.getText();
                if (text == null) text = "[attachment]";

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.getUser().getName(), text, createdAt);
            }
        };
    }
}
