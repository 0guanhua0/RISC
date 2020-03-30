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

    public Player(InputStream in, OutputStream out) throws IOException {
        this.territories = new HashSet<>();
        this.in = new ObjectInputStream(in);
        this.out = new ObjectOutputStream(out);
        this.id = -1;
    }


    //since only after first player communicating with server and selecting the map
    // can we get the color field for player, so color can't be a input field for first player
    public Player(int id, InputStream in, OutputStream out) throws IllegalArgumentException, IOException {
        if (id <= 0){
            throw new IllegalArgumentException("ID must large than 0.");
        }
        this.id = id;
        this.territories = new HashSet<>();
        this.in = new ObjectInputStream(in);
        this.out = new ObjectOutputStream(out);
    }

    //this constructor should be called for all players except for first player
    public Player(T color, int id, InputStream in, OutputStream out) throws IllegalArgumentException, IOException {
        if (id <= 0){
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

    public void setId(int id){
        if (id <= 0){
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

    public void addTerritory(Territory territory) throws IllegalArgumentException{
        if (!territory.isFree()){
            throw new IllegalArgumentException("You can not occupy an occupied territory");
        }
        territories.add(territory);
        territory.setOwner(this.id);
    }

    public void loseTerritory(Territory territory) throws IllegalArgumentException{
        if(!territories.contains(territory)){
            throw new IllegalArgumentException("the territory doesn't belong to this user!");
        }
        territories.remove(territory);
        territory.setIsFree(true);
    }

    public void send(Object data) throws IOException {
        out.writeObject(data);
        out.flush();
    }

    public Object recv() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    /**
     * This function will send the player info to corresponding client(in json format), now it includes:
     * 1) player id
     * 2) player color
     * @throws IOException probably because of stream closed
     */
    public void sendPlayerInfo() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PLAYER_ID, id);
        jsonObject.put(PLAYER_COLOR, color);
        send(jsonObject.toString());
    }

    public int getTerrNum(){
        return territories.size();
    }

    /**
     * this method is called to add the resource production of each territory
     * that they own at the end of the turn.
     */
    public abstract void updateResource();

    public abstract int getFoodNum();

    public abstract int getTechNum();

    public abstract void useFood(int foodUse);

    public abstract void useTech(int techUse);

}
