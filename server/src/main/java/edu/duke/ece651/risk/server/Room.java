package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.RoomInfo;
import edu.duke.ece651.risk.shared.action.Action;
import edu.duke.ece651.risk.shared.action.AttackResult;
import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.TStatus;
import edu.duke.ece651.risk.shared.map.Territory;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import edu.duke.ece651.risk.shared.player.SPlayer;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static edu.duke.ece651.risk.shared.Constant.*;

//TODO for every method that have networking, take client losing connection into consideration
//TODO for every method that have networking, handle some exceptions rather than just throwing it

@Entity
public class Room {
    @Id
    int roomID;

    String roomName;
    // all players in current room
    @Embedded
    List<Player<String>> players;
    // the map this room is playing
    @Embedded
    WorldMap<String> map;

    // some basic info we need for a game(e.g. winnerID, roundNum)
    @Embedded
    GameInfo gameInfo;
    // all new threads we create in this game(e.g. player thread, chat thread)
    //ignore this field to store in mongo db
    @Transient
    List<Thread> threads;


    /**
     * The constructor, initialize the whole playGame(room.
     *
     * @param roomID      roomID for this room
     * @param player      the "beginner", the player create this room
     * @param mapDataBase all map we have
     * @throws IllegalArgumentException probably because of invalid roomID(should be positive)
     * @throws ClassNotFoundException   probably because of not follow the protocol
     */
    public Room(int roomID, Player<String> player, MapDataBase<String> mapDataBase) throws IllegalArgumentException, ClassNotFoundException {
        if (roomID < 0) {
            throw new IllegalArgumentException("Invalid value of Room ID");
        }
        this.roomID = roomID;
        this.roomName = "";
        players = new ArrayList<>();
        players.add(player);
        player.setId(players.size());

        // let the beginner choose map & specify room name
        initGame(mapDataBase);

        List<String> colorList = map.getColorList();
        // assign the color
        player.setColor(colorList.get(0));
        player.sendPlayerInfo();

        player.send(new RoomInfo(roomID, roomName, map, players));

        gameInfo = new GameInfo(-1, 1);
        gameInfo.addPlayer(player.getId(), player.getName());

        System.out.println("successfully init the playGame");
        System.out.println("room name: " + this.roomName);
        threads = new ArrayList<>();
    }

    // constructor for testing
    public Room(int roomID, MapDataBase<String> mapDataBase) throws IllegalArgumentException {
        if (roomID < 0) {
            throw new IllegalArgumentException("Invalid value of Room Id");
        }
        this.roomID = roomID;
        this.roomName = "";

        players = new ArrayList<>();

        gameInfo = new GameInfo(-1, 1);
    }

    //constructor for mongo
    public Room() {

    }

    /**
     * recover
     */
    void recover(MapDataBase mapDataBase) {
        //set all player disconnect
        for (Player p : players) {
            p.setIn(null);
            p.setOut(null);
            p.setChatStream(null, null);
            p.setConnect(false);

            //restore action
            List<Action> actions = new ArrayList<>();
            p.setActions(actions);
        }
        //world map territory
        String mapName = this.map.getName();
        this.map = mapDataBase.getMap(mapName);

        //update territory info
        for (Player p : players) {
            Set<Territory> territorySet = p.getTerritories();
            //loop through, update Tstatus
            for (Territory t : territorySet) {
                String tName = t.getName();
                TStatus tStatus = t.getStatus();
                this.map.getTerritory(tName).setStatus(tStatus);
            }
        }
        //recover player Set<Territory>
        for (Player p : players) {
            Set<Territory> newT = new HashSet<>();
            Set<Territory> oldT = p.getTerritories();

            for (Territory t : oldT) {
                String tName = t.getName();
                Territory tmp = this.map.getTerritory(tName);
                newT.add(tmp);
            }

            p.setTerritories(newT);
        }


        //populate the ally field with null
        for (Player p : players) {
            p.setAlly(null);
        }

        //player ally
        for (Player p : players) {
            if (p.getAllyRequest() != -1) {
                Player ally = players.get(p.getAllyRequest());
                p.allyWith(ally);
            }
        }


        //restore territory status in map

        threads = new ArrayList<>();


        //ready to restart game
        new Thread(() -> {
            try {
                reGame();
            } catch (Exception ignored) {
            }
        }).

                start();


    }

    /**
     * call this method to add a new player into this room
     * after the last player enter the room, playGame will begin automatically
     *
     * @param player new player
     */
    void addPlayer(Player<String> player) {
        // only accept new player if the playGame is not start yet
        if (players.size() < map.getColorList().size()) {
            players.add(player);

            List<String> colorList = map.getColorList();
            player.setId(players.size());
            player.setColor(colorList.get(players.size() - 1));
            player.sendPlayerInfo();

            // send the latest room info
            player.send(new RoomInfo(roomID, roomName, map, players));

            gameInfo.addPlayer(player.getId(), player.getName());

            // broadcast the newly joined player info
            sendAllExcept(player.getName(), player);

            // check whether has enough player to start the playGame
            if (players.size() == map.getColorList().size()) {
                // broadcast enough players join the playGame
                sendAll(INFO_ALL_PLAYER);
                // use a separate thread to run the playGame
                new Thread(() -> {
                    try {
                        runGame();
                    } catch (Exception ignored) {
                    }
                }).start();
            }
        }
    }

    void initGame(MapDataBase<String> mapDataBase) throws ClassNotFoundException {
        Player<String> firstPlayer = players.get(0);
        firstPlayer.send(mapDataBase);
        while (true) {
            String json = (String) firstPlayer.recv();
            JSONObject jsonObject = new JSONObject(json);
            String mapName = jsonObject.optString(MAP_NAME, "test");
            String roomName = jsonObject.optString(ROOM_NAME, "fancy room");
            if (mapDataBase.containsMap(mapName)) {
                map = mapDataBase.getMap(mapName);
                this.roomName = roomName;
                break;
            } else {
                firstPlayer.send(SELECT_MAP_ERROR);
            }
        }
        firstPlayer.send(SUCCESSFUL);
    }

    /**
     * This function will send the data to all players in current room
     *
     * @param data data to be sent
     */
    void sendAll(Object data) {
        for (Player<String> player : players) {
            if (player.isConnect()) {
                player.send(data);
            }
        }
    }

    /**
     * This function will send the data to all players except p.
     *
     * @param data data to be sent
     * @param p    except this player
     */
    void sendAllExcept(Object data, Player<String> p) {
        for (Player<String> player : players) {
            if (player.isConnect() && player != p) {
                player.send(data);
            }

        }
    }

    /**
     * This function should be called at the end of each round, will resolve all combats happen in all territories.
     *
     * @throws IOException probably because of stream closed
     */
    void resolveCombats() throws IOException {
        Map<String, Territory> territoryMap = map.getAtlas();
        for (Territory t : territoryMap.values()) {
            // calculate the attack result
            List<AttackResult> attackResultList = t.resolveCombats();

            // generate attack result message based on AttackResult object
            for (AttackResult aR : attackResultList) {
                StringBuilder sb = new StringBuilder();

                Player<String> pAttack = players.get(aR.getAttackerID() - 1);
                Player<String> pDefend = players.get(aR.getDefenderID() - 1);

                Territory destTerritory = map.getAtlas().get(aR.getDestTerritory());

                // attack info
                sb.append(pAttack.getName()).append(" attacks ").append(pDefend.getName());
                sb.append("'s territory ").append(destTerritory.getName());
                sb.append("(from ");
                for (String name : aR.getSrcTerritories()) {
                    sb.append(name).append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                sb.append(")");

                // change ownership of territory
                if (aR.isAttackerWin()) {
                    sb.append(" ---> attacker wins");
                    // attacker wins, win the destination territory
                    pDefend.loseTerritory(destTerritory);
                    pAttack.addTerritory(destTerritory);
                } else {
                    sb.append(" ---> attacker loses");
                }

                // send the result of each attack action to all players
                sendAll(sb.toString());
            }
        }
    }

    /**
     * check whether there is a winner
     */
    void checkWinner() {
        int targetNum = map.getTerriNum();
        int totalNum = 0;
        for (Player<String> player : players) {
            int curNum = player.getTerrNum();
            totalNum += curNum;
            if (totalNum > targetNum) {
                throw new IllegalStateException("Illegal State of current world");
            }
            if (curNum == targetNum) {
                gameInfo.setWinner(player.getId());
            }
        }
    }

    void endGame() {
        String winnerName = gameInfo.getWinnerName();
        for (Player<String> player : players) {
            if (player.getId() != gameInfo.getWinnerID()) {
                player.send(String.format("Winner is the %s player.", winnerName));
            } else {
                player.send(YOU_WINS);
            }
        }
        // interrupt all thread in current room
        for (Thread t : threads) {
            t.interrupt();
        }
    }

    /**
     * update the state(e.g. num of units and resources)
     * of current map after the end of each single round of playGame
     */
    void updateWorld() {
        // update tech&food resources for every player
        for (Player<String> player : players) {
            player.updateState();
        }
    }

    boolean hasFinished() {
        return gameInfo.hasFinished();
    }

    boolean hasStarted() {
        return players.size() == map.getColorList().size();
    }

    void runGame() throws IOException {
        // + 1 for main thread
        CyclicBarrier barrier = new CyclicBarrier(players.size() + 1);

        for (Player<String> player : players) {
            Thread t = new PlayerThread(player, map, gameInfo, barrier, this.players);
            threads.add(t);
            t.start();
        }
        // wait for selecting territory
        barrierWait(barrier);

        // open the chat thread
        Thread tChat = new ChatThread<String>(players);
        threads.add(tChat);
        tChat.start();

        mainGame(barrier);
        endGame();
    }

    //recover game
    //diff: no need to select territory
    //use recover player thread
    void reGame() throws IOException {
        // + 1 for main thread
        CyclicBarrier barrier = new CyclicBarrier(players.size() + 1);

        for (Player<String> player : players) {
            Thread t = new PlayerThreadRecover(player, map, gameInfo, barrier, this.players);
            threads.add(t);
            t.start();
        }
        // open the chat thread
        Thread tChat = new ChatThread<String>(players);
        threads.add(tChat);
        tChat.start();

        barrierWait(barrier);
        mainGame(barrier);
        endGame();
    }

    //main game process
    void mainGame(CyclicBarrier barrier) throws IOException {
        while (true) {
            // wait for all player to ready start a round(give main thread some time to process round result)
            System.out.println("room " + roomName + "  " + gameInfo.roundNum + " wait player start round");
            barrierWait(barrier);
            System.out.println("room " + roomName + "  " + gameInfo.roundNum + " wait player play");

            // wait for all player to finish one round
            barrierWait(barrier);
            System.out.println("room " + roomName + "  " + gameInfo.roundNum + " after");

            resolveCombats();

            // after execute all actions, tell the player to enter next round
            sendAll(ROUND_OVER);

            //save current game to db
            Mongo m = new Mongo();
            m.morCon().save(this);


            // check the playGame result

            checkWinner();
            if (!gameInfo.hasFinished()) {
                sendAll("continue");
            } else {
                sendAll(GAME_OVER);
                break;
            }
            gameInfo.nextRound();
            updateWorld();
        }

    }

    void barrierWait(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ignored) {
        }
    }

    /**
     * get players
     *
     * @return list of players in current room
     */
    public List<Player<String>> getPlayers() {
        return players;
    }

    /**
     * Find certain player.
     *
     * @param name name of the player looking for
     * @return corresponding player object
     */
    public Player<?> getPlayer(String name) {
        for (Player<String> p : players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Check if certain player is in current room.
     *
     * @param name player name
     * @return true for player in this room
     */
    public boolean hasPlayer(String name) {
        for (Player<String> p : players) {
            if (p.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether this player is lose.
     *
     * @param playerName player name
     * @return true for lose
     */
    public boolean isPlayerLose(String playerName) {
        for (Player<String> p : players) {
            if (p.getName().equals(playerName)) {
                return p.getTerrNum() <= 0;
            }
        }
        return false;
    }
}
