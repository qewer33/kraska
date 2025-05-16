package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

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
                + "name TEXT NOT NULL, "
                + "file_address TEXT NOT NULL, "
                + "created_time TEXT NOT NULL, "         // ISO8601 string, e.g. '2024-05-17T12:34:56'
                + "last_opened_time TEXT NOT NULL"       // ISO8601 string
                + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error while trying to initialize the projects database: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public static void addProject(Project project) {
        String sql = "INSERT INTO projects (name, file_address, created_time, last_opened_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getFileAddress());
            pstmt.setString(3, project.getCreated());
            pstmt.setString(4, project.getLastOpened());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while trying to add a project to the database: " + e.getMessage());
        }
    }

    public static void logAllProjects() {
        String sql = "SELECT name, file_address, created_time, last_opened_time FROM projects";
        try (Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("Projects in database:");
            while (rs.next()) {
                System.out.println(
                    "Name: " + rs.getString("name") +
                    ", File: " + rs.getString("file_address") +
                    ", Created: " + rs.getString("created_time") +
                    ", Last Opened: " + rs.getString("last_opened_time")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error while logging projects: " + e.getMessage());
        }
    }

    public static ArrayList<Project> getAllProjects() {
        String sql = "SELECT name, file_address, created_time, last_opened_time FROM projects";
        try (Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            ArrayList<Project> projects = new ArrayList<>();
            while (rs.next()) {
                Project project = new Project(
                    rs.getString("name"),
                    rs.getString("file_address"),
                    rs.getString("created_time"),
                    rs.getString("last_opened_time")
                );
                projects.add(project);
            }
            return projects;
        } catch (SQLException e) {
            System.err.println("Error while fetching projects: " + e.getMessage());
            return new ArrayList<Project>();
        }
    }

    public static void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error while closing the database connection: " + e.getMessage());
        }
    }
}
