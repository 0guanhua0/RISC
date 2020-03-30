package edu.duke.ece651.risk.server;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLTest {


    @Test
    void Test() throws SQLException, ClassNotFoundException {
        SQL sql = new SQL();
        String userName = "test";
        String password = "123";
        assertEquals(false, sql.authUser(userName, password));


        assertEquals(true, sql.addUser(userName, password));

        assertEquals(true, sql.authUser(userName, password));
        assertEquals(false, sql.addUser(userName, password));
    }


}