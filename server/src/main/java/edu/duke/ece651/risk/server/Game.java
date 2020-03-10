package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.Server;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;

import java.io.IOException;
import java.net.Socket;

public class Game {
    Server connect;
    Socket socket;

    public Game() throws IOException {
        connect = new Server(8080);
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game();
        game.acceptSocket();
        Server.send(game.socket, "Hello");//new MapDataBase().getMap("a clash of kings"));
    }



    public void acceptSocket() {
        socket = connect.accept();
    }
}
