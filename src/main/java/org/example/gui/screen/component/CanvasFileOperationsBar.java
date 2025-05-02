package org.example.gui.screen.component;

import org.example.gui.canvas.Canvas;

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
        ImageIcon openIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/open.png")));
        ImageIcon undoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/undo.png")));
        ImageIcon redoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/redo.png")));
        ImageIcon saveIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/save.png")));
        ImageIcon saveAsIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/saveAs.png")));
        ImageIcon exportIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/fileOperations/export.png")));

        // === Tool Buttons ===
        JButton newButton = new JButton(newIcon);
        JButton openButton = new JButton(openIcon);
        JButton undoButton = new JButton(undoIcon);
        undoButton.addActionListener(e->canvas.undo());
        JButton redoButton = new JButton(redoIcon);
        redoButton.addActionListener(e->canvas.redo());
        JButton saveButton = new JButton(saveIcon);
        JButton saveAsButton = new JButton(saveAsIcon);
        JButton exportButton = new JButton(exportIcon);

        // Add buttons
        addSeparator(new Dimension(5, 0));
        add(newButton);
        addSeparator(new Dimension(5, 0));
        add(openButton);
        addSeparator(new Dimension(5, 0));
        add(exportButton);
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
