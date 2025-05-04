package org.example.gui.canvas;

import java.awt.*;

/*
 * OverlayPainter is an interface that defines the method for painting overlays on top of the canvas.
 */
public interface OverlayPainter {
    void paintOverlay(Graphics2D g2d, double zoomFactor);
}
