package org.pguia.java.swing.jdbc.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDataBase {
    private static String url = "jdbc:mysql://localhost:3306/swing_products?serverTimezone=UTC";
    private static String username = "root";
    private static String password = "$Pguia280704";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
