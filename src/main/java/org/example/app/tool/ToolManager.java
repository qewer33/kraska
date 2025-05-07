package org.example.app.tool;

import org.example.app.ActionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/*
 * ToolManager manages the active tool and the list of available tools.
 * It is a manager class that uses the singleton pattern.
 */
public class ToolManager {
    private final LinkedHashMap<String, AbstractTool> tools = new LinkedHashMap<>();
    private final HashMap<String, Icon> toolIcons = new HashMap<>();
    private AbstractTool activeTool;

    private final ActionManager actionManager = ActionManager.getInstance();

    // Listener support
    private final ArrayList<ToolChangeListener> listeners = new ArrayList<>();

    // Singleton pattern
    private static ToolManager instance;

    public static ToolManager getInstance() {
        if (instance == null) {
            instance = new ToolManager("Brush");
        }
        return instance;
    }

    private ToolManager(String defaultTool) {
        registerDefaultTools();
        setActiveTool(defaultTool);
    }

    private void registerDefaultTools() {
        ImageIcon brushIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/brush.png")));
        ImageIcon eraserIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/eraser.png")));
        ImageIcon eyedropperIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/eyedropper.png")));
        ImageIcon bucketIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/bucket.png")));
        ImageIcon airBrushIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/airBrush.png")));
        ImageIcon shapeIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/shape.png")));
        ImageIcon selectionIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/selection.png")));
        ImageIcon textIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/tools/text.png")));

        registerTool(new BrushTool(Color.BLACK, 12), brushIcon);
        registerTool(new EraserTool(12), eraserIcon);
        registerTool(new ColorPickerTool(), eyedropperIcon);
        registerTool(new BucketTool(), bucketIcon);
        registerTool(new AirBrushTool(12,50), airBrushIcon);
        registerTool(new ShapeTool(), shapeIcon);
        registerTool(new SelectionTool(), selectionIcon);
        registerTool(new TextTool(), textIcon);

        for (AbstractTool tool : getTools()) {
            String name = tool.getName();
            actionManager.registerAction(name, new AbstractAction(name, getToolIcon(name) ) {
                {
                    putValue(Action.SELECTED_KEY,false);
                }

                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() instanceof AbstractButton button) {
                        boolean selected = button.isSelected();
                        putValue(Action.SELECTED_KEY, selected); // Sync back to the action
                    }
                    getInstance().setActiveTool(name);
                }
            });
        }
    }

    public void registerTool(AbstractTool tool, Icon toolIcon) {
        tools.put(tool.getName(), tool);
        toolIcons.put(tool.getName(), toolIcon);
    }

    public AbstractTool getTool(String name) {
        return tools.get(name);
    }

    public Icon getToolIcon(String name) {
        return toolIcons.get(name);
    }

    public ArrayList<AbstractTool> getTools() {
        return new ArrayList<>(tools.values());
    }

    public void setActiveTool(String toolName) {
        AbstractTool newTool = tools.get(toolName);
        if (newTool != null && newTool != activeTool) {
            AbstractTool oldTool = activeTool;
            if (oldTool != null) oldTool.onDeactivate();
            activeTool = newTool;
            activeTool.onActivate();
            notifyToolChanged(oldTool, newTool);
        }
    }

    public AbstractTool getActiveTool() {
        return activeTool;
    }

    // 🔧 Listener registration
    public void addToolChangeListener(ToolChangeListener listener) {
        listeners.add(listener);
    }

    public void removeToolChangeListener(ToolChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyToolChanged(AbstractTool oldTool, AbstractTool newTool) {
        for (ToolChangeListener listener : listeners) {
            listener.onToolChanged(oldTool, newTool);
        }
    }

    // 🔧 Listener interface
    public interface ToolChangeListener {
        void onToolChanged(AbstractTool oldTool, AbstractTool newTool);
    }
}
