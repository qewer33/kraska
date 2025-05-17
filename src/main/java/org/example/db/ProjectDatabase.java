package org.example.db;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ProjectDatabase {
    private ArrayList<Project> projects;
    private static ProjectDatabase instance;

    private ProjectDatabase() {
        DatabaseManager.connectAndInitialize();
        projects = DatabaseManager.getAllProjects();
        // DatabaseManager.addProject(new Project("dermo", "C:/Projects/Canvas1.kraska", LocalDateTime.now().minusDays(1).toString(), LocalDateTime.now().toString()));
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
        DatabaseManager.addProject(project);
    }

    public void removeProject(String projectName) {
        projects.removeIf(p -> p.getName().equals(projectName));
        DatabaseManager.removeProject(projectName);
    }

    public void updateLastOpened(String projectName, String newLastOpened) {
        DatabaseManager.updateLastOpened(projectName, newLastOpened);
    }
}