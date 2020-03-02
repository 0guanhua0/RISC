package edu.duke.ece651;

/**
 * The data structure to be sent from client to server.
 * Represent the action user specify.
 */
public class Action {
    String actionType;
    String src;
    String dest;

    public Action(){}

    public Action(String actionType, String src, String dest){
        this.actionType = actionType;
        this.src = src;
        this.dest = dest;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }
}
