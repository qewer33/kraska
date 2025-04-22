package org.example.gui.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CanvasViewer extends JScrollPane {
    private final Canvas canvas;
    private double zoomFactor = 1.0;
    private Point panStartPoint;

    public CanvasViewer(Canvas canvas) {
        super(canvas);
        this.canvas = canvas;
        setupViewer();
    }

    private void setupViewer() {
        // Basic setup
        setPreferredSize(new Dimension(800, 600));
        getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        // Enable mouse wheel for future zooming
        addMouseWheelListener(this::handleMouseWheel);

        // Setup panning listeners (for future implementation)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) { // Middle mouse button
                    panStartPoint = e.getPoint();
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    panStartPoint = null;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (panStartPoint != null) {
                    handlePanning(e.getPoint());
                }
            }
        });
    }

    // Placeholder for zoom functionality
    private void handleMouseWheel(MouseWheelEvent e) {
        // Will implement zooming later
        // For now, just scroll vertically
        JScrollBar vertical = getVerticalScrollBar();
        vertical.setValue(vertical.getValue() + e.getWheelRotation() * vertical.getUnitIncrement());
    }

    // Placeholder for panning functionality
    private void handlePanning(Point currentPoint) {
        // Will implement proper panning later
        JViewport viewport = getViewport();
        Point viewPosition = viewport.getViewPosition();
        int dx = panStartPoint.x - currentPoint.x;
        int dy = panStartPoint.y - currentPoint.y;

        viewport.setViewPosition(new Point(
                viewPosition.x + dx,
                viewPosition.y + dy
        ));

        panStartPoint = currentPoint;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    // Will be used for zoom implementation later
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
        // Future zoom implementation will go here
    }
}
