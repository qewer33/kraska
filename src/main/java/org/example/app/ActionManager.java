package org.example.app;

import javax.swing.*;
import java.util.*;

/*
 * ActionManager manages global app actions (such as tool activation).
 * It is a manager class that uses the singleton pattern.
 */
public class ActionManager {
    private final HashMap<String, Action> actions = new HashMap<>();

    // Singleton pattern
    private static ActionManager instance;

    public static ActionManager getInstance() {
        if (instance == null) {
            instance = new ActionManager();
        }
        return instance;
    }

    private ActionManager() {}

    public void registerAction(String name, AbstractAction action) {
        actions.put(name, action);
    }

    public Action getAction(String name) {
        return actions.get(name);
    }
}
