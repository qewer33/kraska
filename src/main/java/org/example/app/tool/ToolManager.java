package org.example.app.tool;

import org.example.app.color.ColorManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ToolManager {
    private final Map<String, AbstractTool> tools = new HashMap<>();
    private AbstractTool activeTool;

    // Singleton design pattern for manager classes
    private static ToolManager instance;

    public static ToolManager getInstance() {
        if (instance == null) {
            instance = new ToolManager("Brush");
        }
        return instance;
    }

    private ToolManager(String toolName) {
        registerDefaultTools();
        setActiveTool(toolName);
    }

    private void registerDefaultTools() {
        registerTool(new BrushTool(Color.BLACK, 5));
        registerTool(new EyedropperTool());
    }

    public void registerTool(AbstractTool tool) {
        tools.put(tool.getName(), tool);
    }

    public AbstractTool getTool(String toolName) {
        return tools.get(toolName);
    }

    public void setActiveTool(String toolName) {
        AbstractTool tool = tools.get(toolName);
        if (tool != null) {
            activeTool = tool;
        }
    }

    public AbstractTool getActiveTool() {
        return activeTool;
    }
}