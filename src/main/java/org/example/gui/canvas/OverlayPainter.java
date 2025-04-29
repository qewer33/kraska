package org.example.gui.canvas;

import java.awt.*;

public interface OverlayPainter {
    void paintOverlay(Graphics2D g2d, double zoomFactor);
}
