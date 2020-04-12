package edu.duke.ece651.riskclient.objects;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

public class Message implements IMessage {
    private int id;
    private IUser user;
    private String msg;
    private Date date;

    public Message(int id, IUser user, String msg) {
        this.id = id;
        this.user = user;
        this.msg = msg;
        this.date = new Date();
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public String getText() {
        return msg;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return date;
    }
}
