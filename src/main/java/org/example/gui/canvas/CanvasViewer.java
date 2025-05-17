package org.example.gui.canvas;

import org.example.gui.canvas.selection.SelectionManager;
import org.example.gui.canvas.selection.SelectionView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 * CanvasViewer is a JScrollPane that wraps a Canvas and provides a zooming and panning functionality.
 * It also displays a checkerboard pattern as the background and handles painting of overlays (e.g. selection).
 */
public class CanvasViewer extends JScrollPane {
    private final Canvas canvas;
    private double zoomFactor = 1.0;
    private Point panStartPoint;

    private final SelectionManager selectionManager = SelectionManager.getInstance();

    private final JLayeredPane layeredPane;
    private final OverlayPanel overlayPanel;

    public CanvasViewer(Canvas canvas) {
        super();
        this.canvas = canvas;
        this.canvas.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));

        layeredPane = new JLayeredPane();
        layeredPane.add(this.canvas, JLayeredPane.DEFAULT_LAYER );

        overlayPanel = new OverlayPanel();
        overlayPanel.setOpaque(false);
        layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);

        // Create wrapper panel to center the canvas and add border
        CheckerboardPanel backgroundPanel = new CheckerboardPanel();
        backgroundPanel.add(layeredPane);

        setViewportView(backgroundPanel);
        setWheelScrollingEnabled(false); // Disable wheel scroll since it's used for zoom
        setupViewer();
    }

    private void setupViewer() {
        setPreferredSize(new Dimension(800, 600));
        getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLayerBounds();
            }
        });

        // Still add wheel listener to viewer
        addMouseWheelListener(this::handleMouseWheel);

        // Middle mouse panning
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    // Convert to viewport coordinates
                    panStartPoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), getViewport());
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    e.consume();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    panStartPoint = null;
                    canvas.setCursor(Cursor.getDefaultCursor());
                    e.consume();
                }
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (panStartPoint != null) {
                    Point current = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), getViewport());
                    handlePanning(current);
                    panStartPoint = current; // <- Key to eliminate jitter
                    e.consume();
                }
            }
        });
    }

    public void updateLayerBounds() {
        Dimension size = canvas.getPreferredSize();
        canvas.setBounds(0, 0, size.width, size.height);
        overlayPanel.setBounds(0, 0, size.width, size.height);
        layeredPane.setPreferredSize(size);
        layeredPane.revalidate();
        layeredPane.repaint();
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
        JViewport viewport = getViewport();

        int dx = panStartPoint.x - currentMouse.x;
        int dy = panStartPoint.y - currentMouse.y;

        Point viewPos = viewport.getViewPosition();

        int newX = Math.max(0, Math.min(viewPos.x + dx, viewport.getViewSize().width - viewport.getExtentSize().width));
        int newY = Math.max(0, Math.min(viewPos.y + dy, viewport.getViewSize().height - viewport.getExtentSize().height));

        viewport.setViewPosition(new Point(newX, newY));
    }

    public void setZoomFactor(double zoomFactor) {
        double oldValue = this.zoomFactor;
        this.zoomFactor = zoomFactor;
        canvas.setZoomFactor(zoomFactor);
        canvas.revalidate();
        canvas.repaint();

        Dimension newSize = canvas.getPreferredSize();
        layeredPane.setPreferredSize(newSize);
        canvas.setBounds(0, 0, newSize.width, newSize.height);
        overlayPanel.setBounds(0, 0, newSize.width, newSize.height);
        layeredPane.revalidate();
        layeredPane.repaint();
        overlayPanel.repaint();

        this.firePropertyChange("zoomFactor", oldValue, zoomFactor);
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JPanel getOverlayPanel() {
        return overlayPanel;
    }

    // OverlayPanel handles painting of on canvas overlays (e.g. selection)
    private class OverlayPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Paint tool overlays
            /*
            AbstractTool activeTool = ToolManager.getInstance().getActiveTool();
            if (activeTool instanceof OverlayPainter painter) {
                painter.paintOverlay((Graphics2D) g.create(), canvas.getZoomFactor());
            }
             */

            // Paint selection overlay
            SelectionView view = selectionManager.getView();
            if (view.isActive()) view.paintOverlay((Graphics2D) g.create(), canvas.getZoomFactor());
        }
    }

    // CheckerboardPanel is a simple JPanel that displays a checkerboard pattern for the background
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
