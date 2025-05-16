package org.example.db;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ProjectDatabase {
    private final ArrayList<Project> projects;
    private static ProjectDatabase instance;

    private ProjectDatabase() {
        DatabaseManager.connectAndInitialize();
        projects = DatabaseManager.getAllProjects();
    }

    public static ProjectDatabase getInstance() {
        if (instance == null) {
            instance = new ProjectDatabase();
        }
        return instance;
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public void addProject(Project project) {
        projects.add(project);
    }
}