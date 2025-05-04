package org.example.gui.canvas.selection;

/*
 * SelectionManager manages the current selection and the view of the selection.
 * It is a manager class that uses the singleton pattern.
 */
public class SelectionManager {
    private SelectionView view;
    public boolean restrictToolInput = false;

    // Singleton pattern
    private static SelectionManager instance;

    public static SelectionManager getInstance() {
        if (instance == null) {
            instance = new SelectionManager();
        }
        return instance;
    }

    private SelectionManager() {
        view = new SelectionView();
    }

    public void startCreating() {
        view.startCreating();
    }

    // Getters and setters
    public void setSelection(Selection selection) {
        view.setNewSelection(selection);
        view.setActive(true);
    }

    public void setView(SelectionView view) {
        this.view = view;
    }

    public SelectionView getView() {
        return view;
    }

    public boolean isActive() {
        return view.isActive();
    }

    public Selection getCurrentSelection() {
        return view.getCurrentSelection();
    }
}
