package org.example.gui.screen;

import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasViewer;

import java.awt.*;

public class CanvasScreen extends AbstractScreen {
    Canvas canvas;
    CanvasViewer viewer;

    public CanvasScreen() {
        this.canvas = new Canvas();
        this.viewer = new CanvasViewer(this.canvas);

        this.setLayout(new BorderLayout());
        this.add(viewer, BorderLayout.CENTER);
    }
}
