package org.example.app.tool;

import org.example.app.Util;
import org.example.app.color.ColorManager;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;

import org.example.gui.screen.component.ToolOptionsPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class BrushTool extends AbstractTool implements CanvasPainter, ToolOptionsProvider {
    public enum BrushShape {
        BASIC,
        MARKER,
        CALLIGRAPHY,
        PENCIL,
        BRISTLES,
        CHALK,
        WATERBRUSH,
        ROLLER
    }

    private final ColorManager colorManager;
    private Color color;
    private Point lastPoint;

    private BrushShape brushShape = BrushShape.BASIC;
    private int size;
    private float force = 1.0f;
    private float hardness = 1.0f;
    private boolean antialiased;

    private BrushResourceProvider brushResourceProvider = new BrushResourceProvider();

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

        JLabel brushTypeLabel = new JLabel("Brush: " + Util.getDisplayName(brushShape.name()));
        JPanel brushShapePanel = new JPanel(new GridLayout(2, 4, 5, 5));
        ButtonGroup brushShapeGroup = new ButtonGroup();

        HashMap<BrushShape, ImageIcon> brushIcons = new HashMap<>();
        brushIcons.put(BrushShape.BASIC, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_basic.jpg"))));
        brushIcons.put(BrushShape.MARKER, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_marker.jpg"))));
        brushIcons.put(BrushShape.CALLIGRAPHY, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_calligraphy.jpg"))));
        brushIcons.put(BrushShape.PENCIL, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_pencil.jpg"))));
        brushIcons.put(BrushShape.BRISTLES, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_bristles.jpg"))));
        brushIcons.put(BrushShape.CHALK, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_chalk.jpg"))));
        brushIcons.put(BrushShape.WATERBRUSH, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_waterbrush.jpg"))));
        brushIcons.put(BrushShape.ROLLER, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/brush/brush_roller.jpg"))));

        for (BrushShape shape : BrushShape.values()) {
            JToggleButton button = new JToggleButton(brushIcons.get(shape));
            button.addActionListener(e -> {
                brushShape = shape;
                brushTypeLabel.setText("Brush: " + Util.getDisplayName(brushShape.name()));
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

        // Spacing slider
        JLabel hardnessLabel = new JLabel("Hardness: " + (int)(hardness * 100) + "%");
        JSlider hardnessSlider = new JSlider(30, 250, (int)(hardness * 100));
        hardnessSlider.addChangeListener(e -> {
            hardness = hardnessSlider.getValue() / 100f;
            hardnessLabel.setText("Hardness: " + hardnessSlider.getValue() + "%");
        });

        JCheckBox antialiasCheckbox = new JCheckBox("Antialiasing");
        antialiasCheckbox.setSelected(antialiased);
        antialiasCheckbox.addItemListener(e -> {
            antialiased = antialiasCheckbox.isSelected();
        });

        panel.addComponentGroup(new JComponent[]{brushTypeLabel, brushShapePanel});
        panel.addComponentGroup(new JComponent[]{sizeLabel, sizeSlider});
        panel.addComponentGroup(new JComponent[]{forceLabel, forceSlider});
        panel.addComponentGroup(new JComponent[]{hardnessLabel, hardnessSlider});
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

        double distance = from.distance(to);
        int steps = (int) (distance * this.hardness);
        if (steps < 1) steps = 1;

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            int x = (int) (from.x + (to.x - from.x) * t);
            int y = (int) (from.y + (to.y - from.y) * t);

            double angle = Math.atan2(to.y - from.y, to.x - from.x); // direction

            drawBrushShape(g2d, x, y, angle);
        }

        g2d.dispose();
        canvas.repaint();
    }

    private void drawBrushShape(Graphics2D g2d, int x, int y, double angle) {
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
            case BRISTLES -> {
                drawStamp(g2d, brushResourceProvider.getBrushTexture("texture_bristles"), x, y, angle, 0.4f, size, color); // example values
            }
            case CHALK -> {
                drawStamp(g2d, brushResourceProvider.getBrushTexture("texture_chalk"), x, y, angle, 0.025f, size, color); // example values
            }
            case WATERBRUSH -> {
                drawStamp(g2d, brushResourceProvider.getBrushTexture("texture_waterbrush"), x, y, angle, 0.1f, size, color); // example values
            }
            case ROLLER -> {
                drawStamp(g2d, brushResourceProvider.getBrushTexture("texture_roller"), x, y, angle, 0.1f, size, color); // example values
            }
        }
    }

    private void drawStamp(Graphics2D g2d, BufferedImage stamp, int x, int y, double angle, float opacity, int size, Color color) {
        Graphics2D g = (Graphics2D) g2d.create();

        g.translate(x, y);
        g.rotate(angle);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        // Step 1: Create a tinted image using color + DST_IN mask from the stamp
        BufferedImage tinted = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tg = tinted.createGraphics();

        // Fill with selected color
        tg.setColor(color);
        tg.fillRect(0, 0, size, size);

        // Mask the brush texture (assumes it's grayscale)
        tg.setComposite(AlphaComposite.DstIn);
        tg.drawImage(stamp, 0, 0, size, size, null);

        tg.dispose();

        // Step 2: Draw the tinted image
        g.drawImage(tinted, -size / 2, -size / 2, null);

        g.dispose();
    }

    private static class BrushResourceProvider {
        private HashMap<String, BufferedImage> brushTextures = new HashMap<>();

        public BrushResourceProvider() {
            loadBrushTextures();
        }

        public BufferedImage getBrushTexture(String name) {
            return brushTextures.get(name);
        }

        public void loadBrushTextures() {
            brushTextures.clear();
            for (String name : new String[] {
                    "texture_bristles",
                    "texture_chalk",
                    "texture_waterbrush",
                    "texture_roller",
            }) {
                if (!brushTextures.containsKey(name)) {
                    try {
                        BufferedImage texture = ImageIO.read(Objects.requireNonNull(getClass().getResource("/icons/brush/texture/" + name + ".png")));
                        brushTextures.put(name, texture);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}