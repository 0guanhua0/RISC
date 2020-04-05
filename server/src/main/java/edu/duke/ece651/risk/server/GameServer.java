package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.network.Server;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
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
    // list of all rooms(each room represent a running game)

    UserList userList;
    // db for user name & password
    SQL db;
    Map<Integer, Room> rooms;

    public GameServer(Server server) throws SQLException, ClassNotFoundException {
        this.server = server;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(32);
        this.threadPool = new ThreadPoolExecutor(4, 16, 5, TimeUnit.SECONDS, workQueue);
        this.rooms = new ConcurrentHashMap<>();
        this.db = new SQL();
        this.userList = new UserList();
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
        //treat new connection as new user
        User user = new User(socket.getInputStream(), socket.getOutputStream());

        ObjectInputStream m = new ObjectInputStream(socket.getInputStream());
        String msg = (String) m.readObject();
        //header info from client
        //String msg = (String) user.recv();
        JSONObject obj = new JSONObject(msg);

        String userName = obj.getString(USER_NAME);
        String action = obj.getString(ACTION);

        user.setUserName(userName);

        //user try to login
        if (action.equals(SIGNUP)) {
            UserValidation.signUp(user, db, obj);
            return;
        }

        //sign up
        if (action.equals(LOGIN)) {
            if (UserValidation.logIn(user, db, obj) && !userList.hasUser(userName)) {
                userList.addUser(user);
            }
            return;
        }

        //user try to play game
        //check user is in validate list
        //if no, return error
        if (!userList.hasUser(userName)) {
            //invalid user
            user.send(INVALID_USER);
            return;
        }

        //if yes, proceed
        //according to actual action to redirect
        //the available room
        if (action.equals(GET_WAIT_ROOM)) {
            user.send(getRoomList());
            return;
        }


        //the room user has joined
        if (action.equals(GET_IN_ROOM)) {
            user.send(user.getRoomList());
            return;
        }


        //create new room
        if (action.equals(CREATE_GAME) || action.equals(JOIN_GAME)) {
            //proceed to original process
            createPlayer(socket, user);
            return;

        }


        //join the existing game
        //if new player, then just new player
        //if existing player, then plug in the stream
        if (action.equals(RECONNECT_ROOM)) {
            int roomID = obj.getInt(ROOM_ID);
            // user is a player already in room
            // redirect io
            if (user.isInRoom(roomID)) {
                //go to the room
                //find that player
                Player currPlayer = rooms.get(roomID).getPlayer(userName);
                currPlayer.reConnect(socket.getInputStream(), socket.getOutputStream());
            } else {
                user.send(INVALID_RECONNECT);
            }

        }


    }


    /**
     * user want to create a room or add a room
     * create new player
     */

    void createPlayer(Socket socket, User user) throws IOException, ClassNotFoundException {
        Player<String> player = new PlayerV2<>(socket.getInputStream(), socket.getOutputStream());
        int choice = askValidRoomNum(player);
        synchronized (this) {
            if (choice < 0) {
                // create a new room
                int roomID = rooms.size();
                rooms.put(roomID, new Room(roomID, player, new MapDataBase<>()));

                //add the roomID to the user list
                user.addRoom(roomID);
            } else {
                // join an existing room
                rooms.get(choice).addPlayer(player);
                user.addRoom(choice);
            }
        }

    }

    /**
     * This function asks the player whether he/she want to start a new room or join an existing room.
     *
     * @param player player object, handle the communication
     * @return room number/ID, e.g. -1(or any negative number) stands for a new room, > 0 stands for an existing room
     */
    int askValidRoomNum(Player<?> player) throws IOException {
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
            //clear done room from user list
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
     * @return List of room object
     */

    List<edu.duke.ece651.risk.shared.Room> getRoomList() {
        clearRoom();
        // clear any finished room
        List<edu.duke.ece651.risk.shared.Room> roomList = new ArrayList<>();
        for (Room room : rooms.values()) {

            if (!room.hasStarted()) {
                roomList.add(new edu.duke.ece651.risk.shared.Room(room.roomID, ""));
            }

        }


        return roomList;
    }

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        GameServer gameServer = new GameServer(new Server());
        gameServer.run();
    }
}
