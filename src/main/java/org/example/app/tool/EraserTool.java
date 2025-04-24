package org.example.app.tool;

import org.example.app.color.ColorManager;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class EraserTool extends AbstractTool implements CanvasPainter {
    private Color color;
    private int size;
    private Point lastPoint;

    public EraserTool(int defaultSize) {
        super("Eraser");
        this.size = defaultSize;
    }

    @Override
    public void onMousePress(org.example.gui.canvas.Canvas canvas, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
            lastPoint = canvas.getUnzoomedPoint(e.getPoint());
            erase(canvas, lastPoint, lastPoint);
        }
    }

    @Override
    public void onMouseDrag(org.example.gui.canvas.Canvas canvas, MouseEvent e) {
        if (lastPoint != null) {
            Point current = canvas.getUnzoomedPoint(e.getPoint());
            erase(canvas, lastPoint, current);
            lastPoint = current;
        }
    }

    @Override
    public void onMouseRelease(org.example.gui.canvas.Canvas canvas, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
            lastPoint = null;
        }
    }

    private void erase(Canvas canvas, Point from, Point to) {
        BufferedImage canvasImage = canvas.getCanvasImage();
        Graphics2D g2d = canvasImage.createGraphics();

        // Enable smooth edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set composite mode to CLEAR — this makes pixels fully transparent
        g2d.setComposite(AlphaComposite.Clear);

        // Erase using a round brush stroke
        g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (from.equals(to)) {
            g2d.fillOval(from.x - size / 2, from.y - size / 2, size, size);
        } else {
            g2d.drawLine(from.x, from.y, to.x, to.y);
        }

        g2d.dispose();
        canvas.repaint();
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
