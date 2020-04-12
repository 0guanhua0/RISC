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
    // the name of the author(sender) of this message
    private String from;
    // message content
    private String message;
    // the creation date of this message
    private Date date;
}
