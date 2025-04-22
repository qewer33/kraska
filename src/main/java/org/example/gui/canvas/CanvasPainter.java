package org.example.gui.canvas;

import java.awt.*;
import java.awt.event.MouseEvent;

public interface CanvasPainter {
    void onMousePress(Canvas canvas, MouseEvent e);
    void onMouseDrag(Canvas canvas, MouseEvent e);
    void onMouseRelease(Canvas canvas, MouseEvent e);
}
