package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.RoomInfo;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.network.Server;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
    // map of all rooms(each room represent a running playGame), key is the room id
    Map<Integer, Room> rooms;
    // action map, store the relation between action type and corresponding handling function
    Map<String, actionHandler> actionMap;

    public GameServer(Server server) throws SQLException, ClassNotFoundException {
        this.server = server;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(32);
        this.threadPool = new ThreadPoolExecutor(4, 16, 5, TimeUnit.SECONDS, workQueue);
        this.rooms = new ConcurrentHashMap<>();
        this.db = new SQL();
        this.userList = new UserList();
        this.actionMap = new HashMap<>();
        // initialize the action handler
        // we abstract a corresponding function for each action
        actionMap.put(SIGNUP, this::signup);
        actionMap.put(LOGIN, this::login);
        actionMap.put(ACTION_GET_WAIT_ROOM, this::getWaitRoom);
        actionMap.put(ACTION_GET_IN_ROOM, this::getInRoom);
        actionMap.put(ACTION_CREATE_GAME, this::playGame);
        actionMap.put(ACTION_JOIN_GAME, this::playGame);
        actionMap.put(ACTION_AUDIENCE_GAME, this::audienceGame);
        actionMap.put(ACTION_RECONNECT_ROOM, this::reconnect);
        actionMap.put(ACTION_CONNECT_CHAT, this::connectChat);

        // TODO: debug info
        db.addUser("xkw", "1234");
        db.addUser("xkx", "1234");
        db.addUser("xxx", "1234");
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

        // recognized action
        if (actionMap.containsKey(action)){
            try {
                // use action mapping to avoid if-else
                actionMap.get(action).apply(player, obj);
            }catch (UnauthorizedUserException e){
                // unauthorized user try to do something needed login
                System.err.println(e.toString());
            }
        }else {
            System.err.println("Unrecognized action type: " + action);
            player.send(INVALID_ACTION_TYPE);
        }
    }

    /* ============ below are the functions which follows the same interface and used to handle incoming request ============ */
    /**
     * Check whether a user has already logined.
     * @param player the player to be checked
     * @param obj JSON object contains some other info we may need
     * @throws UnauthorizedUserException user doesn't login
     */
    void checkLogin(Player<String> player, JSONObject obj) throws UnauthorizedUserException {
        String userName = obj.getString(USER_NAME);
        if (!userList.hasUser(userName)) {
            //invalid user
            player.send(INVALID_USER);
            throw new UnauthorizedUserException("Unauthorized user try to do something.");
        }
    }

    /**
     * Handle the sign up related stuff(e.g. store to DB).
     * @param player new connection(wrap by player object)
     * @param obj JSON object contains some other info we may need
     * @throws SQLException database problem
     * @throws ClassNotFoundException receive unexpected data
     */
    void signup(Player<String> player, JSONObject obj) throws SQLException, ClassNotFoundException{
        String userName = obj.getString(USER_NAME);
        String userPassword = obj.getString(USER_PASSWORD);
        if (db.addUser(userName, userPassword)) {
            player.send(SUCCESSFUL);
        } else {
            player.send(INVALID_SIGNUP);
        }
    }

    /**
     * Handle the login related stuff(e.g. verify).
     * @param player new connection(wrap by player object)
     * @param obj JSON object contains some other info we may need
     * @throws SQLException database problem
     * @throws ClassNotFoundException receive unexpected data
     */
    void login(Player<String> player, JSONObject obj) throws SQLException, ClassNotFoundException{
        String userName = obj.getString(USER_NAME);
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
    }

    /**
     * Get all rooms which is still waiting for new players.
     * @param player new connection(wrap by player object)
     * @param obj JSON object contains some other info we may need
     * @throws UnauthorizedUserException user doesn't login, can't perform this action
     */
    void getWaitRoom(Player<String> player, JSONObject obj) throws UnauthorizedUserException {
        String userName = obj.getString(USER_NAME);
        checkLogin(player, obj);
        player.send(getRoomList(userName));
    }

    /**
     * Get all rooms which current player is inside.
     * @param player new connection(wrap by player object)
     * @param obj JSON object contains some other info we may need
     * @throws UnauthorizedUserException user doesn't login, can't perform this action
     */
    void getInRoom(Player<String> player, JSONObject obj) throws UnauthorizedUserException {
        String userName = obj.getString(USER_NAME);
        checkLogin(player, obj);
        player.send(getUserRoom(userName));
    }

    /**
     * Create a new room or join an existing room.
     * @param player new connection(wrap by player object)
     * @param obj JSON object contains some other info we may need
     * @throws UnauthorizedUserException user doesn't login, can't perform this action
     * @throws IOException stream error
     * @throws ClassNotFoundException receive unexpected data
     */
    void playGame(Player<String> player, JSONObject obj) throws UnauthorizedUserException, IOException, ClassNotFoundException {
        checkLogin(player, obj);
        startGame(player);
    }

    /**
     * Audience a running game.
     * @param player new connection(wrap by player object)
     * @param obj JSON object contains some other info we may need
     * @throws UnauthorizedUserException user doesn't login, can't perform this action
     */
    void audienceGame(Player<String> player, JSONObject obj) throws UnauthorizedUserException, IOException, ClassNotFoundException {
        checkLogin(player, obj);
        int roomID = obj.getInt(ROOM_ID);
        Room room = rooms.get(roomID);
        if (room.hasFinished()){
            player.send(INVALID_AUDIENCE_FINISHED);
        }else if (!room.hasStarted()){
            player.send(INVALID_AUDIENCE_NOT_START);
        }else {
            player.send(SUCCESSFUL);
            // we only allowed audience for the game which is started but not finished
            room.addAudience(player);
        }
    }

    /**
     * Player try to reconnect to a previous room.
     * @param player new connection(wrap by player object)
     * @param obj JSON object contains some other info we may need
     * @throws UnauthorizedUserException user doesn't login, can't perform this action
     */
    void reconnect(Player<String> player, JSONObject obj) throws UnauthorizedUserException {
        checkLogin(player, obj);
        String userName = obj.getString(USER_NAME);
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

    /**
     * Player try to connect to the chat channel of a room.
     * @param player new connection(wrap by player object)
     * @param obj JSON object contains some other info we may need
     * @throws UnauthorizedUserException user doesn't login, can't perform this action
     */
    void connectChat(Player<String> player, JSONObject obj) throws UnauthorizedUserException {
        checkLogin(player, obj);
        String userName = obj.getString(USER_NAME);
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

    /* ============ end ============ */

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
     * @return list of RoomInfo object
     */
    List<RoomInfo> getRoomList(String user) {
        // clear any finished room
        clearRoom();

        List<RoomInfo> roomInfoList = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (!room.hasPlayer(user)){
                roomInfoList.add(new RoomInfo(room.roomID, room.roomName, room.map, room.players));
            }
        }
        return roomInfoList;
    }

    /**
     * This function will return the current running room list of one specific player.
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
