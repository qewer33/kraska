package org.example.db;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectDatabase {
    private final List<Project> projects = new ArrayList<>();
    private static ProjectDatabase instance;

    private ProjectDatabase() {
        // Add some dummy data for testing
        projects.add(new Project("dermo", "C:/Projects/Canvas1.kraska", LocalDateTime.now().minusDays(1), LocalDateTime.now()));
        projects.add(new Project("govno", "C:/Projects/Canvas2.kraska", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusHours(5)));
    }

    public static ProjectDatabase getInstance() {
        if (instance == null) {
            instance = new ProjectDatabase();
        }
        return instance;
    }

    public List<Project> getAllProjects() {
        return new ArrayList<>(projects);
    }

    public void addProject(Project project) {
        projects.add(project);
    }
}