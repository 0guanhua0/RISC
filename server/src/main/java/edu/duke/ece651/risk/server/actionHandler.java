package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.player.Player;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

@FunctionalInterface
public interface actionHandler {
    void apply(Player<String> player, JSONObject object)
            throws SQLException, ClassNotFoundException, UnauthorizedUserException, IOException;
}
