package org.example.db;

public class Project {
    private final String name;
    private final String fileAddress;
    private final String created;
    private String lastOpened;

    public Project(String name, String fileAddress, String created, String lastOpened) {
        this.name = name;
        this.fileAddress = fileAddress;
        this.created = created;
        this.lastOpened = lastOpened;
    }

    public String getName() {
        return name;
    }

    public String getFileAddress() {
        return fileAddress;
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