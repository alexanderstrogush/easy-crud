package com.alexstrog.dao;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

    private static final Logger logger = Logger.getLogger(ConnectionUtil.class);

    private static final String CONNECTION_URL = "jdbc:h2:tcp://localhost/~/easy_crud";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static Connection connection;

    static {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Can't get connect with DB", e);
        }
    }

    public static Connection connect() {
        return connection;
    }
}
