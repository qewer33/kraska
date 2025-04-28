package org.example.app.tool;

import org.example.app.color.ColorManager;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;

import org.example.gui.screen.component.ToolOptionsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class BrushTool extends AbstractTool implements CanvasPainter, ToolOptionsProvider {
    public enum BrushShape {
        BASIC,
        MARKER,
        CALLIGRAPHY,
        PENCIL
    }

    private final ColorManager colorManager;
    private Color color;
    private Point lastPoint;

    private BrushShape brushShape = BrushShape.BASIC;
    private int size;
    private float force = 1.0f;
    private boolean antialiased;

    public BrushTool(Color defaultColor, int defaultSize) {
        super("Brush");
        colorManager = ColorManager.getInstance();
        this.color = colorManager.getPrimary();
        this.size = defaultSize;
        this.antialiased = true;
    }

    @Override
    public void onMousePress(Canvas canvas, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
            this.color = e.getButton() == MouseEvent.BUTTON1 ? colorManager.getPrimary() : colorManager.getSecondary();

            canvas.setTempBufferAlpha(force);
            canvas.clearTempBuffer();

            lastPoint = canvas.getUnzoomedPoint(e.getPoint());
            draw(canvas, lastPoint, lastPoint);
        }
    }

    @Override
    public void onMouseDrag(Canvas canvas, MouseEvent e) {
        if (lastPoint != null) {
            Point current = canvas.getUnzoomedPoint(e.getPoint());
            draw(canvas, lastPoint, current);
            lastPoint = current;
        }
    }

    @Override
    public void onMouseRelease(Canvas canvas, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
            canvas.applyTempBuffer(force);
            lastPoint = null;
        }
    }


    public JPanel getToolOptionsPanel() {
        ToolOptionsPanel panel = new ToolOptionsPanel();

        JLabel brushTypeLabel = new JLabel("Brush: " + getBrushShapeName());
        JPanel brushShapePanel = new JPanel(new GridLayout(1, 4, 5, 5));
        ButtonGroup brushShapeGroup = new ButtonGroup();

        HashMap<BrushShape, ImageIcon> brushIcons = new HashMap<>();
        brushIcons.put(BrushShape.BASIC, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_basic.jpg"))));
        brushIcons.put(BrushShape.MARKER, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_marker.jpg"))));
        brushIcons.put(BrushShape.CALLIGRAPHY, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_calligraphy.jpg"))));
        brushIcons.put(BrushShape.PENCIL, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_pencil.jpg"))));

        for (BrushShape shape : BrushShape.values()) {
            JToggleButton button = new JToggleButton(brushIcons.get(shape));
            button.addActionListener(e -> {
                brushShape = shape;
                brushTypeLabel.setText("Brush: " + getBrushShapeName());
            });
            brushShapeGroup.add(button);
            brushShapePanel.add(button);

            // Select default active button
            if (shape == brushShape) {
                button.setSelected(true);
            }
        }

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

        panel.addComponentGroup(new JComponent[]{brushTypeLabel, brushShapePanel});
        panel.addComponentGroup(new JComponent[]{sizeLabel, sizeSlider});
        panel.addComponentGroup(new JComponent[]{forceLabel, forceSlider});
        panel.addComponent(antialiasCheckbox);

        return panel;
    }

    private void draw(Canvas canvas, Point from, Point to) {
        if (brushShape == BrushShape.BASIC) {
            drawDirectLine(canvas, from, to);
        } else {
            drawInterpolated(canvas, from, to);
        }
    }

    private void drawDirectLine(Canvas canvas, Point from, Point to) {
        Graphics2D g2d = canvas.getTempGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                this.antialiased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        if (from.equals(to)) {
            g2d.fillOval(from.x - size/2, from.y - size/2, size, size);
        } else {
            g2d.drawLine(from.x, from.y, to.x, to.y);
        }

        g2d.dispose();
        canvas.repaint();
    }

    private void drawInterpolated(Canvas canvas, Point from, Point to) {
        Graphics2D g2d = canvas.getTempGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                this.antialiased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setColor(color);

        float samplingFactor = 0.05f;
        switch (brushShape) {
            case MARKER -> {samplingFactor = 1.4f;}
            case CALLIGRAPHY -> {samplingFactor = 1.5f;}
            case PENCIL -> {samplingFactor = 0.6f;}
            default -> {samplingFactor = 1.0f;}
        }

        double distance = from.distance(to);
        int steps = (int) (distance * samplingFactor);
        if (steps < 1) steps = 1;

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            int x = (int) (from.x + (to.x - from.x) * t);
            int y = (int) (from.y + (to.y - from.y) * t);

            drawBrushShape(g2d, x, y);
        }

        g2d.dispose();
        canvas.repaint();
    }

    private void drawBrushShape(Graphics2D g2d, int x, int y) {
        switch (brushShape) {
            case BASIC -> {
                g2d.fillOval(x - size / 2, y - size / 2, size, size);
            }
            case MARKER -> {
                float radius = size;
                Point2D center = new Point2D.Float(x, y);
                float[] dist = {0f, 0.15f, 0.2f, 1f};
                Color[] colors = {
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 100),
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 80),
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 20),
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
                };

                RadialGradientPaint paint = new RadialGradientPaint(center, radius, dist, colors);

                Paint oldPaint = g2d.getPaint();
                g2d.setPaint(paint);
                g2d.fillOval(x - size, y - size, size * 2, size * 2);
                g2d.setPaint(oldPaint);
            }
            case CALLIGRAPHY -> {
                AffineTransform old = g2d.getTransform();
                Paint oldPaint = g2d.getPaint();

                g2d.translate(x, y);
                g2d.rotate(Math.toRadians(45));

                int width = size;
                int height = Math.max(size / 4, 1);

                GradientPaint gp = new GradientPaint(
                        -width / 2, 0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 220),
                        width / 2, 0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 65)
                );

                g2d.setPaint(gp);
                g2d.fillRect(-width / 2, -height / 2, width, height);

                g2d.setPaint(oldPaint);
                g2d.setTransform(old);
            }
            case PENCIL -> {
                Random rand = new Random();

                int circles = 3 + rand.nextInt(17); // 10 to 20 circles
                int sprayRadius = size / 2;

                for (int i = 0; i < circles; i++) {
                    int offsetX = rand.nextInt(sprayRadius * 2) - sprayRadius;
                    int offsetY = rand.nextInt(sprayRadius * 2) - sprayRadius;

                    int circleSize = Math.max(2, size / 4 + rand.nextInt(size / 4)); // smallish random size

                    int alpha = 5 + rand.nextInt(30); // 80-180 opacity range
                    Color variedColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);

                    Paint oldPaint = g2d.getPaint();
                    g2d.setPaint(variedColor);

                    g2d.fillOval(x + offsetX - circleSize / 2, y + offsetY - circleSize / 2, circleSize, circleSize);

                    g2d.setPaint(oldPaint);
                }
            }
        }
    }

    public String getBrushShapeName() {
        return brushShape.name().substring(0, 1).toUpperCase() + brushShape.name().substring(1).toLowerCase();
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public float getForce() {
        return force;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public boolean isAntialiased() {
        return antialiased;
    }

    public void setAntialiased(boolean antialiased) {
        this.antialiased = antialiased;
    }
}