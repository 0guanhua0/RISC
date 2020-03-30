package edu.duke.ece651.risk.server;

import java.sql.*;

//todo: add change password, encrypt password
public class SQL {
    Connection conn = null;

    public SQL() throws ClassNotFoundException, SQLException {

        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/risk", "postgres", "postgres");

        //drop table if exists
        dropTable();
        //create table
        createTable();


    }

    public void createTable() throws SQLException {
        Statement statement = conn.createStatement();
        // create table
        String sql = "CREATE TABLE risk (PLAYER_ID SERIAL PRIMARY KEY," +
                "NAME TEXT NOT NULL," +
                "PASSWORD TEXT NOT NULL);";
        statement.executeUpdate(sql);
        statement.close();
    }


    public void dropTable() throws SQLException {
        Statement statement = conn.createStatement();
        // create table
        String sql = "DROP TABLE IF EXISTS risk;";
        statement.executeUpdate(sql);
        statement.close();
    }


    public boolean addUser(String name, String pwd) throws SQLException {
        // ask postgres to crypt the password

        //check if user already exists
        if (authUser(name, pwd)) {
            return false;
        }
        Statement statement = conn.createStatement();
        statement.executeUpdate(String.format("INSERT INTO risk (name, password) VALUES (\n" +
                "  '%s',\n" +
                "  '%s'\n" +
                ");", name, pwd));

        return true;
    }

    public boolean authUser(String name, String pwd) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet set = statement.executeQuery(String.format("SELECT PLAYER_ID \n" +
                "  FROM risk\n" +
                " WHERE name = '%s' \n" +
                "   AND password = '%s'", name, pwd));
        return set.next();

    }

};
