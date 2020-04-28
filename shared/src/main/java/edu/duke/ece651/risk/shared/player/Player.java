package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.map.Territory;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Transient;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static edu.duke.ece651.risk.shared.Constant.*;

/**
 * @program: risk
 * @description: this is the abstract player class
 * note that a valid user id should be positive, 0 is for unoccupied id
 * @author: Chengda Wu(cw402)
 * @create: 2020-03-09 16:24
 **/
@Embedded
public abstract class Player<T> implements Serializable {
    private static final long serialVersionUID = 16L;

    @Transient
    T color;
    int id;
    transient ObjectInputStream in;
    transient ObjectOutputStream out;
    transient ObjectInputStream chatIn;
    transient ObjectOutputStream chatOut;
    @Embedded
    Set<Territory> territories;

    //status mark connected / dis
    boolean isConnect;
    String name;

    //used only in evolution3
    //pay attention that in order to use the field below and make the state of territory remain legal
    //you need to clearly understand the definition of allyRequest:
    //the player id of target ally this player object receives from client side  during
    int allyRequest;
    @Transient
    List<Action> actions;
    boolean isSpying;

    @Transient
    Player ally;

    String allyName;

    public Player(InputStream in, OutputStream out) throws IOException {
        this.territories = new HashSet<>();
        this.in = new ObjectInputStream(in);
        this.out = new ObjectOutputStream(out);
        this.id = -1;
        this.isConnect = true;
        this.allyRequest = -1;
        this.ally = null;
        this.allyName = null;
        actions = new ArrayList<>();
        isSpying = false;
    }

    // since only after first player communicating with server and selecting the map
    // can we get the color field for player, so color can't be a input field for first player
    public Player(int id, InputStream in, OutputStream out) throws IllegalArgumentException, IOException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must large than 0.");
        }
        this.id = id;
        this.territories = new HashSet<>();
        this.in = new ObjectInputStream(in);
        this.out = new ObjectOutputStream(out);
        this.isConnect = true;
        this.allyRequest = -1;
        this.ally = null;
        actions = new ArrayList<>();
        isSpying = false;
    }

    //this constructor should be called for all players except for first player
    public Player(T color, int id, InputStream in, OutputStream out) throws IllegalArgumentException, IOException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must large than 0.");
        }
        this.color = color;
        this.id = id;
        this.territories = new HashSet<>();
        this.in = new ObjectInputStream(in);
        this.out = new ObjectOutputStream(out);
        this.isConnect = true;
        this.allyRequest = -1;
        this.ally = null;
        actions = new ArrayList<>();
        isSpying = false;
    }

    //constructor for mongo
    public Player() {

    }

    //get ally name
    public String getAllyName() {
        return allyName;
    }

    //set ally for reconstruct
    public void setAlly(Player ally) {
        this.ally = ally;
        this.allyName = ally.getName();
    }

    //getter for set of territory
    public Set<Territory> getTerritories() {
        return territories;
    }

    //setter for recover
    public void setTerritories(Set<Territory> territories) {
        this.territories = territories;
    }

    public int getId() {
        return id;
    }

    //set action
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must large than 0.");
        }
        this.id = id;
    }

    public T getColor() {
        return color;
    }

    public void setColor(T color) {
        this.color = color;
    }


    /**
     * actually, it's not a very good to implement it like so, actually, we shouldn't decouple setOwner and addTerritory
     * maybe change that in future commit
     *
     * @param territory: territory you want to add to this player
     * @throws IllegalArgumentException
     */
    public void addTerritory(Territory territory) throws IllegalArgumentException {
        if (!territory.isFree()) {
            throw new IllegalArgumentException("You can not occupy an occupied territory");
        }
        territories.add(territory);
        //based on current situation, the attack action will call setOwner directly
        //thus we should execute the following logic
        territory.setOwner(this.id);
    }

    public void loseTerritory(Territory territory) throws IllegalArgumentException {


        if (!territories.contains(territory)) {
            throw new IllegalArgumentException("the territory doesn't belong to this user!");
        }
        territories.remove(territory);
        territory.setFree();
    }

    public void send(Object data) {
        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException ignored) {
            this.setConnect(false);
        }
    }

    public Object recv() throws ClassNotFoundException {
        Object o = null;
        try {
            o = in.readObject();
        } catch (IOException ignored) {
            this.setConnect(false);
        }
        return o;
    }

    /**
     * Send a chat message to the player, use a separate socket.
     *
     * @param message simple message object
     */
    public void sendChatMessage(Object message) {
        try {
            chatOut.writeObject(message);
            chatOut.flush();
        } catch (IOException ignored) {
            // user disconnect from chat, not mean disconnect from game
            // this.setConnect(false);
        }
    }


    /**
     * Use the chat socket to receive a chat message from this player.
     *
     * @return ChatMessage
     */
    public Object recvChatMessage() {
        try {
            return chatIn.readObject();
        } catch (Exception ignored) {
            // user disconnect from chat, not mean disconnect from game
            // this.setConnect(false);
        }
        return null;
    }

    /**
     * This function will send the player info to corresponding client(in json format), now it includes:
     * 1) player id
     * 2) player color
     */
    public void sendPlayerInfo() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PLAYER_ID, id);
        jsonObject.put(PLAYER_COLOR, color);
        send(jsonObject.toString());
    }

    public int getTerrNum() {
        return territories.size();
    }

    public void setAllyRequest(int allyRequest) {
        if (this.allyRequest != -1) {
            throw new IllegalArgumentException("Invalid argument!");
        }
        this.allyRequest = allyRequest;
    }

    //reconstruct the ally request
    public void reAllyRequest(int allyRequest) {
        this.allyRequest = allyRequest;
    }


    public boolean hasRecvAlly() {
        return this.allyRequest != -1;
    }

    public boolean hasAlly() {
        return this.ally != null;
    }

    public boolean canAllyWith(Player p) {
        if (!this.hasAlly() && !p.hasAlly() && this.allyRequest == p.getId() && p.allyRequest == this.getId() && this.allyRequest != -1) {
            return true;
        } else {
            return false;
        }
    }

    public void allyWith(Player p) {
        if (!this.canAllyWith(p)) {
            throw new IllegalArgumentException("Invalid argument");
        }
        this.ally = p;
        this.allyName = p.name;
        p.ally = this;
        p.allyName = this.name;
        this.setTerrAlly();
        p.setTerrAlly();
    }

    public boolean isAllyWith(Player p) {
        return this.ally == p;
    }


    public int getAllyRequest() {
        return allyRequest;
    }

    public void setTerrAlly() {
        for (Territory territory : this.territories) {
            territory.setAlly(this.ally);
        }
    }


    public Player getAlly() {
        return ally;
    }

    /**
     * rupture of alliance between this player and her ally
     */
    public void ruptureAlly() {
        if (hasAlly()) {
//            assert(ally.ally==this);//used only for debugging
            //change the state of all territories
            for (Territory territory : this.territories) {
                territory.ruptureAlly();
            }
            for (Object o : this.ally.territories) {
                Territory territory = (Territory) o;
                territory.ruptureAlly();
            }
            this.ally.allyName = null;
            this.ally.ally = null;
            this.ally.allyRequest = -1;
            this.allyRequest = -1;
            this.ally = null;
            this.allyName = null;
        } else {
            throw new IllegalStateException("trying to rupture an not existed alliance");
        }
    }

    public void addAction(Action action) {
        this.actions.add(action);
    }

    public void setIsSpying() {
        if (isSpying) {
            throw new IllegalStateException("Invalid state");
        }
        this.isSpying = true;
    }

    public boolean isSpying() {
        return this.isSpying;
    }

    public boolean canAffordSpy() {
        return this.getTechNum() >= SPY_COST;
    }

    public List<Action> getActions() {
        return this.actions;
    }

    /**
     * this method is called to add the resource production of each territory
     * that they own at the end of the turn.
     */
    public abstract void updateState();

    public abstract int getFoodNum();

    public abstract int getTechNum();

    public abstract void useFood(int foodUse);

    public abstract void useTech(int techUse);

    public abstract boolean canUpMaxTech();

    public abstract void upMaxTech();

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    /**
     * This function will update the chat stream of current user
     *
     * @param in  chat in stream
     * @param out chat out stream
     */
    public void setChatStream(ObjectInputStream in, ObjectOutputStream out) {
        this.chatIn = in;
        this.chatOut = out;
    }

    /**
     * reset connection handle connection
     */
    //todo: redirect the round info to a log file
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract int getTechLevel();


}
