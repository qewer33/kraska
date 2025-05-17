package org.example.gui.screen.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Objects;

import org.example.app.ActionManager;
import org.example.app.tool.ToolManager;


public class CanvasToolbar extends JToolBar {
    ToolManager toolManager;

    public CanvasToolbar() {
        toolManager = ToolManager.getInstance();
        setFloatable(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOrientation(JToolBar.VERTICAL);

        // Create tool buttons
        ButtonGroup toolGroup = new ButtonGroup();
        toolManager.getTools().forEach((tool) -> {
            JToggleButton button = new JToggleButton();
            ActionManager actionManager = ActionManager.getInstance();
            button.setAction(actionManager.getAction(tool.getName()));
            button.setHideActionText(true);
            button.setToolTipText(tool.getName());
            if (tool.getName().equals("Brush")) button.setSelected(true);

            toolGroup.add(button);
            addSeparator(new Dimension(0, 5));
            add(button);
        });

        toolManager.addToolChangeListener((oldTool, newTool) -> {
            for (Component component : getComponents()) {
                if (component instanceof JToggleButton button) {
                    if (button.getToolTipText().equals(newTool.getName())) {
                        button.setSelected(true);
                    }
                }
            }
        });
    }
}
