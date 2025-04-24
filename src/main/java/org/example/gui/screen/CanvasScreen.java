package org.example.gui.screen;

import org.example.app.color.ColorManager;
import org.example.app.tool.BrushTool;
import org.example.app.tool.EyedropperTool;
import org.example.app.tool.ToolManager;
import org.example.gui.ApplicationStatusBar;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasViewer;

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
    private final EyedropperTool eyedropperTool;

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
        eyedropperTool = (EyedropperTool) toolManager.getTool("Eyedropper");

        this.setLayout(new BorderLayout());
        this.add(createToolbar(), BorderLayout.NORTH); // Add toolbar at the top
        this.add(viewer, BorderLayout.CENTER);         // Canvas viewer in center
        this.add(statusBar, BorderLayout.SOUTH);
        // this.add(createSidebar(), BorderLayout.EAST);  // Add sidebar at the right

        setupKeyBindings(); // Setup key bindings
    }

    public Canvas getCanvas() {
        return canvas;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return sidebar;
    }


    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Add spacing


        // === Tool Buttons ===
        JToggleButton brushBtn = new JToggleButton("Brush");
        JToggleButton eraserBtn = new JToggleButton("Eraser");
        JToggleButton eyedropperBtn = new JToggleButton("Eyedropper");

        ButtonGroup toolGroup = new ButtonGroup();
        toolGroup.add(brushBtn);
        toolGroup.add(eraserBtn);
        toolGroup.add(eyedropperBtn);

        brushBtn.setSelected(true);
        brushBtn.addActionListener(e -> toolManager.setActiveTool("Brush"));
        eraserBtn.addActionListener(e -> toolManager.setActiveTool("Eraser"));
        eyedropperBtn.addActionListener(e -> {
            toolManager.setActiveTool("Eyedropper");
            eyedropperTool.setColorUpdateCallback((isPrimary, color) -> {
                if (isPrimary) {
                    primaryColorBtn.setBackground(color);
                } else {
                    secondaryColorBtn.setBackground(color);
                }
            });
        });

        toolbar.add(brushBtn);
        toolbar.add(eraserBtn);
        toolbar.add(eyedropperBtn);
        toolbar.addSeparator(new Dimension(20, 0));

        // === Color Buttons ===
        primaryColorBtn = new JButton();
        primaryColorBtn.setPreferredSize(new Dimension(30, 30));
        primaryColorBtn.setBackground(colorManager.getPrimary());
        primaryColorBtn.setOpaque(true);
        primaryColorBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        primaryColorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Pick Primary Color", colorManager.getPrimary());
            if (chosen != null) {
                primaryColorBtn.setBackground(chosen);
                colorManager.setPrimary(chosen);
            }
        });

        secondaryColorBtn = new JButton();
        secondaryColorBtn.setPreferredSize(new Dimension(30, 30));
        secondaryColorBtn.setBackground(colorManager.getSecondary());
        secondaryColorBtn.setOpaque(true);
        secondaryColorBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        secondaryColorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Pick Secondary Color", colorManager.getSecondary());
            if (chosen != null) {
                secondaryColorBtn.setBackground(chosen);
                colorManager.setSecondary(chosen);
            }
        });

        JButton swapBtn = new JButton("↔");
        swapBtn.addActionListener(e -> {
            colorManager.swap();
            Color temp = primaryColorBtn.getBackground();
            primaryColorBtn.setBackground(secondaryColorBtn.getBackground());
            secondaryColorBtn.setBackground(temp);
        });

        // === Brush Size Slider ===
        JLabel brushLabel = new JLabel("Brush Size:");
        JLabel brushValueLabel = new JLabel(brushTool.getSize() + "px");
        JSlider brushSlider = new JSlider(1, 50, brushTool.getSize());
        brushSlider.setPreferredSize(new Dimension(120, 40)); // Limit width
        brushSlider.setPaintTicks(false);
        brushSlider.setPaintLabels(false);
        brushSlider.addChangeListener(e -> {
            brushTool.setSize(brushSlider.getValue());
            brushValueLabel.setText(brushTool.getSize() + "px");
        });

        // === Add to toolbar ===
        toolbar.add(primaryColorBtn);
        toolbar.add(swapBtn);
        toolbar.add(secondaryColorBtn);
        toolbar.addSeparator(new Dimension(20, 0));
        toolbar.add(brushLabel);
        toolbar.add(brushValueLabel);
        toolbar.add(brushSlider);

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

