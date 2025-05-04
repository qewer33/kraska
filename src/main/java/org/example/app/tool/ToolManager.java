package org.example.app.tool;

import java.awt.*;
import java.util.*;

/*
 * ToolManager manages the active tool and the list of available tools.
 * It is a manager class that uses the singleton pattern.
 */
public class ToolManager {
    private final Map<String, AbstractTool> tools = new HashMap<>();
    private AbstractTool activeTool;

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
        registerTool(new BrushTool(Color.BLACK, 12));
        registerTool(new EraserTool(12));
        registerTool(new ColorPickerTool());
        registerTool(new BucketTool());
        registerTool(new AirBrushTool(12,50));
        registerTool(new ShapeTool());
        registerTool(new SelectionTool());
        registerTool(new TextTool());
    }

    public void registerTool(AbstractTool tool) {
        tools.put(tool.getName(), tool);
    }

    public AbstractTool getTool(String name) {
        return tools.get(name);
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
