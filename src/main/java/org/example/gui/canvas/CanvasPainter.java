package org.example.gui.canvas;

import java.awt.event.MouseEvent;

/*
 * CanvasPainter is an interface that defines the method for painting the canvas.
 * It is used by tools that extend AbstractTool to paint on the canvas.
 * It is an integral part of Kraska's painting engine.
 */
public interface CanvasPainter {
    void onMousePress(Canvas canvas, MouseEvent e);
    void onMouseDrag(Canvas canvas, MouseEvent e);
    void onMouseRelease(Canvas canvas, MouseEvent e);
}
