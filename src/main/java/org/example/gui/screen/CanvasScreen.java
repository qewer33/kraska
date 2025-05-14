package org.example.gui.screen;

import org.example.app.color.ColorManager;
import org.example.app.file.AppFile;
import org.example.app.tool.BrushTool;
import org.example.app.tool.ColorPickerTool;
import org.example.app.tool.ToolManager;
import org.example.gui.ApplicationStatusBar;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasViewer;
import org.example.gui.screen.component.CanvasFileOperationsBar;
import org.example.gui.screen.component.CanvasSidebar;
import org.example.gui.screen.component.CanvasToolbar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CanvasScreen extends AbstractScreen {
    Canvas canvas;
    CanvasViewer viewer;

    public CanvasScreen(int width, int height, Color backgroundColor) {
        this.canvas = new Canvas(width, height, backgroundColor);
        this.viewer = new CanvasViewer(this.canvas);

        AppFile.setInstance(new AppFile(this.canvas));

        ApplicationStatusBar statusBar = new ApplicationStatusBar(this.viewer);
        CanvasToolbar toolbar = new CanvasToolbar();
        CanvasFileOperationsBar fileOperationsBar = new CanvasFileOperationsBar(this.canvas);

        ToolManager toolManager = ToolManager.getInstance();

        this.setLayout(new BorderLayout());
        this.add(toolbar, BorderLayout.WEST); // Add toolbar at the top
        this.add(viewer, BorderLayout.CENTER); // Canvas viewer in center
        this.add(statusBar, BorderLayout.SOUTH);
        this.add(fileOperationsBar, BorderLayout.NORTH);

        CanvasSidebar sidebar = new CanvasSidebar();
        sidebar.updateToolOptions(toolManager.getActiveTool());
        toolManager.addToolChangeListener((oldTool, newTool) -> {
            sidebar.updateToolOptions(newTool);
        });
        this.add(sidebar, BorderLayout.EAST);  // Add sidebar at the right

        setupKeyBindings(); // Setup key bindings
    }

    public Canvas getCanvas() {
        return canvas;
    }

    private void setupKeyBindings() {
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("control Z"), "undo");
        inputMap.put(KeyStroke.getKeyStroke("control Y"), "redo");

        actionMap.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.undo(); // Trigger undo
            }
        });

        actionMap.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.redo(); // Trigger redo
            }
        });
    }

}

