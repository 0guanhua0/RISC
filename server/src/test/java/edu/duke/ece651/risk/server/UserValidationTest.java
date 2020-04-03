package edu.duke.ece651.risk.server;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.duke.ece651.risk.shared.Constant.*;
import static edu.duke.ece651.risk.shared.Mock.readAllStringFromObjectStream;
import static edu.duke.ece651.risk.shared.Mock.setupMockInput;
import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    @Test
    void validate() throws SQLException, ClassNotFoundException, IOException {
        SQL db = new SQL();

        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        ArrayList inputStream1 =  new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"login\"} "));
        String userName = "name";
        JSONObject jsonObject1 = new JSONObject("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"login\"} ");
        User user1 = new User(setupMockInput(inputStream1), outputStream1);

        assertFalse(UserValidation.logIn(user1, db, jsonObject1));
        assertEquals(INVALID_LOGIN, readAllStringFromObjectStream(outputStream1));


        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        ArrayList inputStream2 =  new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\" }"));
        String userName2 = "name";
        JSONObject jsonObject2 = new JSONObject("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\"} ");
        User user2 = new User(setupMockInput(inputStream2), outputStream2);

        assertTrue(UserValidation.signUp(user2, db, jsonObject2));
        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(outputStream2));



        ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
        ArrayList inputStream3 =  new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"login\" }"));

        JSONObject jsonObject3 = new JSONObject("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"login\" }");

        String userName3 = "name";
        User user3 = new User(userName3, setupMockInput(inputStream3), outputStream3);


        assertTrue(UserValidation.logIn(user3, db, jsonObject3));
        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(outputStream3));




        ByteArrayOutputStream outputStream6 = new ByteArrayOutputStream();
        ArrayList inputStream6 = new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\" }"));
        String userName6 = "name";
        JSONObject jsonObject6 = new JSONObject("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\" }");
        User user6 = new User(userName6, setupMockInput(inputStream6), outputStream6);
        assertFalse(UserValidation.signUp(user6, db, jsonObject6));
        assertEquals(INVALID_SIGNUP, readAllStringFromObjectStream(outputStream6));







    }
}