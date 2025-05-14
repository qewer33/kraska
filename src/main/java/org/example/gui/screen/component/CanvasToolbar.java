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
        ImageIcon airBrushIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/airBrush.png")));
        ImageIcon shapeIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape.png")));
        ImageIcon selectionIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/selection.png")));
        ImageIcon textIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/text.png")));

        // === Tool Buttons ===
        JToggleButton brushBtn = new JToggleButton(brushIcon);
        JToggleButton eraserBtn = new JToggleButton(eraserIcon);
        JToggleButton eyedropperBtn = new JToggleButton(eyedropperIcon);
        eyedropperBtn.setMargin(new Insets(0, 4, 0, 0));
        JToggleButton bucketButton = new JToggleButton(bucketIcon);
        JToggleButton airBrushButton = new JToggleButton(airBrushIcon);
        JToggleButton shapeButton = new JToggleButton(shapeIcon);
        JToggleButton selectionButton = new JToggleButton(selectionIcon);
        JToggleButton textButton = new JToggleButton(textIcon);

        ButtonGroup toolGroup = new ButtonGroup();
        toolGroup.add(brushBtn);
        toolGroup.add(eraserBtn);
        toolGroup.add(eyedropperBtn);
        toolGroup.add(bucketButton);
        toolGroup.add(airBrushButton);
        toolGroup.add(shapeButton);
        toolGroup.add(selectionButton);
        toolGroup.add(textButton);

        // Change active tool
        brushBtn.setSelected(true);
        brushBtn.addActionListener(e -> toolManager.setActiveTool("Brush"));
        eraserBtn.addActionListener(e -> toolManager.setActiveTool("Eraser"));
        eyedropperBtn.addActionListener(e -> toolManager.setActiveTool("Color Picker"));
        bucketButton.addActionListener(e -> toolManager.setActiveTool("Bucket"));
        airBrushButton.addActionListener(e -> toolManager.setActiveTool("Air Brush"));
        shapeButton.addActionListener(e -> toolManager.setActiveTool("Shape"));
        selectionButton.addActionListener(e -> toolManager.setActiveTool("Selection"));
        textButton.addActionListener(e -> toolManager.setActiveTool("Text Tool"));

        // Add buttons
        addSeparator(new Dimension(0, 5));
        add(brushBtn);
        addSeparator(new Dimension(0, 5));
        add(eraserBtn);
        addSeparator(new Dimension(0, 5));
        add(bucketButton);
        addSeparator(new Dimension(0, 5));
        add(airBrushButton);
        addSeparator(new Dimension(0, 5));
        add(eyedropperBtn);
        addSeparator(new Dimension(0, 5));
        add(shapeButton);
        addSeparator(new Dimension(0, 5));
        add(selectionButton);
        addSeparator(new Dimension(0, 5));
        add(textButton);
    }
}
