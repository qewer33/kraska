package org.example;

public class Main {
    public static void main(String[] args) {
        org.example.db.DatabaseManager.connectAndInitialize();

        Application app = new Application();
        app.run();

        
    }
}