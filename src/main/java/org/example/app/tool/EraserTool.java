package org.example.app.tool;

import org.example.app.Util;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;
import org.example.gui.screen.component.ToolOptionsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Objects;

public class EraserTool extends AbstractTool implements CanvasPainter, ToolOptionsProvider {
    private enum EraserShape {
        BASIC,
        SHARP,
        SOFT
    }

    private Point lastPoint;

    private EraserShape eraserShape = EraserShape.BASIC;
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

        JLabel eraserTypeLabel = new JLabel("Eraser: " + Util.getDisplayName(eraserShape.name()));
        JPanel brushShapePanel = new JPanel(new GridLayout(1, 4, 5, 5));
        ButtonGroup brushShapeGroup = new ButtonGroup();

        HashMap<EraserShape, ImageIcon> eraserIcons = new HashMap<>();
        eraserIcons.put(EraserShape.BASIC, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/eraser/eraser_basic.png"))));
        eraserIcons.put(EraserShape.SHARP, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/eraser/eraser_sharp.png"))));
        eraserIcons.put(EraserShape.SOFT, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/eraser/eraser_soft.png"))));

        for (EraserShape shape : EraserShape.values()) {
            JToggleButton button = new JToggleButton(eraserIcons.get(shape));
            button.addActionListener(e -> {
                eraserShape = shape;
                eraserTypeLabel.setText("Eraser: " + Util.getDisplayName(eraserShape.name()));
            });
            brushShapeGroup.add(button);
            brushShapePanel.add(button);

            // Select default active button
            if (shape == eraserShape) {
                button.setSelected(true);
            }
        }

        // Hacky layout fix to make icons square
        JToggleButton spacerButton = new JToggleButton();
        spacerButton.setVisible(false);
        brushShapePanel.add(spacerButton);

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

        panel.addComponentGroup(new JComponent[]{eraserTypeLabel, brushShapePanel});
        panel.addComponentGroup(new JComponent[]{sizeLabel, sizeSlider});
        panel.addComponentGroup(new JComponent[]{forceLabel, forceSlider});
        panel.addComponent(antialiasCheckbox);

        return panel;
    }

    private void erase(Canvas canvas, Point from, Point to) {
        BufferedImage canvasImage = canvas.getCanvasImage();
        Graphics2D g2d = canvasImage.createGraphics();

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, force));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

        int steps = (int) from.distance(to) * 2;
        if (steps == 0) steps = 1;

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            int x = (int) (from.x + (to.x - from.x) * t);
            int y = (int) (from.y + (to.y - from.y) * t);

            drawEraserStamp(g2d, x, y);
        }

        g2d.dispose();
        canvas.repaint();
    }

    private void drawEraserStamp(Graphics2D g2d, int x, int y) {
        switch (eraserShape) {
            case BASIC -> g2d.fillOval(x - size / 2, y - size / 2, size, size);
            case SHARP -> {
                int width = size;
                int height = Math.max(size / 6, 1);

                AffineTransform old = g2d.getTransform();
                g2d.translate(x, y);
                g2d.rotate(Math.toRadians(45));
                g2d.fillRect(-width / 2, -height / 2, width, height);
                g2d.setTransform(old);
            }
            case SOFT -> {
                RadialGradientPaint gradient = new RadialGradientPaint(
                        new Point(x, y),
                        size / 2f,
                        new float[] {0f, 0.15f, 0.2f, 1f},
                        new Color[] {
                                new Color(0, 0, 0, (int) (force*100)),
                                new Color(0, 0, 0, (int) (force*80)),
                                new Color(0, 0, 0, (int) (force*10)),
                                new Color(0, 0, 0, 0)
                        }
                );

                Paint oldPaint = g2d.getPaint();
                g2d.setPaint(gradient);
                g2d.fillOval(x - size / 2, y - size / 2, size, size);
                g2d.setPaint(oldPaint);
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
