package org.example.gui.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CanvasViewer extends JScrollPane {
    private final Canvas canvas;
    private double zoomFactor = 1.0;
    private Point panStartPoint;
    private Point viewStartPoint;

    public CanvasViewer(Canvas canvas) {
        super();
        this.canvas = canvas;

        // Create wrapper panel to center the canvas and add border
        CheckerboardPanel backgroundPanel = new CheckerboardPanel();
        this.canvas.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        backgroundPanel.add(canvas);
        setViewportView(backgroundPanel);
        setWheelScrollingEnabled(false); // Disable wheel scroll since it's used for zoom
        setupViewer();
    }

    private void setupViewer() {
        setPreferredSize(new Dimension(800, 600));
        getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        // Still add wheel listener to viewer
        addMouseWheelListener(this::handleMouseWheel);

        // 🔁 Middle mouse panning — now added to the canvas
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    panStartPoint = e.getPoint();
                    viewStartPoint = getViewport().getViewPosition();
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    e.consume(); // prevent paint tools from triggering
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    panStartPoint = null;
                    viewStartPoint = null;
                    canvas.setCursor(Cursor.getDefaultCursor());
                    e.consume();
                }
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (panStartPoint != null && viewStartPoint != null) {
                    handlePanning(e.getPoint());
                    e.consume();
                }
            }
        });
    }

    private void handleMouseWheel(MouseWheelEvent e) {
        if (e.isControlDown()) {
            double delta = -e.getPreciseWheelRotation() * 0.1;
            double newZoom = Math.max(0.1, zoomFactor + delta);
            setZoomFactor(newZoom);
        } else {
            JScrollBar vertical = getVerticalScrollBar();
            vertical.setValue(vertical.getValue() + e.getWheelRotation() * vertical.getUnitIncrement());
        }
    }

    private void handlePanning(Point currentMouse) {
        int dx = panStartPoint.x - currentMouse.x;
        int dy = panStartPoint.y - currentMouse.y;

        int newX = viewStartPoint.x + dx;
        int newY = viewStartPoint.y + dy;

        // Clamp to bounds
        JViewport viewport = getViewport();
        Dimension viewSize = viewport.getViewSize();
        Dimension extentSize = viewport.getExtentSize();

        newX = Math.max(0, Math.min(viewSize.width - extentSize.width, newX));
        newY = Math.max(0, Math.min(viewSize.height - extentSize.height, newY));

        viewport.setViewPosition(new Point(newX, newY));
    }

    public void setZoomFactor(double zoomFactor) {
        double oldValue = this.zoomFactor;
        this.zoomFactor = zoomFactor;
        canvas.setZoomFactor(zoomFactor);
        canvas.revalidate();
        canvas.repaint();
        this.firePropertyChange("zoomFactor", oldValue, zoomFactor);
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    private static class CheckerboardPanel extends JPanel {
        private static final int TILE_SIZE = 16;
        private static final Color COLOR1 = new Color(200, 200, 200);
        private static final Color COLOR2 = new Color(240, 240, 240);

        public CheckerboardPanel() {
            super(new GridBagLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Full clean background
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g.create();

            // Checkerboard fill
            for (int y = 0; y < getHeight(); y += TILE_SIZE) {
                for (int x = 0; x < getWidth(); x += TILE_SIZE) {
                    g2d.setColor(((x / TILE_SIZE + y / TILE_SIZE) % 2 == 0) ? COLOR1 : COLOR2);
                    g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                }
            }
            g2d.dispose();
        }
    }
}
