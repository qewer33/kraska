package org.example.db;

import java.time.LocalDateTime;

public class Project {
    private final String name;
    private final String fileAddress;
    private final LocalDateTime created;
    private LocalDateTime lastUpdated;

    public Project(String name, String fileAddress, LocalDateTime created, LocalDateTime lastUpdated) {
        this.name = name;
        this.fileAddress = fileAddress;
        this.created = created;
        this.lastUpdated = lastUpdated;
    }

    public String getName() {
        return name;
    }

    public String getFileAddress() {
        return fileAddress;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}