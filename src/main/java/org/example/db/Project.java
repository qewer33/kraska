package org.example.db;

public class Project {
    private final String name;
    private final String created;
    private String lastOpened;

    public Project(String name, String created, String lastOpened) {
        this.name = name;
        this.created = created;
        this.lastOpened = lastOpened;
    }

    public String getName() {
        return name;
    }

    public String getCreated() {
        return created;
    }

    public String getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(String lastOpened) {
        this.lastOpened = lastOpened;
    }
}