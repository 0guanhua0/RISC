package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.map.Territory;
import org.json.JSONObject;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static edu.duke.ece651.risk.shared.Constant.PLAYER_COLOR;
import static edu.duke.ece651.risk.shared.Constant.PLAYER_ID;

/**
 * @program: risk
 * @description: this is the abstract player class
 * note that a valid user id should be positive, 0 is for unoccupied id
 * @author: Chengda Wu(cw402)
 * @create: 2020-03-09 16:24
 **/

public abstract class Player<T> {

    T color;
    int id;
    ObjectInputStream in;
    ObjectOutputStream out;
    Set<Territory> territories;

    //status mark connected / dis
    private boolean isConnect;
    private String name;

    public Player(InputStream in, OutputStream out) throws IOException {
        this.territories = new HashSet<>();
        this.in = new ObjectInputStream(in);
        this.out = new ObjectOutputStream(out);
        this.id = -1;

        this.isConnect = true;
    }


    //since only after first player communicating with server and selecting the map
    // can we get the color field for player, so color can't be a input field for first player
    public Player(int id, InputStream in, OutputStream out) throws IllegalArgumentException, IOException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must large than 0.");
        }
        this.id = id;
        this.territories = new HashSet<>();
        this.in = new ObjectInputStream(in);
        this.out = new ObjectOutputStream(out);
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
    }

    public int getId() {
        return id;
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

    public void addTerritory(Territory territory) throws IllegalArgumentException {
        if (!territory.isFree()) {
            throw new IllegalArgumentException("You can not occupy an occupied territory");
        }
        territories.add(territory);
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
            System.out.println(ignored.toString());
            this.setConnect(false);
        }
    }

    public Object recv() throws ClassNotFoundException {
        Object o = new Object();
        try {
            o =  in.readObject();
        }
        catch (IOException ignored) {
            this.setConnect(false);
        }
        return o;
    }

    /**
     * This function will send the player info to corresponding client(in json format), now it includes:
     * 1) player id
     * 2) player color
     *
     * @throws IOException probably because of stream closed
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
