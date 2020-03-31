package edu.duke.ece651.risk.server;

import edu.duke.ece651.risk.shared.player.Player;
import edu.duke.ece651.risk.shared.player.PlayerV1;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.duke.ece651.risk.shared.Mock.readAllStringFromObjectStream;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;
import static edu.duke.ece651.risk.shared.Constant.*;

class UserValidationTest {

    @Test
    void validate() throws SQLException, ClassNotFoundException, IOException {
        SQL db = new SQL();

        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        ArrayList inputStream1 =  new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"login\"} "));
        Player<String> player1 = new PlayerV1<>(setupMockInput(inputStream1), outputStream1);

        assertFalse(UserValidation.validate(player1, db));
        assertEquals(INVALID_LOGIN, readAllStringFromObjectStream(outputStream1));


        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        ArrayList inputStream2 =  new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\" }"));
        Player<String> player2 = new PlayerV1<>(setupMockInput(inputStream2), outputStream2);

        assertTrue(UserValidation.validate(player2, db));
        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(outputStream2));


        ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
        ArrayList inputStream3 =  new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"login\" }"));
        Player<String> player3 = new PlayerV1<>(setupMockInput(inputStream3), outputStream3);

        assertTrue(UserValidation.validate(player3, db));
        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(outputStream3));



        ByteArrayOutputStream outputStream5 = new ByteArrayOutputStream();
        ArrayList inputStream5 =  new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"xxx\" }"));
        Player<String> player5 = new PlayerV1<>(setupMockInput(inputStream5), outputStream5);

        assertFalse(UserValidation.validate(player5, db));
        assertEquals(INVALID_VALIDATE, readAllStringFromObjectStream(outputStream5));

        ByteArrayOutputStream outputStream6 = new ByteArrayOutputStream();
        ArrayList inputStream6 = new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\" }"));
        Player<String> player6 = new PlayerV1<>(setupMockInput(inputStream6), outputStream6);

        assertFalse(UserValidation.validate(player6, db));
        assertEquals(INVALID_SIGNUP, readAllStringFromObjectStream(outputStream6));





    }
}