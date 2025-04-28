package org.example.app.tool;

import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;
import org.example.gui.screen.component.ToolOptionsPanel;

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

    public JPanel getToolOptionsPanel() {
        ToolOptionsPanel panel = new ToolOptionsPanel();

        JLabel sizeLabel = new JLabel("Size: " + size + "px");
        JSlider sizeSlider = new JSlider(1, 100, size);
        sizeSlider.addChangeListener(e -> {
            size = sizeSlider.getValue();
            sizeLabel.setText("Size: " + size + "px");
        });

        // Force slider (transparency strength)
        JLabel forceLabel = new JLabel("Force: " + (int)(force * 100) + "%");
        JSlider forceSlider = new JSlider(1, 100, (int)(force * 100));
        forceSlider.addChangeListener(e -> {
            force = forceSlider.getValue() / 100f;
            forceLabel.setText("Force: " + forceSlider.getValue() + "%");
        });

        JCheckBox antialiasCheckbox = new JCheckBox("Antialiasing");
        antialiasCheckbox.setSelected(antialiased);
        antialiasCheckbox.addItemListener(e -> {
            antialiased = antialiasCheckbox.isSelected();
        });

        panel.addComponentGroup(new JComponent[]{sizeLabel, sizeSlider});
        panel.addComponentGroup(new JComponent[]{forceLabel, forceSlider});
        panel.addComponent(antialiasCheckbox);

        return panel;
    }

    private void erase(Canvas canvas, Point from, Point to) {
        BufferedImage canvasImage = canvas.getCanvasImage();

        int steps = (int) from.distance(to); // how many steps along the line
        if (steps == 0) steps = 1; // prevent division by zero

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            int xPos = (int) (from.x + (to.x - from.x) * t);
            int yPos = (int) (from.y + (to.y - from.y) * t);

            eraseCircle(canvasImage, xPos, yPos, size);
        }

        canvas.repaint();
    }

    private void eraseCircle(BufferedImage img, int centerX, int centerY, int radius) {
        int rSquared = radius * radius;

        int minX = Math.max(0, centerX - radius);
        int minY = Math.max(0, centerY - radius);
        int maxX = Math.min(img.getWidth() - 1, centerX + radius);
        int maxY = Math.min(img.getHeight() - 1, centerY + radius);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                int dx = x - centerX;
                int dy = y - centerY;

                if (dx * dx + dy * dy <= rSquared) { // inside the circle
                    int rgba = img.getRGB(x, y);
                    int alpha = (rgba >> 24) & 0xFF;
                    int red = (rgba >> 16) & 0xFF;
                    int green = (rgba >> 8) & 0xFF;
                    int blue = rgba & 0xFF;

                    int newAlpha = (int)(alpha * (1.0f - force));
                    if (newAlpha < 0) newAlpha = 0;

                    int newRGBA = (newAlpha << 24) | (red << 16) | (green << 8) | blue;
                    img.setRGB(x, y, newRGBA);
                }
            }
        }
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
