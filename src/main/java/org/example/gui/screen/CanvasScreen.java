package org.example.gui.screen;

import org.example.app.color.ColorManager;
import org.example.app.tool.BrushTool;
import org.example.app.tool.ToolManager;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasViewer;

import javax.swing.*;
import java.awt.*;

public class CanvasScreen extends AbstractScreen {
    Canvas canvas;
    CanvasViewer viewer;

    // Managers
    private final ColorManager colorManager;
    private final ToolManager toolManager;

    private final BrushTool brushTool;

    public CanvasScreen() {
        this.canvas = new Canvas();
        this.viewer = new CanvasViewer(this.canvas);

        colorManager = ColorManager.getInstance();
        toolManager = ToolManager.getInstance();

        brushTool = (BrushTool) toolManager.getTool("Brush");

        this.setLayout(new BorderLayout());
        this.add(createToolbar(), BorderLayout.NORTH); // Add toolbar at the top
        this.add(viewer, BorderLayout.CENTER);         // Canvas viewer in center
    }

    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Add spacing

        // === Color Buttons ===
        JButton primaryColorBtn = new JButton();
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

        JButton secondaryColorBtn = new JButton();
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
        swapBtn.setMargin(new Insets(3, 8, 3, 8));
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
}

