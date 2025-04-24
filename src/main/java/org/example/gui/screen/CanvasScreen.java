package org.example.gui.screen;

import org.example.app.color.ColorManager;
import org.example.app.tool.BrushTool;
import org.example.app.tool.ColorPickerTool;
import org.example.app.tool.ToolManager;
import org.example.gui.ApplicationStatusBar;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasViewer;
import org.example.gui.screen.component.CanvasSidebar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CanvasScreen extends AbstractScreen {
    Canvas canvas;
    CanvasViewer viewer;

    // Managers
    private final ColorManager colorManager;
    private final ToolManager toolManager;

    private final BrushTool brushTool;
    private final ColorPickerTool colorPickerTool;

    // Color buttons
    private JButton primaryColorBtn;
    private JButton secondaryColorBtn;

    public CanvasScreen(int width, int height, Color backgroundColor) {
        this.canvas = new Canvas(width, height, backgroundColor);
        this.viewer = new CanvasViewer(this.canvas);
        ApplicationStatusBar statusBar = new ApplicationStatusBar(this.viewer);

        colorManager = ColorManager.getInstance();
        toolManager = ToolManager.getInstance();

        brushTool = (BrushTool) toolManager.getTool("Brush");
        colorPickerTool = (ColorPickerTool) toolManager.getTool("Color Picker");

        this.setLayout(new BorderLayout());
        this.add(createToolbar(), BorderLayout.NORTH); // Add toolbar at the top
        this.add(viewer, BorderLayout.CENTER);         // Canvas viewer in center
        this.add(statusBar, BorderLayout.SOUTH);

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

    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Add spacing

        // === Tool Buttons ===
        JToggleButton brushBtn = new JToggleButton("Brush");
        JToggleButton eraserBtn = new JToggleButton("Eraser");
        JToggleButton eyedropperBtn = new JToggleButton("Color Picker");

        ButtonGroup toolGroup = new ButtonGroup();
        toolGroup.add(brushBtn);
        toolGroup.add(eraserBtn);
        toolGroup.add(eyedropperBtn);

        brushBtn.setSelected(true);
        brushBtn.addActionListener(e -> toolManager.setActiveTool("Brush"));
        eraserBtn.addActionListener(e -> toolManager.setActiveTool("Eraser"));
        eyedropperBtn.addActionListener(e -> toolManager.setActiveTool("Color Picker"));

        toolbar.add(brushBtn);
        toolbar.add(eraserBtn);
        toolbar.add(eyedropperBtn);
        toolbar.addSeparator(new Dimension(20, 0));

        return toolbar;
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

