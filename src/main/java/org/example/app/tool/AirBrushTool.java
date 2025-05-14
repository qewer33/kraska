package org.example.app.tool;

import org.example.app.color.ColorManager;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;
import org.example.gui.screen.component.ToolOptionsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class AirBrushTool extends AbstractTool implements CanvasPainter, ToolOptionsProvider{
    final private ColorManager colorManager;
    private Color color;
    private Point lastPoint;
    private int radius;
    private int density;

    public AirBrushTool(int defaultRadius , int defaultIntensity) {
        super("Air Brush");
        colorManager = ColorManager.getInstance();
        this.color = colorManager.getPrimary();
        this.radius = defaultRadius;
        this.density = defaultIntensity;
    }

    public void airBrush(Canvas canvas, int centerX, int centerY, Color brushColor, int radius, float opacity) {
        BufferedImage canvasImage = canvas.getCanvasImage();
        Graphics2D g2d = canvasImage.createGraphics();
        Random random = new Random();

        // Loop for creating 100 particles
        for (int i = 0; i < density; i++) {

            // Select a random position
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = random.nextDouble() * radius;

            // Calculate the (dx, dy) coordinates based on angle and distance
            int dx = (int) (centerX + Math.cos(angle) * distance);
            int dy = (int) (centerY + Math.sin(angle) * distance);
            Point point = new Point(dx, dy);

            //  Check if the point is on the canvas
            if (dx < 0 || dy < 0 || dx >= canvas.getLogicalSize().getWidth() || dy >= canvas.getLogicalSize().getHeight()) {
                continue; // Skip points outside the canvas
            }

            // Get the current color at the point and blend it with the brush color
            Color currentColor = canvas.getColorAt(canvas,point);
            Color blendedColor = blendColors(currentColor, brushColor, opacity);

            // Set the pixel to the blended color
            g2d.setColor(blendedColor);
            g2d.fillRect(dx, dy, 1, 1);
        }

        g2d.dispose();
        canvas.repaint();
    }

    // Color blending function
    private Color blendColors(Color base, Color blend, float opacity) {
        int red = (int) (base.getRed() * (1 - opacity) + blend.getRed() * opacity);
        int green = (int) (base.getGreen() * (1 - opacity) + blend.getGreen() * opacity);
        int blue = (int) (base.getBlue() * (1 - opacity) + blend.getBlue() * opacity);
        int alpha = (int) (base.getAlpha() * (1 - opacity) + blend.getAlpha() * opacity);
        return new Color(red, green, blue, alpha);
    }

    // Create and return a panel with tool options
    @Override
    public JPanel getToolOptionsPanel(){
        ToolOptionsPanel panel = new ToolOptionsPanel();

        JLabel sizeLabel = new JLabel("Size: " + radius + "px");
        JSlider sizeSlider = new JSlider(1, 100, radius);

        JLabel densityLabel = new JLabel("Density: " + density);
        JSlider densitySlider = new JSlider(1, 100, density);

        // Add a listener to update brush size when the slider is moved
        sizeSlider.addChangeListener(e -> {
            radius = sizeSlider.getValue();
            sizeLabel.setText("Size: " + radius + "px");
        });

        // Add a listener to update density when the slider is moved
        densitySlider.addChangeListener(e -> {
            density = densitySlider.getValue();
            densityLabel.setText("Density: " + density);
        });

        panel.addComponentGroup(new JComponent[]{sizeLabel, sizeSlider});
        panel.addComponentGroup(new JComponent[]{densityLabel, densitySlider});

        return panel;
    }

    @Override
    public void onMousePress(Canvas canvas, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
            this.color = e.getButton() == MouseEvent.BUTTON1 ? colorManager.getPrimary() : colorManager.getSecondary();

            lastPoint = canvas.getUnzoomedPoint(e.getPoint());
            airBrush(canvas, lastPoint.x, lastPoint.y, this.color,radius,0.5f);
        }
    }
    @Override
    public void onMouseDrag(Canvas canvas, MouseEvent e){
        if (lastPoint != null) {
            Point current = canvas.getUnzoomedPoint(e.getPoint());
            airBrush(canvas, lastPoint.x, lastPoint.y, this.color,radius,0.5f);
            lastPoint = current;
        }
    }
    @Override
    public void onMouseRelease(Canvas canvas, MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
            lastPoint = null;
        }
    }
}

