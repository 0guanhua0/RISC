package edu.duke.ece651.risk.shared.player;

import java.io.Serializable;
import java.util.Date;

/**
 * Simple message, use in chat function.
 */
// TODO: we should also serialize this object into the database
public class SMessage implements Serializable {
    // id of current message, generate by the server
    private int id;
    // id of the sender of this message
    private int senderID;
    // id of the receiver of this message(-1 represent broadcast to everyone)
    private int receiverID;
    // the name of the author(sender) of this message
    private String from;
    // message content
    private String message;
    // the creation date of this message
    private Date date;

    public SMessage(int id, int senderID, int receiverID, String from, String message) {
        this.id = id;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.from = from;
        this.message = message;
        this.date = new Date();
    }

    public int getReceiverID() {
        return receiverID;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("%s send: %s to %d", from, message, receiverID);
    }
}
