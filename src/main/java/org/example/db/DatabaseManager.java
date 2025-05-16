package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:projects.db";
    private static Connection conn;
    public static void connectAndInitialize() {
        try {
            conn = DriverManager.getConnection(URL);
            initializeDB();
        } catch (SQLException e) {
            System.err.println("Error while trying to connect to the projects database: " + e.getMessage());
        }
    }
    public static void initializeDB() {
        String sql = "CREATE TABLE IF NOT EXISTS projects ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println("Error while trying to initialize the projects database: " + e.getMessage());
            System.exit(1);
        }
    }
}
