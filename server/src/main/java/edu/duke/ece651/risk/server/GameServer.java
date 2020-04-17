package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.RoomInfo;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.network.Server;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static edu.duke.ece651.risk.shared.Constant.*;

public class GameServer {
    // the server object, use to communicate with all players
    Server server;
    // thread pool, used to handle incoming request
    ThreadPoolExecutor threadPool;
    // list of all authenticated user
    UserList userList;
    // db for user name & password
    SQL db;
    // map of all rooms(each room represent a running game), key is the room id
    Map<Integer, Room> rooms;

    public GameServer(Server server) throws SQLException, ClassNotFoundException, IOException {
        this.server = server;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(32);
        this.threadPool = new ThreadPoolExecutor(4, 16, 5, TimeUnit.SECONDS, workQueue);
        this.rooms = new ConcurrentHashMap<>();
        this.db = new SQL();
        this.userList = new UserList();

        //recover
        recover();

        // TODO: debug info
        db.addUser("xkw", "1234");
        db.addUser("xkx", "1234");
        db.addUser("xxx", "1234");
        db.addUser("a", "a");
        db.addUser("s", "s");

    }

    /**
     * recover function will try to read from mongo db
     * then will put the info back to room list
     */

    void recover() throws IOException {
        //connect to mongo
        Mongo m = new Mongo();
        Datastore datastore = m.morCon();
        Query<Room> query = datastore.createQuery(Room.class);
        List<Room> rooms = query.asList();

        //it through rooms, put back to map
        for (Room r : rooms) {
            r.recover(new MapDataBase<>());
            this.rooms.put(r.roomID, r);
        }
    }

    /**
     * This will run forever(until the thread is killed), keep listen for new connection and handle it.
     */
    public void run() {
        System.out.println("Game server is running, waiting for new connection...");
        while (!Thread.currentThread().isInterrupted()) {
            Socket socket = server.accept();
            if (socket != null) {
                threadPool.execute(() -> {
                    try {
                        handleIncomeRequest(socket);
                    } catch (IOException | ClassNotFoundException | SQLException e) {
                        // IO Exception, probably a bette way is write to log file
                        try {
                            socket.close();
                        } catch (IOException ignored) {
                        }
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
     *
     * @param socket represent a newly accept player
     */
    void handleIncomeRequest(Socket socket) throws IOException, ClassNotFoundException, SQLException {
        // treat each new connection as a new user
        Player<String> player = new PlayerV2<>(socket.getInputStream(), socket.getOutputStream());

        System.out.println("receive new connection");
        // header info from client(specify the action)
        String msg = (String) player.recv();
        System.out.println("recv: " + msg);

        JSONObject obj = new JSONObject(msg);
        String userName = obj.getString(USER_NAME);
        String action = obj.getString(ACTION);
        player.setName(userName);

        // user try to sign up
        if (action.equals(SIGNUP)) {
            String userPassword = obj.getString(USER_PASSWORD);
            if (db.addUser(userName, userPassword)) {
                player.send(SUCCESSFUL);
            } else {
                player.send(INVALID_SIGNUP);
            }
            return;
        }

        // login
        if (action.equals(LOGIN)) {
            String userPassword = obj.getString(USER_PASSWORD);
            if (db.authUser(userName, userPassword)) {
                player.send(SUCCESSFUL);
                //add to active user list & keep tracking room info
                if (!userList.hasUser(userName)) {
                    User user = new User(userName, userPassword);
                    userList.addUser(user);
                }
            } else {
                player.send(INVALID_LOGIN);
            }
            return;
        }

        /* ====== actions below all need user login first(so we will check the user list) ====== */

        // if not login, return error
        if (!userList.hasUser(userName)) {
            //invalid user
            player.send(INVALID_USER);
            return;
        }
        // successful login

        // get all rooms which waiting for new players
        if (action.equals(ACTION_GET_WAIT_ROOM)) {
            player.send(getRoomList());
            return;
        }

        // the room user has joined
        if (action.equals(ACTION_GET_IN_ROOM)) {
            player.send(getUserRoom(userName));
            return;
        }

        // create a new room or join an existing room
        if (action.equals(ACTION_CREATE_GAME) || action.equals(ACTION_JOIN_GAME)) {
            // proceed to original process
            startGame(player);
            return;

        }

        // reconnect to the room
        if (action.equals(ACTION_RECONNECT_ROOM)) {
            int roomID = obj.getInt(ROOM_ID);
            // user is a player already in room
            // redirect io
            if (userList.getUser(userName).isInRoom(roomID)) {
                // go to the room, find that player and replace the stream with new one
                Player<?> currPlayer = rooms.get(roomID).getPlayer(userName);
                currPlayer.setIn(player.getIn());
                currPlayer.setOut(player.getOut());
                currPlayer.setConnect(true);
                currPlayer.send(SUCCESSFUL);
            } else {
                player.send(INVALID_RECONNECT);
            }
        }

        // user want to connect to the chat
        if (action.equals(ACTION_CONNECT_CHAT)) {
            int roomID = obj.getInt(ROOM_ID);
            // user is a player already in room
            // redirect io
            if (userList.getUser(userName).isInRoom(roomID)) {
                // go to the room, find that player and replace the stream with new one
                Player<?> currPlayer = rooms.get(roomID).getPlayer(userName);
                currPlayer.setChatStream(player.getIn(), player.getOut());
                currPlayer.setConnect(true);
                currPlayer.sendChatMessage(SUCCESSFUL);
            } else {
                player.send(INVALID_RECONNECT);
            }
        }
    }

    /**
     * user want to create a room or add a room
     */
    void startGame(Player<String> player) throws IOException, ClassNotFoundException {
        int choice = askValidRoomNum(player);
        synchronized (this) {
            if (choice < 0) {
                // create a new room
                int roomID = rooms.size();
                rooms.put(roomID, new Room(roomID, player, new MapDataBase<>()));
                //add the roomID to the user list
                userList.getUser(player.getName()).addRoom(roomID);
            } else {
                // join an existing room
                rooms.get(choice).addPlayer(player);
                userList.getUser(player.getName()).addRoom(choice);
            }
        }

    }

    /**
     * This function asks the player whether he/she want to start a new room or join an existing room.
     *
     * @param player player object, handle the communication
     * @return room number/ID, e.g. -1(or any negative number) stands for a new room, > 0 stands for an existing room
     */
    int askValidRoomNum(Player<?> player) {
        while (true) {
            try {
                String choice = (String) player.recv();
                int num = Integer.parseInt(choice);
                if (num >= 0 && !rooms.containsKey(num)) {
                    throw new InvalidKeyException();
                }
                player.send(SUCCESSFUL);
                return num;
            } catch (NumberFormatException | NullPointerException | InvalidKeyException | ClassNotFoundException e) {
                // Number format error
                player.send("Invalid choice, try again.");
            }
        }
    }

    /**
     * clear finish room
     */
    void clearRoom() {
        List<Integer> finishedRoom = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (room.hasFinished()) {
                finishedRoom.add(room.roomID);
            }
        }

        for (int id : finishedRoom) {
            rooms.remove(id, rooms.get(id));
            // clear any finished rooms from user list
            for (User u : userList.getUserList()) {
                if (u.isInRoom(id)) {
                    u.rmRoom(id);
                }
            }
        }
    }

    /**
     * This function will return the current running room list.
     *
     * @return list of RoomInfo object
     */
    List<RoomInfo> getRoomList() {
        // clear any finished room
        clearRoom();

        List<RoomInfo> roomInfoList = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (!room.hasStarted()) {
                roomInfoList.add(new RoomInfo(room.roomID, room.roomName, room.map, room.players));
            }

        }
        return roomInfoList;
    }

    /**
     * This function will return the current running room list of one specific player.
     *
     * @return list of RoomInfo object
     */
    List<RoomInfo> getUserRoom(String user) {
        // clear any finished room
        clearRoom();

        List<RoomInfo> roomInfoList = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (room.hasPlayer(user) && !room.isPlayerLose(user)) {
                roomInfoList.add(new RoomInfo(room.roomID, room.roomName, room.map, room.players));
            }
        }
        return roomInfoList;
    }

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        GameServer gameServer = new GameServer(new Server(12345));
        gameServer.run();
    }
}
