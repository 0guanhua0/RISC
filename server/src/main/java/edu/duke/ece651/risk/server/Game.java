package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.network.Server;

import java.io.IOException;
import java.net.Socket;

public class Game {
    Server connect;
    private Socket socket;
    private String data;

    public Game() throws IOException {
        connect = new Server();
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game();
        game.acceptSocket();
        game.sendSocket();
    }

    public void sendSocket() throws IOException {
        Server.send(socket, data);
    }

    public void acceptSocket() {
        connect.accept();
    }
}
