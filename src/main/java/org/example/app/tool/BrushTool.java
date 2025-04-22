package org.example.app.tool;

import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;

import org.example.gui.canvas.Canvas;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class BrushTool extends AbstractTool implements CanvasPainter {
    private Color color;
    private int size;
    private Point lastPoint;

    public BrushTool(Color defaultColor, int defaultSize) {
        super("Brush");
        this.color = defaultColor;
        this.size = defaultSize;
    }

    @Override
    public void onMousePress(Canvas canvas, MouseEvent e) {
        lastPoint = e.getPoint();
        draw(canvas, e.getPoint(), e.getPoint()); // Draw initial dot
    }

    @Override
    public void onMouseDrag(Canvas canvas, MouseEvent e) {
        if (lastPoint != null) {
            draw(canvas, lastPoint, e.getPoint());
            lastPoint = e.getPoint();
        }
    }

    @Override
    public void onMouseRelease(Canvas canvas, MouseEvent e) {
        if (lastPoint != null) {
            draw(canvas, lastPoint, e.getPoint()); // Finalize line
            lastPoint = null;
        }
    }

    private void draw(Canvas canvas, Point from, Point to) {
        BufferedImage canvasImage = canvas.getCanvasImage();
        Graphics2D g2d = canvasImage.createGraphics();

        // Configure graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        // Draw the line or dot
        if (from.equals(to)) {
            g2d.fillOval(from.x - size/2, from.y - size/2, size, size);
        } else {
            g2d.drawLine(from.x, from.y, to.x, to.y);
        }

        g2d.dispose();
        canvas.repaint();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setSize(int size) {
        this.size = size;
    }
}