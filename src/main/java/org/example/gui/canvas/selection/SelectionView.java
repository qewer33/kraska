package org.example.gui.canvas.selection;

import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasPainter;
import org.example.gui.canvas.OverlayPainter;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/*
 * SelectionView manages the selection view and state.
 * It also handles the painting of the selection overlay.
 */
public class SelectionView implements CanvasPainter, OverlayPainter {
    private enum State {
        IDLE, CREATING, MOVING, SCALING, ROTATING
    }
    private enum ScaleHandle {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    private static final int HANDLE_SIZE = 12;
    private static final int ROTATION_HANDLE_OFFSET = 30;
    private static final int ROTATION_HANDLE_RADIUS = 8;

    private boolean isActive = false;
    private State currentState = State.IDLE;
    private boolean copy = false;

    private Selection currentSelection = null;
    private boolean tempPaintOverlay = false;
    private double rotationAngle = 0;

    private Point startPoint = null;
    private Point currentPoint = null;
    private Point moveStartPoint = null;
    private ScaleHandle activeHandle = null;

    @Override
    public void onMousePress(org.example.gui.canvas.Canvas canvas, MouseEvent e) {
        Point clicked = canvas.getUnzoomedPoint(e.getPoint());

        tempPaintOverlay = false;

        if (currentSelection != null && isOnRotationHandle(clicked, canvas.getZoomFactor())) {
            currentState = State.ROTATING;
            moveStartPoint = clicked;
        } else if ((activeHandle = getHandleAt(clicked)) != null) {
            currentState = State.SCALING;
        } else if (currentSelection != null && isInsideSelection(clicked)) {
            currentState = State.MOVING;
            moveStartPoint = clicked;
        } else {
            if (isActive) isActive = false;
            startPoint = clicked;
            currentPoint = clicked;
            currentState = State.CREATING;
            applySelection(canvas);
            rotationAngle = 0;
            currentSelection = null;
            tempPaintOverlay = true;
        }

        canvas.repaint();
    }

    @Override
    public void onMouseDrag(org.example.gui.canvas.Canvas canvas, MouseEvent e) {
        Point current = canvas.getUnzoomedPoint(e.getPoint());

        switch (currentState) {
            case ROTATING -> {
                Rectangle bounds = currentSelection.getBounds();
                int centerX = bounds.x + bounds.width / 2;
                int centerY = bounds.y + bounds.height / 2;

                double angle1 = Math.atan2(moveStartPoint.y - centerY, moveStartPoint.x - centerX);
                double angle2 = Math.atan2(current.y - centerY, current.x - centerX);

                rotationAngle += (angle2 - angle1);
                moveStartPoint = current;
            }
            case SCALING -> {
                Rectangle bounds = currentSelection.getBounds();
                Point rotated = rotatePoint(canvas.getUnzoomedPoint(e.getPoint()).x, canvas.getUnzoomedPoint(e.getPoint()).y,
                        bounds.x + bounds.width/2, bounds.y + bounds.height/2, -rotationAngle);

                int newX = bounds.x;
                int newY = bounds.y;
                int newW = bounds.width;
                int newH = bounds.height;

                switch (activeHandle) {
                    case TOP_LEFT -> {
                        int diffX = rotated.x - bounds.x;
                        int diffY = rotated.y - bounds.y;
                        newX += diffX;
                        newY += diffY;
                        newW -= diffX;
                        newH -= diffY;
                    }
                    case TOP_RIGHT -> {
                        int diffX = rotated.x - (bounds.x + bounds.width);
                        int diffY = rotated.y - bounds.y;
                        newY += diffY;
                        newW += diffX;
                        newH -= diffY;
                    }
                    case BOTTOM_LEFT -> {
                        int diffX = rotated.x - bounds.x;
                        int diffY = rotated.y - (bounds.y + bounds.height);
                        newX += diffX;
                        newW -= diffX;
                        newH += diffY;
                    }
                    case BOTTOM_RIGHT -> {
                        int diffX = rotated.x - (bounds.x + bounds.width);
                        int diffY = rotated.y - (bounds.y + bounds.height);
                        newW += diffX;
                        newH += diffY;
                    }
                }

                // Clamp to avoid negatives
                newW = Math.max(1, newW);
                newH = Math.max(1, newH);

                currentSelection.setBounds(new Rectangle(newX, newY, newW, newH));
            }
            case MOVING -> {
                int dx = current.x - moveStartPoint.x;
                int dy = current.y - moveStartPoint.y;
                currentSelection.move(dx, dy);
                moveStartPoint = current;
            }
            case CREATING -> currentPoint = current;
        }

        canvas.repaint();
    }

    @Override
    public void onMouseRelease(org.example.gui.canvas.Canvas canvas, MouseEvent e) {
        if (canvas.getUnzoomedPoint(e.getPoint()) == currentPoint) {
            isActive = false;
        } else if (currentState == State.CREATING && startPoint != null && currentPoint != null) {
            int x = Math.min(startPoint.x, currentPoint.x);
            int y = Math.min(startPoint.y, currentPoint.y);
            int w = Math.abs(startPoint.x - currentPoint.x);
            int h = Math.abs(startPoint.y - currentPoint.y);

            if (w > 0 && h > 0) {
                Rectangle rect = new Rectangle(x, y, w, h);
                currentSelection = new Selection(rect);

                BufferedImage source = canvas.getCanvasImage();
                BufferedImage selectionImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = selectionImage.createGraphics();
                g2d.drawImage(source,
                        0, 0, w, h,
                        rect.x, rect.y, rect.x + w, rect.y + h,
                        null);
                g2d.dispose();

                currentSelection.setContent(selectionImage);

                // Remove area from canvas
                if (!copy) {
                    Graphics2D clearG = source.createGraphics();
                    clearG.setComposite(AlphaComposite.Clear);
                    clearG.fillRect(rect.x, rect.y, rect.width, rect.height);
                    clearG.dispose();
                }
            }
        }

        currentState = State.IDLE;
        moveStartPoint = null;
        currentPoint = null;
        activeHandle = null;
        tempPaintOverlay = false;
        canvas.repaint();
    }

    @Override
    public void paintOverlay(Graphics2D g2d, double zoomFactor) {
        if (tempPaintOverlay) {
            Stroke oldStroke = g2d.getStroke();
            g2d.setColor(new Color(0, 120, 215));
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{4f, 4f}, 0));
            g2d.drawRect(
                    (int) (startPoint.x * zoomFactor),
                    (int) (startPoint.y * zoomFactor),
                    (int) ((currentPoint.x - startPoint.x) * zoomFactor),
                    (int) ((currentPoint.y - startPoint.y) * zoomFactor)
            );
            g2d.setStroke(oldStroke);
        }
        if (currentSelection == null) return;

        Rectangle rect = currentSelection.getBounds();
        int centerX = rect.x + rect.width / 2;
        int centerY = rect.y + rect.height / 2;

        // Rotate the four corners
        Point topLeft = rotatePoint(rect.x, rect.y, centerX, centerY, rotationAngle);
        Point topRight = rotatePoint(rect.x + rect.width, rect.y, centerX, centerY, rotationAngle);
        Point bottomRight = rotatePoint(rect.x + rect.width, rect.y + rect.height, centerX, centerY, rotationAngle);
        Point bottomLeft = rotatePoint(rect.x, rect.y + rect.height, centerX, centerY, rotationAngle);

        // Scale points for zoom
        topLeft.x *= (int) zoomFactor;
        topLeft.y *= (int) zoomFactor;
        topRight.x *= (int) zoomFactor;
        topRight.y *= (int) zoomFactor;
        bottomRight.x *= (int) zoomFactor;
        bottomRight.y *= (int) zoomFactor;
        bottomLeft.x *= (int) zoomFactor;
        bottomLeft.y *= (int) zoomFactor;

        // --- Draw selected content (rotated)
        if (currentSelection.getContent() != null) {
            BufferedImage img = currentSelection.getContent();
            Graphics2D g2dCopy = (Graphics2D) g2d.create();

            // Translate to center position (scaled)
            g2dCopy.translate(centerX * zoomFactor, centerY * zoomFactor);

            // Rotate around center
            g2dCopy.rotate(rotationAngle);

            // Scale only the image size
            double scaleX = (rect.width * zoomFactor) / (double) img.getWidth();
            double scaleY = (rect.height * zoomFactor) / (double) img.getHeight();
            g2dCopy.scale(scaleX, scaleY);

            // Move to draw from top-left corner
            g2dCopy.translate(-img.getWidth() / 2.0, -img.getHeight() / 2.0);

            g2dCopy.drawImage(img, 0, 0, null);
            g2dCopy.dispose();
        }

        // --- Draw rotated selection border
        Path2D path = new Path2D.Double();
        path.moveTo(topLeft.x, topLeft.y);
        path.lineTo(topRight.x, topRight.y);
        path.lineTo(bottomRight.x, bottomRight.y);
        path.lineTo(bottomLeft.x, bottomLeft.y);
        path.closePath();

        Stroke oldStroke = g2d.getStroke();
        g2d.setColor(new Color(0, 120, 215));
        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{4f, 4f}, 0));
        g2d.draw(path);
        g2d.setStroke(oldStroke);

        // --- Draw corner handles
        drawHandle(g2d, topLeft);
        drawHandle(g2d, topRight);
        drawHandle(g2d, bottomRight);
        drawHandle(g2d, bottomLeft);

        // --- Draw rotation handle
        Point topCenter = new Point((topLeft.x + topRight.x) / 2, (topLeft.y + topRight.y) / 2);
        // Calculate rotated offset
        double angle = -Math.PI / 2 + rotationAngle; // upward from top center
        double distance = ROTATION_HANDLE_OFFSET;

        int handleX = (int)(topCenter.x + Math.cos(angle) * distance);
        int handleY = (int)(topCenter.y + Math.sin(angle) * distance);

        // Connect line
        g2d.setColor(new Color(0, 120, 215));
        g2d.drawLine(topCenter.x, topCenter.y, handleX, handleY);

        // Circle handle
        g2d.setColor(Color.WHITE);
        g2d.fillOval(handleX - ROTATION_HANDLE_RADIUS, handleY - ROTATION_HANDLE_RADIUS,
                ROTATION_HANDLE_RADIUS * 2, ROTATION_HANDLE_RADIUS * 2);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(handleX - ROTATION_HANDLE_RADIUS, handleY - ROTATION_HANDLE_RADIUS,
                ROTATION_HANDLE_RADIUS * 2, ROTATION_HANDLE_RADIUS * 2);
    }

    public void applySelection(Canvas canvas) {
        if (currentSelection != null && currentSelection.getContent() != null) {
            BufferedImage buffer = canvas.getCanvasImage();
            Graphics2D g2d = buffer.createGraphics();

            Rectangle sel = currentSelection.getBounds();
            BufferedImage content = currentSelection.getContent();

            g2d.translate(sel.x + sel.width / 2, sel.y + sel.height / 2);
            g2d.rotate(rotationAngle);
            g2d.translate(-sel.width / 2.0, -sel.height / 2.0);

            g2d.drawImage(content, 0, 0, sel.width, sel.height, null); // SCALE to match selection size

            g2d.dispose();

            currentSelection = null;
            isActive = false;
            rotationAngle = 0; // Reset after apply
            canvas.repaint();
        }
    }

    public void startCreating() {
        isActive = true;
    }

    private ScaleHandle getHandleAt(Point p) {
        if (currentSelection == null) return null;

        Rectangle bounds = currentSelection.getBounds();
        int centerX = bounds.x + bounds.width / 2;
        int centerY = bounds.y + bounds.height / 2;

        // Unrotate the click
        Point unrotated = rotatePoint(p.x, p.y, centerX, centerY, -rotationAngle);

        if (Math.abs(unrotated.x - bounds.x) <= HANDLE_SIZE && Math.abs(unrotated.y - bounds.y) <= HANDLE_SIZE) {
            return ScaleHandle.TOP_LEFT;
        } else if (Math.abs(unrotated.x - (bounds.x + bounds.width)) <= HANDLE_SIZE && Math.abs(unrotated.y - bounds.y) <= HANDLE_SIZE) {
            return ScaleHandle.TOP_RIGHT;
        } else if (Math.abs(unrotated.x - bounds.x) <= HANDLE_SIZE && Math.abs(unrotated.y - (bounds.y + bounds.height)) <= HANDLE_SIZE) {
            return ScaleHandle.BOTTOM_LEFT;
        } else if (Math.abs(unrotated.x - (bounds.x + bounds.width)) <= HANDLE_SIZE && Math.abs(unrotated.y - (bounds.y + bounds.height)) <= HANDLE_SIZE) {
            return ScaleHandle.BOTTOM_RIGHT;
        }

        return null;
    }

    private boolean isOnRotationHandle(Point p, double zoomFactor) {
        if (currentSelection == null) return false;

        Rectangle bounds = currentSelection.getBounds();
        int centerX = bounds.x + bounds.width / 2;
        int centerY = bounds.y + bounds.height / 2;

        // Unrotate click
        Point unrotated = rotatePoint(p.x, p.y, centerX, centerY, -rotationAngle);

        // Top middle
        int topCenterX = bounds.x + bounds.width / 2;
        int topCenterY = bounds.y;

        int handleCenterY = topCenterY - ROTATION_HANDLE_OFFSET;

        double distance = unrotated.distance(topCenterX, handleCenterY);
        return distance <= ROTATION_HANDLE_RADIUS * 1.5;
    }

    private void drawHandle(Graphics2D g2d, Point p) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(p.x - HANDLE_SIZE/2, p.y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(p.x - HANDLE_SIZE/2, p.y - HANDLE_SIZE/2, HANDLE_SIZE, HANDLE_SIZE);
    }

    private Point rotatePoint(int px, int py, int cx, int cy, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        int newX = (int) (cos * (px - cx) - sin * (py - cy) + cx);
        int newY = (int) (sin * (px - cx) + cos * (py - cy) + cy);
        return new Point(newX, newY);
    }

    private boolean isInsideSelection(Point p) {
        if (currentSelection == null) return false;

        Rectangle rect = currentSelection.getBounds();
        int centerX = rect.x + rect.width / 2;
        int centerY = rect.y + rect.height / 2;

        // Reverse-rotate the point by -rotationAngle around center
        double sin = Math.sin(-rotationAngle);
        double cos = Math.cos(-rotationAngle);

        int translatedX = p.x - centerX;
        int translatedY = p.y - centerY;

        int rotatedX = (int) (translatedX * cos - translatedY * sin) + centerX;
        int rotatedY = (int) (translatedX * sin + translatedY * cos) + centerY;

        return rect.contains(rotatedX, rotatedY);
    }

    public void setNewSelection(Selection sel) {
        this.currentSelection = sel;
        this.startPoint = new Point(sel.getX(), sel.getY());
        this.currentPoint = new Point(sel.getX()+sel.getWidth(), sel.getY()+sel.getHeight());
    }

    public Selection getCurrentSelection() {
        return currentSelection;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }

    public boolean getCopy() {
        return copy;
    }
}
