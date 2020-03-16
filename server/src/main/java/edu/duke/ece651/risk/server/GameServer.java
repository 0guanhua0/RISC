package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.network.Server;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.util.Map;
import java.util.concurrent.*;

public class GameServer {
    // the server object, use to communicate with all players
    Server server;
    // thread pool, used to handle incoming request
    ThreadPoolExecutor threadPool;
    // list of all rooms(each room represent a running game)
    Map<Integer, RoomController> rooms;

    public GameServer(Server server) {
        this.server = server;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(32);
        this.threadPool = new ThreadPoolExecutor(4, 16, 5, TimeUnit.SECONDS, workQueue);
        this.rooms = new ConcurrentHashMap<>();
    }

    /**
     * This will run forever(until the thread is killed), keep listen for new connection and handle it.
     */
    public void run(){
        while (!Thread.currentThread().isInterrupted()){
            Socket socket = server.accept();
            if (socket != null){
                threadPool.execute(()-> {
                    try {
                        handleIncomeRequest(socket);
                    } catch (IOException | ClassNotFoundException e) {
                        // IO Exception, probably a bette way is write to log file
                    }
                });
            }
        }
    }

    /**
     * This function is used to handle each incoming request.
     * 1. ask the player whether he/she want to start a new room or join an existing room
     * 2. either create a new RoomController or fetch an existing one
     * 3. pass the socket(i.e player) to corresponding "room"
     * @param socket represent a newly accept player
     */
    void handleIncomeRequest(Socket socket) throws IOException, ClassNotFoundException {
        // here we wrap the socket with player object ASAP(i.e. decouple socket with stream)
        PlayerV1<String> player = new PlayerV1<>(socket.getInputStream(), socket.getOutputStream());

        String helloInfo = "Welcome to the fancy RISK game!!!";
        player.send(helloInfo);

        int choice = askValidRoomNum(player);
        if (choice < 0){
            // create a new room
            int roomID = rooms.size();
            //TODO here I create a MapDataBase object for every room, a more efficient approach would be using deep copy to build a new object of WorldMap after this user choose the WorldMap she wants
            rooms.put(roomID, new RoomController(roomID, player, new MapDataBase<String>()));
        }else {
            // join an existing room
            rooms.get(choice).addPlayer(player);
        }
    }

    /**
     * This function asks the player whether he/she want to start a new room or join an existing room.
     * @param player player object, handle the communication
     * @return room number/ID, e.g. -1(or any negative number) stands for a new room, > 0 stands for an existing room
     */
    int askValidRoomNum(Player<?> player) throws IOException {
        while (true){
            try {
                String choice = (String) player.recv();
                int num = Integer.parseInt(choice);
                if (num >= 0 && !rooms.containsKey(num)){
                    throw new InvalidKeyException();
                }
                return num;
            }catch (NumberFormatException | NullPointerException | InvalidKeyException | ClassNotFoundException e){
                // Number format error
                player.send("Invalid choice, try again");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        GameServer gameServer = new GameServer(new Server());
        gameServer.run();
    }
}
