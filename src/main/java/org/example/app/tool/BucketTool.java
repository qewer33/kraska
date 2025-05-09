package org.example.app.tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;
import org.example.gui.canvas.Canvas;
import org.example.app.color.ColorManager;
import org.example.gui.canvas.CanvasPainter;



public class BucketTool extends AbstractTool implements CanvasPainter {
    private ColorManager colorManager;
    private Color color;

    public BucketTool() {
        super("Bucket");
        colorManager = ColorManager.getInstance();
        this.color = colorManager.getPrimary();
    }

    public void fill(Canvas canvas, Point mousePoint, Color newColor) {
        BufferedImage canvasImage = canvas.getCanvasImage();
        Graphics2D g2d = canvasImage.createGraphics();

        Color oldColor = canvas.getColorAt(canvas,mousePoint);
        if (oldColor.equals(newColor)) return;

        Queue<Point> queue = new LinkedList<>();
        queue.add(mousePoint);

        while (!queue.isEmpty()) {
            Point point = queue.poll();
            int px = point.x;
            int py = point.y;

            // Check if point is on the canvas and not the same color with already colored points
            if (px < 0 || py < 0 || px >= canvas.getWidth()/canvas.getZoomFactor() || py >= canvas.getHeight()/canvas.getZoomFactor() || !(canvas.getColorAt(canvas , point).equals(oldColor))) {
                continue;
            }

            // Set the pixel to the chosen color
            g2d.setColor(newColor);
            g2d.fillRect(px, py, 1, 1);

            // Add surrounding pixels to the queue
            queue.add(new Point(px + 1, py));
            queue.add(new Point(px - 1, py));
            queue.add(new Point(px, py + 1));
            queue.add(new Point(px, py - 1));
        }

        g2d.dispose();
        canvas.repaint();
    }

    @Override
    public void onMousePress(Canvas canvas, MouseEvent e) {
        Point point = canvas.getUnzoomedPoint(e.getPoint());
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
            this.color = e.getButton() == MouseEvent.BUTTON1 ? colorManager.getPrimary() : colorManager.getSecondary();

            fill(canvas, point, this.color);
        }
    }
    @Override
    public void onMouseDrag(Canvas canvas, MouseEvent e){}
    @Override
    public void onMouseRelease(Canvas canvas, MouseEvent e){}
}
