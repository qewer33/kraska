package org.example.gui.screen;

import org.example.gui.canvas.Canvas;

import java.awt.*;

public class CanvasScreen extends AbstractScreen {
    Canvas canvas;

    public CanvasScreen() {
        this.canvas = new Canvas();
    }
}
