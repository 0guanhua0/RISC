package edu.duke.ece651.riskclient.activity;

import android.os.Bundle;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import edu.duke.ece651.risk.shared.player.SMessage;
import edu.duke.ece651.risk.shared.player.SPlayer;
import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.listener.onReceiveListener;
import edu.duke.ece651.riskclient.objects.Message;
import edu.duke.ece651.riskclient.objects.SimplePlayer;

import static edu.duke.ece651.riskclient.RiskApplication.getRoomID;
import static edu.duke.ece651.riskclient.RiskApplication.sendChat;
import static edu.duke.ece651.riskclient.utils.HTTPUtils.listenChatMessage;
import static edu.duke.ece651.riskclient.utils.SQLUtils.getAllMessages;
import static edu.duke.ece651.riskclient.utils.SQLUtils.insertMessage;
import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class ChatActivity extends AppCompatActivity
        implements MessagesListAdapter.OnMessageLongClickListener<Message>,
        MessagesListAdapter.OnLoadMoreListener,
        MessageInput.InputListener {

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
    private List<String> playerName;
    private Map<String, Integer> nameToID;

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

        playerName = new ArrayList<>();
        nameToID = new HashMap<>();
        nameToID.put(EVERYONE, -1);
        playerName.add(EVERYONE);
        try {
            sender = new PlayerV1<>("a", 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle data = getIntent().getExtras();
        if (data != null){
            sender = (Player<String>) data.getSerializable(PlayGameActivity.DATA_CURRENT_PLAYER);
            ArrayList<SPlayer> players = (ArrayList<SPlayer>) data.get(PlayGameActivity.DATA_ALL_PLAYERS);
            if (players != null){
                for (SPlayer player : players){
                    playerName.add(player.getName());
                    nameToID.put(player.getName(), player.getId());
                }
            }
        }

        setUpUI();

        // register a chat listener, keep listening for incoming message
        listenChatMessage(new onReceiveListener() {
            @Override
            public void onFailure(String error) {
                Log.e(TAG, "recv chat error: " + error);
            }

            @Override
            public void onSuccessful(Object object) {
                if (object instanceof Message){
                    Message message = (Message) object;
                    // insert into database
                    insertMessage(message);
                    // refresh UI
                    runOnUiThread(() -> {
                        messagesAdapter.addToStart(message, true);
                    });
                }
            }
        });

        // load history data
        getAllMessages(getRoomID(), new onReceiveListener() {
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

    @Override
    public void onMessageLongClick(Message message) {

    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {

    }

    @Override
    public boolean onSubmit(CharSequence input) {
        Message newMessage = new Message(0, getRoomID(), new SimplePlayer(sender.getId(), sender.getName()), input.toString());
        // insert into database
        insertMessage(newMessage);
        showToastUI(ChatActivity.this, input.toString());
        messagesAdapter.addToStart(newMessage, true);
        // send the message to server
        SMessage message = new SMessage(0, sender.getId(), -1, sender.getName(), input.toString());
        sendChat(message);
//        boolean b = new Random().nextBoolean();
//        if (b){
//            messagesAdapter.addToStart(new Message(1, new SimplePlayer(1, "xkw"), input.toString()), true);
//        }else {
//            messagesAdapter.addToStart(new Message(2, new SimplePlayer(2, "xkx"), input.toString()), true);
//        }
        return true;
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
                .setIncomingTextLayout(R.layout.item_custom_incoming_text_message);

        messagesAdapter = new MessagesListAdapter<>(String.valueOf(sender.getId()), holdersConfig, null);
        messagesAdapter.setOnMessageLongClickListener(this);
        messagesAdapter.setLoadMoreListener(this);
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
                        playerName);

        toPlayerName = toPlayerAdapter.getItem(0);

        AutoCompleteTextView dropdownToPlayer = layout.findViewById(R.id.dd_input);
        dropdownToPlayer.setAdapter(toPlayerAdapter);
        dropdownToPlayer.setText(toPlayerName, false);
        dropdownToPlayer.setOnItemClickListener((parent, v, position, id) -> {
            toPlayerName = toPlayerAdapter.getItem(position);
        });
    }
}
