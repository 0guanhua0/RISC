package edu.duke.ece651.riskclient.chat;

import android.view.View;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageHolders;

import edu.duke.ece651.riskclient.R;
import edu.duke.ece651.riskclient.objects.Message;

public class CustomIncomingMessageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<Message> {

    private TextView tvSenderName;

    public CustomIncomingMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        tvSenderName = itemView.findViewById(R.id.tv_sender_name);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);

        tvSenderName.setText(message.getUser().getName());
    }

    public static class Payload {

    }
}
