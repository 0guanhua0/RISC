package edu.duke.ece651.risk.server;

import java.sql.*;

//todo: add change password, encrypt password
public class SQL {
//    static String dbUrl = "jdbc:postgresql://drona.db.elephantsql.com:5432/gqjnslms";
//    static String dbUser = "gqjnslms";
//    static String dbPassword = "lgdCePb8sUPuaIh1NxvkcXVcRIxc-lVp";

    // local test config
    static String dbUrl = "jdbc:postgresql://localhost:5432/risk";
    static String dbUser = "postgres";
    static String dbPassword = "postgres";

    public SQL() throws ClassNotFoundException, SQLException {
        //drop table if exists
        dropTable();
        //create table
        createTable();
    }

    public void createTable() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

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
        Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        Statement statement = conn.createStatement();
        // create table
        String sql = "DROP TABLE IF EXISTS risk;";
        statement.executeUpdate(sql);
        statement.close();
        conn.close();
    }

    public boolean addUser(String name, String pwd) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        // TODO: ask postgres to crypt the password

        //check if user already exists
        if (isNameExist(name)) {
            return false;
        }
        Statement statement = conn.createStatement();
        statement.executeUpdate(String.format("INSERT INTO risk (name, password) VALUES ( " +
                "  '%s', " +
                "  '%s' " +
                ");", name, pwd));

        statement.close();
        conn.close();
        return true;
    }

    public boolean authUser(String name, String pwd) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        Statement statement = conn.createStatement();
        ResultSet set = statement.executeQuery(String.format("SELECT PLAYER_ID  " +
                "  FROM risk " +
                " WHERE name = '%s'  " +
                "   AND password = '%s'", name, pwd));
        boolean rst = set.next();
        set.close();
        statement.close();
        conn.close();
        return rst;

    }

    public boolean isNameExist(String name) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        Statement statement = conn.createStatement();
        ResultSet set = statement.executeQuery(String.format("SELECT PLAYER_ID " +
                "  FROM risk " +
                " WHERE name = '%s' ", name));

        boolean rst = set.next();
        set.close();
        statement.close();
        conn.close();
        return rst;

    }
}
