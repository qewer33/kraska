package org.example.app.tool;

import org.example.app.Util;
import org.example.app.color.ColorManager;
import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;
import org.example.gui.canvas.selection.Selection;
import org.example.gui.canvas.selection.SelectionManager;
import org.example.gui.screen.component.ToolOptionsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Objects;

public class ShapeTool extends AbstractTool implements CanvasPainter, ToolOptionsProvider {
    public enum ShapeType {
        LINE,
        RECTANGLE,
        ELLIPSE,
        TRIANGLE,
        HEXAGON,
        LIGHTNING,
        STAR,
        HEART,
        TURKISH_FLAG,
        GAZI_LOGO,
        JAVA_LOGO
    }

    private ShapeType shapeType = ShapeType.LINE;
    private boolean filled = false;
    private int thickness = 5;
    private Point startPoint;
    private Point lastPoint;
    private Rectangle shapeBounds;

    private final ColorManager colorManager = ColorManager.getInstance();
    private final SelectionManager selectionManager = SelectionManager.getInstance();

    public ShapeTool() {
        super("Shape");
    }

    @Override
    public void onActivate() {
        selectionManager.restrictToolInput = true;
    }

    @Override
    public void onMousePress(Canvas canvas, MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
            startPoint = canvas.getUnzoomedPoint(e.getPoint());
            canvas.clearTempBuffer();
            shapeBounds = new Rectangle(0, 0, 0, 0);
        }
    }

    @Override
    public void onMouseDrag(Canvas canvas, MouseEvent e) {
        if (startPoint != null) {
            Point current = canvas.getUnzoomedPoint(e.getPoint());
            lastPoint = current;
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
                    int x = Math.min(startPoint.x, current.x);
                    int y = Math.min(startPoint.y, current.y);
                    int w = Math.abs(startPoint.x - current.x);
                    int h = Math.abs(startPoint.y - current.y);
                    shapeBounds.setBounds(x, y, w, h);
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
                    }
                    g2d.drawRect(x, y, w, h);
                    shapeBounds.setBounds(x, y, w, h);
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
                    }
                    g2d.drawOval(x, y, w, h);
                    shapeBounds.setBounds(x, y, w, h);
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
                    }
                    g2d.drawPolygon(xPoints, yPoints, 3);

                    int x = Math.min(x1, x2);
                    int y = Math.min(y1, y2);
                    int w = Math.abs(x2 - x1);
                    int h = Math.abs(y2 - y1);
                    shapeBounds.setBounds(x, y, w, h);
                }
                case HEXAGON -> drawHexagon(g2d, startPoint, current);
                case LIGHTNING -> drawLightning(g2d, startPoint, current);
                case STAR -> drawStar(g2d, startPoint, current);
                case HEART -> drawHeart(g2d, startPoint, current);
                case TURKISH_FLAG -> {
                    Image img = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/image/turkish_flag.png"))).getImage();
                    drawImageShape(g2d, startPoint, current, img, shiftDown);
                }
                case GAZI_LOGO -> {
                    Image img = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/image/gazi_logo.png"))).getImage();
                    drawImageShape(g2d, startPoint, current, img, shiftDown);
                }
                case JAVA_LOGO -> {
                    Image img = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/image/java_logo.png"))).getImage();
                    drawImageShape(g2d, startPoint, current, img, shiftDown);
                }
            }

            g2d.dispose();
            canvas.repaint();
        }
    }

    @Override
    public void onMouseRelease(Canvas canvas, MouseEvent e) {
        if (lastPoint == null) return;
        if (startPoint != null) {
            int x = shapeBounds.x;
            int y = shapeBounds.y;
            int w = shapeBounds.width;
            int h = shapeBounds.height;

            int pad = thickness;

            Selection sel = new Selection(new Rectangle(x-pad, y-pad, w+pad*2, h+pad*2));

            BufferedImage tempBuf = canvas.getTempBuffer();
            BufferedImage copy = new BufferedImage(w+pad*2, h+pad*2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = copy.createGraphics();
            g.drawImage(tempBuf, 0, 0, w+pad*2, h+pad*2, x-pad, y-pad, x + w + pad, y + h + pad, null);
            g.dispose();

            sel.setContent(copy);
            selectionManager.setSelection(sel);

            canvas.clearTempBuffer();
        }
        startPoint = null;
        lastPoint = null;
    }

    @Override
    public JPanel getToolOptionsPanel() {
        ToolOptionsPanel panel = new ToolOptionsPanel();

        // Label
        JLabel shapeLabel = new JLabel("Shape: " + Util.getDisplayName(shapeType.name()));

        // 4x2 Toggle button grid
        JPanel buttonGrid = new JPanel(new GridLayout(3, 4, 5, 5));
        ButtonGroup shapeGroup = new ButtonGroup();

        HashMap<ShapeType, ImageIcon> shapeIcons = new HashMap<>();
        shapeIcons.put(ShapeType.LINE, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/shape_line.png"))));
        shapeIcons.put(ShapeType.RECTANGLE, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/shape_rectangle.png"))));
        shapeIcons.put(ShapeType.ELLIPSE, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/shape_ellipse.png"))));
        shapeIcons.put(ShapeType.TRIANGLE, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/shape_triangle.png"))));
        shapeIcons.put(ShapeType.HEXAGON, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/shape_hexagon.png"))));
        shapeIcons.put(ShapeType.LIGHTNING, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/shape_lightning.png"))));
        shapeIcons.put(ShapeType.STAR, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/shape_star.png"))));
        shapeIcons.put(ShapeType.HEART, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/shape_heart.png"))));
        shapeIcons.put(ShapeType.TURKISH_FLAG, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/turkish_flag.png"))));
        shapeIcons.put(ShapeType.GAZI_LOGO, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/gazi_logo.png"))));
        shapeIcons.put(ShapeType.JAVA_LOGO, new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/shape/java_logo.png"))));

        for (ShapeType type : ShapeType.values()) {
            JToggleButton btn = new JToggleButton(shapeIcons.get(type));
            btn.addActionListener(e -> {
                shapeType = type;
                shapeLabel.setText("Shape: " + Util.getDisplayName(shapeType.name()));
            });
            btn.setFocusable(false);
            shapeGroup.add(btn);
            buttonGrid.add(btn);

            // Pre-select current shape
            if (type == shapeType) {
                btn.setSelected(true);
            }
        }

        // Fill checkbox
        JCheckBox fillBox = new JCheckBox("Fill shape");
        fillBox.setSelected(filled);
        fillBox.addItemListener(e -> filled = fillBox.isSelected());
        fillBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Thickness slider
        JLabel thicknessLabel = new JLabel("Thickness: " + thickness + "px");
        thicknessLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSlider thicknessSlider = new JSlider(1, 100, thickness);
        thicknessSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        thicknessSlider.setMaximumSize(new Dimension(200, 40));
        thicknessSlider.addChangeListener(e -> {
            thickness = thicknessSlider.getValue();
            thicknessLabel.setText("Thickness: " + thickness + "px");
        });

        panel.addComponentGroup(new JComponent[]{
                shapeLabel,
                buttonGrid,
        }, 10);
        panel.addComponentGroup(new JComponent[]{
                thicknessLabel,
                thicknessSlider,
        });
        panel.addComponent(fillBox);

        return panel;
    }

    private void drawHexagon(Graphics2D g2d, Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int w = Math.abs(end.x - start.x);
        int h = Math.abs(end.y - start.y);
        int size = Math.min(w, h); // Make it regular (equal sides)

        double centerX = x + size / 2.0;
        double centerY = y + size / 2.0;
        double radius = size / 2.0;

        Path2D path = new Path2D.Double();

        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30); // Start flat top
            double px = centerX + Math.cos(angle) * radius;
            double py = centerY + Math.sin(angle) * radius;

            if (i == 0) {
                path.moveTo(px, py);
            } else {
                path.lineTo(px, py);
            }
        }
        path.closePath();
        drawFillPath(g2d, path);
        shapeBounds.setBounds(x, y, size, size);
    }

    private void drawLightning(Graphics2D g2d, Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int w = Math.abs(end.x - start.x);
        int h = Math.abs(end.y - start.y);

        Path2D path = new Path2D.Double();
        path.moveTo(x + w * 0.4, y);
        path.lineTo(x + w * 0.6, y);
        path.lineTo(x + w * 0.5, y + h * 0.4);
        path.lineTo(x + w * 0.7, y + h * 0.4);
        path.lineTo(x + w * 0.3, y + h);
        path.lineTo(x + w * 0.5, y + h * 0.5);
        path.lineTo(x + w * 0.3, y + h * 0.5);
        path.closePath();
        drawFillPath(g2d, path);
        shapeBounds.setBounds(x, y, w, h);
    }

    private void drawStar(Graphics2D g2d, Point start, Point end) {
        int x = start.x;
        int y = start.y;
        int w = Math.abs(end.x - start.x);
        int h = Math.abs(end.y - start.y);
        int size = Math.min(w, h); // make it square for symmetry

        int xd = (x < end.x) ? 1 : -1;
        int yd = (y < end.y) ? 1 : -1;
        double centerX = x + size*xd / 2.0;
        double centerY = y + size*yd / 2.0;
        double radiusOuter = size / 2.0;
        double radiusInner = radiusOuter * 0.5;

        Path2D path = new Path2D.Double();

        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 5 * i;
            double r = (i % 2 == 0) ? radiusOuter : radiusInner;
            double px = centerX + Math.cos(angle - Math.PI/2) * r;
            double py = centerY + Math.sin(angle - Math.PI/2) * r;

            if (i == 0) {
                path.moveTo(px, py);
            } else {
                path.lineTo(px, py);
            }
        }
        path.closePath();
        drawFillPath(g2d, path);
        shapeBounds.setBounds((int) centerX-size/2, (int) centerY-size/2, size, size);
    }

    private void drawHeart(Graphics2D g2d, Point start, Point end) {
        int x = start.x;
        int y = start.y;
        int w = Math.abs(end.x - start.x);
        int h = Math.abs(end.y - start.y);
        int size = Math.min(w, h);

        int xd = (x < end.x) ? 1 : -1;
        int yd = (y < end.y) ? 1 : -1;
        double cx = x + size*xd / 2.0;
        double cy = y + size*yd / 2.0;
        double r = size / 4.0;

        Path2D path = new Path2D.Double();

        // Left circle
        path.moveTo(cx, cy + r / 2);
        path.curveTo(cx - r * 2, cy - r * 1.5,
                cx - r, cy - r * 2.5,
                cx, cy - r);

        // Right circle
        path.curveTo(cx + r, cy - r * 2.5,
                cx + r * 2, cy - r * 1.5,
                cx, cy + r / 2);

        path.closePath();
        drawFillPath(g2d, path);
        shapeBounds.setBounds((int) cx-size/2, (int) cy-size/2, size, (int) (size*0.75));
    }

    private void drawFillPath(Graphics2D g2d, Path2D path) {
        if (filled) {
            g2d.setColor(colorManager.getSecondary());
            g2d.fill(path);
            g2d.setColor(colorManager.getPrimary());
        }
        g2d.draw(path);
    }

    private void drawImageShape(Graphics2D g2d, Point start, Point end, Image image, boolean shiftDown) {
        if (image == null) return;

        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int w = Math.abs(end.x - start.x);
        int h = Math.abs(end.y - start.y);

        if (shiftDown) {
            // Keep aspect ratio of image
            double imgAspect = (double) image.getWidth(null) / image.getHeight(null);
            double boxAspect = (double) w / h;

            if (imgAspect > boxAspect) {
                // Image is wider than box — adjust height
                h = (int) (w / imgAspect);
            } else {
                // Image is taller than box — adjust width
                w = (int) (h * imgAspect);
            }
        }

        g2d.drawImage(image, x, y, w, h, null);
        shapeBounds.setBounds(x, y, w, h);
    }
}

