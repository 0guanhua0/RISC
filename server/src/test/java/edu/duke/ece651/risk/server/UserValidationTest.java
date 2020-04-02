package edu.duke.ece651.risk.server;

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
        User user1 = new User(userName, setupMockInput(inputStream1), outputStream1);

        assertFalse(UserValidation.validate(user1, db));
        assertEquals(INVALID_LOGIN, readAllStringFromObjectStream(outputStream1));


        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        ArrayList inputStream2 =  new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\" }"));
        String userName2 = "name";
        User user2 = new User(userName2,setupMockInput(inputStream2), outputStream2);

        assertTrue(UserValidation.validate(user2, db));
        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(outputStream2));


        ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
        ArrayList inputStream3 =  new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"login\" }"));

        String userName3 = "naem";
        User user3 = new User(userName3, setupMockInput(inputStream3), outputStream3);

        assertTrue(UserValidation.validate(user3, db));
        assertEquals(SUCCESSFUL, readAllStringFromObjectStream(outputStream3));



        ByteArrayOutputStream outputStream5 = new ByteArrayOutputStream();
        ArrayList inputStream5 =  new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"xxx\" }"));
        String userName5 = "name";
        User user5 = new User(userName5, setupMockInput(inputStream5), outputStream5);

        assertFalse(UserValidation.validate(user5, db));
        assertEquals(INVALID_VALIDATE, readAllStringFromObjectStream(outputStream5));

        ByteArrayOutputStream outputStream6 = new ByteArrayOutputStream();
        ArrayList inputStream6 = new ArrayList<>(Arrays.asList("{\"userName\": \"name\",\n" +
                "\"userPassword\": \"password\",\n" +
                "\"action\": \"signup\" }"));
        String userName6 = "name";
        User user6 = new User(userName6, setupMockInput(inputStream6), outputStream6);
        assertFalse(UserValidation.validate(user6, db));
        assertEquals(INVALID_SIGNUP, readAllStringFromObjectStream(outputStream6));





    }
}