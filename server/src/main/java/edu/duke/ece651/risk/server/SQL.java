package edu.duke.ece651.risk.server;

import java.sql.*;

public class SQL {
    Connection conn = null;

    public SQL() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/risk", "postgres", "postgres");

        } catch (Exception ignored) {

        }
    }

    public boolean createTable() {
        try {
            Statement statement = conn.createStatement();
            // create table
            String sql = "CREATE TABLE risk (PLAYER_ID SERIAL PRIMARY KEY," +
                    "NAME TEXT NOT NULL," +
                    "PASSWORD TEXT NOT NULL);";
            statement.executeUpdate(sql);
            statement.close();
            return true;
        } catch (SQLException ignored) {
        }
        return false;
    }

    public boolean dropTable() {
        try {
            Statement statement = conn.createStatement();
            // create table
            String sql = "DROP TABLE IF EXISTS risk;";
            statement.executeUpdate(sql);
            statement.close();
            return true;
        } catch (SQLException ignored) {
        }
        return false;
    }

    public boolean addUser(String name, String pwd) {
        // ask postgres to crypt the password
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(String.format("INSERT INTO risk (name, password) VALUES (\n" +
                    "  '%s',\n" +
                    "  '%s'\n" +
                    ");", name, pwd));
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean authUser(String name, String pwd) {
        try {
            Statement statement = conn.createStatement();
            try (ResultSet set = statement.executeQuery(String.format("SELECT PLAYER_ID \n" +
                    "  FROM risk\n" +
                    " WHERE name = '%s' \n" +
                    "   AND password = '%s'", name, pwd));) {
                return set.next();
            } catch (SQLException ignored) {
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    public static void main(String[] args) {
        SQL sql = new SQL();
        sql.dropTable();
        sql.createTable();
        if (sql.authUser("test", "123")) {
            System.out.println("pass");
        } else {
            System.out.println("fail");
        }
        sql.addUser("test", "123");
        if (sql.authUser("test", "123")) {
            System.out.println("pass");
        } else {
            System.out.println("fail");
        }
    }
};
