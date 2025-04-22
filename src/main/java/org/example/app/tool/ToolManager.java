package org.example.app.tool;

import org.example.gui.canvas.Canvas;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ToolManager {
    private final Map<String, AbstractTool> tools = new HashMap<>();
    private final Canvas canvas;

    public ToolManager(Canvas canvas) {
        this.canvas = canvas;
        registerDefaultTools();
    }

    private void registerDefaultTools() {
        registerTool(new BrushTool(Color.BLACK, 5));
        // registerTool(new BlurTool());
    }

    public void registerTool(AbstractTool tool) {
        tools.put(tool.getName(), tool);
    }

    public void activateTool(String toolName) {
        AbstractTool tool = tools.get(toolName);
        if (tool != null) {
            canvas.setCurrentTool(tool);
        }
    }
}