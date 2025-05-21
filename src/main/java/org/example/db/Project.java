package org.example.db;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class Project {
    private final String name;
    private final String created;
    private String lastOpened;

    public Project(String name, String created, String lastOpened) {
        this.name = name;
        this.created = created;
        this.lastOpened = lastOpened;
    }

    public File getLatestAutosave() {
        File savesRoot = new File(System.getProperty("user.home") + File.separator + "kraska_saves");
        String safeProjectName = this.name.replaceAll("[^a-zA-Z0-9\\-_]", "_");
        File projectDir = new File(savesRoot, safeProjectName);

        if (!projectDir.exists()) return null;

        File[] files = projectDir.listFiles((dir, name) -> name.endsWith(".png"));
        if (files == null || files.length == 0) return null;

        return Arrays.stream(files)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
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