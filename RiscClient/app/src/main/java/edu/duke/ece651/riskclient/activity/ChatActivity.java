package edu.duke.ece651.riskclient.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.objects.Message;
import edu.duke.ece651.riskclient.objects.SimplePlayer;

import static edu.duke.ece651.riskclient.utils.UIUtils.showToastUI;

public class ChatActivity extends AppCompatActivity implements MessagesListAdapter.OnMessageLongClickListener<Message>, MessagesListAdapter.OnLoadMoreListener, MessageInput.InputListener {

    private static final String EVERYONE = "Everyone";

    /**
     * UI variable for chat
     */
    protected MessagesListAdapter<Message> messagesAdapter;
    private MessagesList messagesList;

    /**
     * Variable
     */
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
            getSupportActionBar().setTitle("Chat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // TODO: pass in a list of player and extract the player name
        playerName = new ArrayList<>();
        nameToID = new HashMap<>();
        nameToID.put(EVERYONE, -1);
        playerName.add(EVERYONE);

        setUpUI();
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
        showToastUI(ChatActivity.this, input.toString());
        boolean b = new Random().nextBoolean();
        if (b){
            messagesAdapter.addToStart(new Message(1, new SimplePlayer(1, "xkw", "xkw"), input.toString()), true);
        }else {
            messagesAdapter.addToStart(new Message(2, new SimplePlayer(2, "xkx", "xkw"), input.toString()), true);
        }
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
        // TODO: modify the sender id here
        messagesAdapter = new MessagesListAdapter<>("1", holdersConfig, null);
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
