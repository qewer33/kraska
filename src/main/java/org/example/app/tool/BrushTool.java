package org.example.app.tool;

import org.example.app.color.ColorManager;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;

import org.example.gui.canvas.Canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class BrushTool extends AbstractTool implements CanvasPainter, ToolOptionsProvider {
    private final ColorManager colorManager;
    private Color color;
    private Point lastPoint;

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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

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

        JCheckBox antialiasCheckbox = new JCheckBox("Antialiasing");
        antialiasCheckbox.setSelected(antialiased);
        antialiasCheckbox.addItemListener(e -> {
            antialiased = antialiasCheckbox.isSelected();
        });

        panel.add(antialiasCheckbox);

        return panel;
    }

    private void draw(Canvas canvas, Point from, Point to) {
        Graphics2D g2d = canvas.getTempGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                this.antialiased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
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