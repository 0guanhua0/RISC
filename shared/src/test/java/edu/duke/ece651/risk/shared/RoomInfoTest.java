package edu.duke.ece651.risk.shared;

import edu.duke.ece651.risk.shared.map.MapDataBase;
import edu.duke.ece651.risk.shared.map.WorldMap;
import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static edu.duke.ece651.risk.shared.Constant.MAP_NAME;
import static edu.duke.ece651.risk.shared.Constant.ROOM_NAME;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RoomInfoTest {
    static Map<Integer, String> players = new HashMap<>();

    @Test
    public void testGetter() throws IOException {
        RoomInfo roomInfo = new RoomInfo(1, "test");
        assertEquals(1, roomInfo.getRoomID());
        assertEquals("test", roomInfo.getRoomName());

        List<Player<String>> players = new ArrayList<>();
        ByteArrayOutputStream o1 = new ByteArrayOutputStream();
        String map = "a clash of kings";
        String rName = "1";

        String s11 = "{\"" + MAP_NAME + "\": \"" + map + "\",\n" +
                "\"" + ROOM_NAME +"\": \"" + rName + "\" }";
        Player<String> p1 = new PlayerV2<>(setupMockInput(new ArrayList<>(Arrays.asList(s11))), o1);

        players.add(p1);

        MapDataBase<String> mapDataBase = new MapDataBase<>();
        WorldMap<String> map0 = mapDataBase.getMap(map);

        RoomInfo roomInfo2 = new RoomInfo(1, "2", map0, players);

        assertEquals(1, roomInfo2.getPlayerCnt());
        assertEquals(2, roomInfo2.getPlayerNeedTotal());
        assertEquals("[null]", roomInfo2.getPlayerNames().toString());
        assertEquals("(2 players needed, 1 players inside, playing map \"a clash of kings\")", roomInfo2.getDetailInfo());
        assertFalse(roomInfo2.hasStarted());
    }

} 
