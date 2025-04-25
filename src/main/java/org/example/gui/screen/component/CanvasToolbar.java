package org.example.gui.screen.component;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import org.example.app.tool.ToolManager;


public class CanvasToolbar extends JToolBar {
    ToolManager toolManager;

    public CanvasToolbar() {
        toolManager = ToolManager.getInstance();
        setFloatable(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOrientation(JToolBar.VERTICAL);

        // Get images from resource
        ImageIcon brushIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush.png")));
        ImageIcon eraserIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/eraser.png")));
        ImageIcon eyedropperIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/eyedropper.png")));
        ImageIcon bucketIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/bucket.png")));

        // === Tool Buttons ===
        JToggleButton brushBtn = new JToggleButton(brushIcon);
        JToggleButton eraserBtn = new JToggleButton(eraserIcon);
        JToggleButton eyedropperBtn = new JToggleButton(eyedropperIcon);
        JToggleButton bucketButton = new JToggleButton(bucketIcon);

        ButtonGroup toolGroup = new ButtonGroup();
        toolGroup.add(brushBtn);
        toolGroup.add(eraserBtn);
        toolGroup.add(eyedropperBtn);
        toolGroup.add(bucketButton);

        // Change active tool
        brushBtn.setSelected(true);
        brushBtn.addActionListener(e -> toolManager.setActiveTool("Brush"));
        eraserBtn.addActionListener(e -> toolManager.setActiveTool("Eraser"));
        eyedropperBtn.addActionListener(e -> toolManager.setActiveTool("Color Picker"));
        bucketButton.addActionListener(e -> toolManager.setActiveTool("Bucket"));

        // Add buttons
        add(brushBtn);
        addSeparator(new Dimension(0, 10));
        add(eraserBtn);
        addSeparator(new Dimension(0, 10));
        add(eyedropperBtn);
        addSeparator(new Dimension(0, 10));
        add(bucketButton);

    }
}
