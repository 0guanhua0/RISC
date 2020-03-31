package edu.duke.ece651.risk.server;

import java.sql.*;

//todo: add change password, encrypt password
public class SQL {

    public SQL() throws ClassNotFoundException, SQLException {
        //drop table if exists
        dropTable();
        //create table
        createTable();


    }

    public void createTable() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/risk", "postgres", "postgres");

        Statement statement = conn.createStatement();
        // create table
        String sql = "CREATE TABLE risk (PLAYER_ID SERIAL PRIMARY KEY," +
                "NAME TEXT NOT NULL," +
                "PASSWORD TEXT NOT NULL);";
        statement.executeUpdate(sql);
        statement.close();
        conn.close();
    }


    public void dropTable() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/risk", "postgres", "postgres");

        Statement statement = conn.createStatement();
        // create table
        String sql = "DROP TABLE IF EXISTS risk;";
        statement.executeUpdate(sql);
        statement.close();
        conn.close();
    }


    public boolean addUser(String name, String pwd) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/risk", "postgres", "postgres");

        // ask postgres to crypt the password

        //check if user already exists
        if (authUserName(name)) {
            return false;
        }
        Statement statement = conn.createStatement();
        statement.executeUpdate(String.format("INSERT INTO risk (name, password) VALUES (\n" +
                "  '%s',\n" +
                "  '%s'\n" +
                ");", name, pwd));

        statement.close();
        conn.close();
        return true;
    }

    public boolean authUser(String name, String pwd) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/risk", "postgres", "postgres");

        Statement statement = conn.createStatement();
        ResultSet set = statement.executeQuery(String.format("SELECT PLAYER_ID \n" +
                "  FROM risk\n" +
                " WHERE name = '%s' \n" +
                "   AND password = '%s'", name, pwd));
        Boolean rst = set.next();
        set.close();
        statement.close();
        conn.close();
        return rst;

    }

    public boolean authUserName(String name) throws SQLException, ClassNotFoundException {

        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/risk", "postgres", "postgres");

        Statement statement = conn.createStatement();
        ResultSet set = statement.executeQuery(String.format("SELECT PLAYER_ID \n" +
                "  FROM risk\n" +
                " WHERE name = '%s' ", name));

        Boolean rst = set.next();

        set.close();
        statement.close();
        conn.close();
        return rst;

    }

};
