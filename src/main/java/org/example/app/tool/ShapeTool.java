package org.example.app.tool;

import org.example.app.color.ColorManager;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ShapeTool extends AbstractTool implements CanvasPainter, ToolOptionsProvider {
    public enum ShapeType {
        LINE,
        RECTANGLE,
        ELLIPSE,
        TRIANGLE
    }

    private ShapeType shapeType = ShapeType.LINE;
    private boolean filled = false;
    private int thickness = 2;
    private Point startPoint;
    private final ColorManager colorManager = ColorManager.getInstance();

    public ShapeTool() {
        super("Shape");
    }

    @Override
    public void onMousePress(Canvas canvas, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
            startPoint = canvas.getUnzoomedPoint(e.getPoint());
            canvas.clearTempBuffer();
        }
    }

    @Override
    public void onMouseDrag(Canvas canvas, MouseEvent e) {
        if (startPoint != null) {
            Point current = canvas.getUnzoomedPoint(e.getPoint());
            boolean shiftDown = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;

            canvas.clearTempBuffer();

            Graphics2D g2d = canvas.getTempGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(colorManager.getPrimary());
            g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (shapeType) {
                case LINE -> {
                    if (shiftDown) {
                        int dx = current.x - startPoint.x;
                        int dy = current.y - startPoint.y;
                        double angle = Math.atan2(dy, dx);
                        double snapAngle = Math.PI / 4;
                        angle = Math.round(angle / snapAngle) * snapAngle;
                        double length = Math.hypot(dx, dy);
                        current.x = startPoint.x + (int) (Math.cos(angle) * length);
                        current.y = startPoint.y + (int) (Math.sin(angle) * length);
                    }
                    g2d.drawLine(startPoint.x, startPoint.y, current.x, current.y);
                }
                case RECTANGLE -> {
                    int x = Math.min(startPoint.x, current.x);
                    int y = Math.min(startPoint.y, current.y);
                    int w = Math.abs(startPoint.x - current.x);
                    int h = Math.abs(startPoint.y - current.y);

                    if (shiftDown) {
                        int size = Math.max(w, h);
                        w = h = size;
                    }

                    if (filled) {
                        g2d.setColor(colorManager.getSecondary());
                        g2d.fillRect(x, y, w, h);
                        g2d.setColor(colorManager.getPrimary());
                        g2d.drawRect(x, y, w, h);
                    } else g2d.drawRect(x, y, w, h);
                }
                case ELLIPSE -> {
                    int x = Math.min(startPoint.x, current.x);
                    int y = Math.min(startPoint.y, current.y);
                    int w = Math.abs(startPoint.x - current.x);
                    int h = Math.abs(startPoint.y - current.y);

                    if (shiftDown) {
                        int size = Math.max(w, h);
                        w = h = size;
                    }

                    if (filled) {
                        g2d.setColor(colorManager.getSecondary());
                        g2d.fillOval(x, y, w, h);
                        g2d.setColor(colorManager.getPrimary());
                        g2d.drawOval(x, y, w, h);
                    } else g2d.drawOval(x, y, w, h);
                }
                case TRIANGLE -> {
                    if (shiftDown) {
                        int side = Math.max(Math.abs(current.x - startPoint.x), Math.abs(current.y - startPoint.y));
                        current.x = startPoint.x + side;
                        current.y = startPoint.y + side;
                    }

                    int x1 = startPoint.x;
                    int y1 = startPoint.y;
                    int x2 = current.x;
                    int y2 = current.y;
                    int midX = (x1 + x2) / 2;

                    int[] xPoints = { x1, x2, midX };
                    int[] yPoints = { y2, y2, y1 };

                    if (filled) {
                        g2d.setColor(colorManager.getSecondary());
                        g2d.fillPolygon(xPoints, yPoints, 3);
                        g2d.setColor(colorManager.getPrimary());
                        g2d.drawPolygon(xPoints, yPoints, 3);
                    } else {
                        g2d.drawPolygon(xPoints, yPoints, 3);
                    }
                }
            }

            g2d.dispose();
            canvas.repaint();
        }
    }


    @Override
    public void onMouseRelease(Canvas canvas, MouseEvent e) {
        if (startPoint != null) {
            canvas.applyTempBuffer(1.0f); // full opacity for shapes (or you can add force later if you want)
            startPoint = null;
        }
    }

    public JPanel getToolOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JComboBox<ShapeType> shapeSelect = new JComboBox<>(ShapeType.values());
        shapeSelect.setMaximumSize(new Dimension(200, 30));
        shapeSelect.setSelectedItem(shapeType);
        shapeSelect.addActionListener(e -> shapeType = (ShapeType) shapeSelect.getSelectedItem());

        JCheckBox fillBox = new JCheckBox("Filled");
        fillBox.setSelected(filled);
        fillBox.addItemListener(e -> filled = fillBox.isSelected());

        JLabel thicknessLabel = new JLabel("Thickness: " + thickness + "px");
        JSlider thicknessSlider = new JSlider(1, 20, thickness);
        thicknessSlider.addChangeListener(e -> {
            thickness = thicknessSlider.getValue();
            thicknessLabel.setText("Thickness: " + thickness + "px");
        });

        panel.add(new JLabel("Shape:"));
        panel.add(shapeSelect);
        panel.add(fillBox);
        panel.add(thicknessLabel);
        panel.add(thicknessSlider);

        return panel;
    }
}

