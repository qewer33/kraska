package org.example.gui.screen.component;

import org.example.app.file.AppFile;
import org.example.gui.canvas.Canvas;
import org.example.gui.screen.DashboardScreen;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class CanvasFileOperationsBar extends JToolBar {

    public CanvasFileOperationsBar(Canvas canvas) {
        setFloatable(false);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOrientation(JToolBar.HORIZONTAL);

        // Get images from resource
        ImageIcon newIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/new.png")));
        ImageIcon undoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/undo.png")));
        ImageIcon redoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/redo.png")));
        ImageIcon saveIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/save.png")));
        ImageIcon saveAsIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/saveAs.png")));

        // === Tool Buttons ===
        JButton newButton = new JButton(newIcon);
        newButton.addActionListener(e->DashboardScreen.getInstance().openCanvasSettingsWindow(null));
        newButton.setToolTipText("New Canvas");
        JButton undoButton = new JButton(undoIcon);
        undoButton.setToolTipText("Undo the Last 'do'");
        undoButton.addActionListener(e->canvas.undo());
        JButton redoButton = new JButton(redoIcon);
        redoButton.setToolTipText("Redo the Last 'do'");
        redoButton.addActionListener(e->canvas.redo());
        JButton saveButton = new JButton(saveIcon);
        saveButton.setToolTipText("Save This File");
        saveButton.addActionListener(e -> AppFile.getInstance().save());
        JButton saveAsButton = new JButton(saveAsIcon);
        saveAsButton.setToolTipText("Save This File As ..");
        saveAsButton.addActionListener(e -> AppFile.getInstance().saveAs());

        // Add buttons
        addSeparator(new Dimension(5, 0));
        add(newButton);
        addSeparator(new Dimension(5, 0));
        add(saveButton);
        addSeparator(new Dimension(5, 0));
        add(saveAsButton);
        addSeparator(new Dimension(5, 0));
        add(undoButton);
        addSeparator(new Dimension(5, 0));
        add(redoButton);
    }
}
