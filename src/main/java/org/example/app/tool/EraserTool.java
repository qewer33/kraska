package org.example.app.tool;

import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class EraserTool extends AbstractTool implements CanvasPainter, ToolOptionsProvider {
    private Point lastPoint;
    private int size;
    private boolean antialiased;
    private float force; // New: 0.0 (weak) to 1.0 (full erase)

    public EraserTool(int defaultSize) {
        super("Eraser");
        this.size = defaultSize;
        this.antialiased = true;
        this.force = 1.0f; // Default to full strength erase
    }

    @Override
    public void onMousePress(Canvas canvas, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
            lastPoint = canvas.getUnzoomedPoint(e.getPoint());
            erase(canvas, lastPoint, lastPoint);
        }
    }

    @Override
    public void onMouseDrag(Canvas canvas, MouseEvent e) {
        if (lastPoint != null) {
            Point current = canvas.getUnzoomedPoint(e.getPoint());
            erase(canvas, lastPoint, current);
            lastPoint = current;
        }
    }

    @Override
    public void onMouseRelease(Canvas canvas, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
            lastPoint = null;
        }
    }

    @Override
    public JPanel getToolOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Size slider
        JLabel sizeLabel = new JLabel("Size: " + size + "px");
        JSlider sizeSlider = new JSlider(1, 100, size);
        sizeSlider.addChangeListener(e -> {
            size = sizeSlider.getValue();
            sizeLabel.setText("Size: " + size + "px");
        });

        panel.add(sizeLabel);
        panel.add(sizeSlider);

        // Force slider (transparency strength)
        JLabel forceLabel = new JLabel("Force: " + (int)(force * 100) + "%");
        JSlider forceSlider = new JSlider(1, 100, (int)(force * 100));
        forceSlider.addChangeListener(e -> {
            force = forceSlider.getValue() / 100f;
            forceLabel.setText("Force: " + forceSlider.getValue() + "%");
        });

        panel.add(forceLabel);
        panel.add(forceSlider);

        // Antialias checkbox
        JCheckBox antialiasCheckbox = new JCheckBox("Antialiasing");
        antialiasCheckbox.setSelected(antialiased);
        antialiasCheckbox.addItemListener(e -> {
            antialiased = antialiasCheckbox.isSelected();
        });
        panel.add(antialiasCheckbox);

        return panel;
    }

    private void erase(Canvas canvas, Point from, Point to) {
        BufferedImage canvasImage = canvas.getCanvasImage();

        Graphics2D g2d = canvasImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                this.antialiased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.dispose(); // we will not draw anything directly!

        // Calculate the bounding rectangle
        int minX = Math.min(from.x, to.x) - size / 2;
        int minY = Math.min(from.y, to.y) - size / 2;
        int maxX = Math.max(from.x, to.x) + size / 2;
        int maxY = Math.max(from.y, to.y) + size / 2;

        // Clamp to canvas bounds
        minX = Math.max(0, minX);
        minY = Math.max(0, minY);
        maxX = Math.min(canvasImage.getWidth() - 1, maxX);
        maxY = Math.min(canvasImage.getHeight() - 1, maxY);

        // Loop over the pixels inside the affected area
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                // Check if pixel is within the eraser circle
                double dist = from.distance(x, y);
                if (dist <= size / 2.0) {
                    int rgba = canvasImage.getRGB(x, y);
                    int alpha = (rgba >> 24) & 0xFF;
                    int red = (rgba >> 16) & 0xFF;
                    int green = (rgba >> 8) & 0xFF;
                    int blue = rgba & 0xFF;

                    // Calculate new alpha
                    int newAlpha = (int)(alpha * (1.0f - force));
                    if (newAlpha < 0) newAlpha = 0;

                    // Write pixel back
                    int newRGBA = (newAlpha << 24) | (red << 16) | (green << 8) | blue;
                    canvasImage.setRGB(x, y, newRGBA);
                }
            }
        }

        canvas.repaint();
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
