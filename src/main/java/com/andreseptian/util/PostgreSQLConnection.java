package com.andreseptian.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConnection {

    private static final String URL = "jdbc:postgresql://localhost:5332/dynamic_regex";
    private static final String USERNAME = "andreseptian";
    private static final String PASSWORD = "123456";

    public static Connection createConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");

        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        connection.setAutoCommit(false);
        return connection;
    }

}
