package org.example.app.tool;

import org.example.gui.canvas.Canvas;

public abstract class AbstractTool {
    private final String name;

    public AbstractTool(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void onActivate() {}
    public void onDeactivate() {}
}
