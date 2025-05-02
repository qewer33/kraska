package org.example.gui.screen.component;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
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
        ImageIcon brushIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/brush.png")));
        ImageIcon eraserIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/eraser.png")));
        ImageIcon eyedropperIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/eyedropper.png")));
        ImageIcon bucketIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/bucket.png")));
        ImageIcon airBrushIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/airBrush.png")));
        ImageIcon shapeIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/shape.png")));
        ImageIcon selectionIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/selection.png")));
        ImageIcon textIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/text.png")));

        // === Tool Buttons ===
        JToggleButton brushBtn = new JToggleButton(brushIcon);
        brushBtn.setSelected(true);
        JToggleButton eraserBtn = new JToggleButton(eraserIcon);
        JToggleButton eyedropperBtn = new JToggleButton(eyedropperIcon);
        eyedropperBtn.setMargin(new Insets(0, 4, 0, 0));
        JToggleButton bucketButton = new JToggleButton(bucketIcon);
        JToggleButton airBrushButton = new JToggleButton(airBrushIcon);
        JToggleButton shapeButton = new JToggleButton(shapeIcon);
        JToggleButton selectionButton = new JToggleButton(selectionIcon);
        JToggleButton textButton = new JToggleButton(textIcon);

        ButtonGroup toolGroup = new ButtonGroup();

        HashMap<String, JToggleButton> toolButtons = new HashMap<>();
        toolButtons.put("Brush", brushBtn);
        toolButtons.put("Eraser", eraserBtn);
        toolButtons.put("Color Picker", eyedropperBtn);
        toolButtons.put("Bucket", bucketButton);
        toolButtons.put("Air Brush", airBrushButton);
        toolButtons.put("Shape", shapeButton);
        toolButtons.put("Selection", selectionButton);
        toolButtons.put("Text Tool", textButton);
        toolButtons.forEach((name, button) -> {
            toolGroup.add(button);
            button.addActionListener(e -> {
                toolManager.setActiveTool(name);
            });
        });

        toolManager.addToolChangeListener((oldTool, newTool) -> {
            toolButtons.get(newTool.getName()).setSelected(true);
        });

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
