package edu.duke.ece651.risk.shared.player;

import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.network.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

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
    Socket socket;
    Set<Territory> territories;

    public Player(T color, int id, Socket socket){
        if (id <= 0){
            throw new IllegalArgumentException("ID must large than 0.");
        }
        this.color = color;
        this.id = id;
        this.territories = new HashSet<>();
        this.socket = socket;
    }

    public void addTerritory(Territory territory){
        territories.add(territory);
        territory.setOwner(this.id);
    }

    public void loseTerritory(Territory territory){
        if(!territories.contains(territory)){
            throw new IllegalArgumentException("there territory doesn't belong to this user!");
        }
        territories.remove(territory);
        territory.setIsFree(true);
    }

    public void send(String data) throws IOException {
        if (socket != null){
            Server.send(socket, data);
        }
    }

    public String recv() throws IOException {
        if (socket != null){
            return Server.recvStr(socket);
        }
        return "";
    }
}
