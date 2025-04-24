package org.example.gui.canvas;

import org.example.app.color.ColorManager;
import org.example.app.tool.ToolManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.Stack;

public class Canvas extends JPanel {
    private Dimension logicalSize = new Dimension(800, 600);
    private BufferedImage canvas;
    private double zoomFactor = 1.0;

    // Managers
    private final ColorManager colorManager;
    private final ToolManager toolManager;

    // State management
    private MouseEvent lastEvent;
    private boolean isDrawing = false;
    private final Stack<BufferedImage> undoStack = new Stack<>();
    private final Stack<BufferedImage> redoStack = new Stack<>();

    public Canvas(int width, int height, Color backgroundColor) {
        colorManager = ColorManager.getInstance();
        toolManager = ToolManager.getInstance();
        this.logicalSize = new Dimension(width, height);
        setPreferredSize(logicalSize);
        setBackground(backgroundColor); // Set the background color
        initializeCanvas(backgroundColor);
        setupMouseListeners();
        setOpaque(false);
    }

    private void initializeCanvas(Color backgroundColor) {
        canvas = new BufferedImage(logicalSize.width, logicalSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(backgroundColor); // Fill the canvas with the background color
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2d.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Apply zoom (scale)
        g2d.scale(zoomFactor, zoomFactor);

        // Draw canvas image
        if (canvas != null) {
            g2d.drawImage(canvas, 0, 0, null);
        }

        g2d.dispose();
    }

    @Override
    public void repaint() {
        super.repaint();

        // Force parent (CheckerboardPanel) to repaint as well
        Container parent = getParent();
        if (parent != null) {
            parent.repaint(); // <- ensures the checkerboard is refreshed
        }
    }

    // --- DRAWING OPERATIONS ---

    public void startDrawing(MouseEvent e) {
        saveToUndoStack();
        isDrawing = true;
        lastEvent = e;

        if (toolManager.getActiveTool() instanceof CanvasPainter) {
            ((CanvasPainter) toolManager.getActiveTool()).onMousePress(this, e);
        }
    }

    public void continueDrawing(MouseEvent e) {
        if (isDrawing && toolManager.getActiveTool() instanceof CanvasPainter) {
            ((CanvasPainter) toolManager.getActiveTool()).onMouseDrag(this, e);
        }
    }

    public void finishDrawing() {
        if (isDrawing && toolManager.getActiveTool() instanceof CanvasPainter) {
            ((CanvasPainter) toolManager.getActiveTool()).onMouseRelease(this, lastEvent);
        }
        isDrawing = false;
        lastEvent = null;
    }

    public void applyImageOperation(BufferedImageOp op) {
        saveToUndoStack();
        BufferedImage result = op.filter(canvas, null);
        Graphics2D g2d = canvas.createGraphics();
        g2d.drawImage(result, 0, 0, null);
        g2d.dispose();
        repaint();
    }

    // --- CANVAS STATE MANAGEMENT ---

    private void saveToUndoStack() {
        BufferedImage copy = new BufferedImage(
                canvas.getWidth(), canvas.getHeight(), canvas.getType());
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(canvas, 0, 0, null);
        g2d.dispose();
        undoStack.push(copy);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(copyCanvas());
            canvas = undoStack.pop();
            repaint();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(copyCanvas());
            canvas = redoStack.pop();
            repaint();
        }
    }

    private BufferedImage copyCanvas() {
        BufferedImage copy = new BufferedImage(
                canvas.getWidth(), canvas.getHeight(), canvas.getType());
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(canvas, 0, 0, null);
        g2d.dispose();
        return copy;
    }

    public void clearCanvas() {
        saveToUndoStack();
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(colorManager.getSecondary());
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2d.dispose();
        repaint();
    }

    // --- MOUSE LISTENERS ---

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                startDrawing(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                finishDrawing();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                continueDrawing(e);
            }
        });
    }

    // --- UTILITY METHODS ---

    public Graphics2D getCanvasGraphics() {
        return canvas.createGraphics();
    }

    public Dimension getCanvasSize() {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                (int) (logicalSize.width * zoomFactor),
                (int) (logicalSize.height * zoomFactor)
        );
    }

    public Point getUnzoomedPoint(Point zoomedPoint) {
        int x = (int) (zoomedPoint.x / zoomFactor);
        int y = (int) (zoomedPoint.y / zoomFactor);
        return new Point(x, y);
    }

    // --- GETTERS AND SETTERS ---

    public BufferedImage getCanvasImage() {
        return canvas;
    }

    public ToolManager getToolManager() {
        return toolManager;
    }

    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
        repaint();
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public void setLogicalSize(Dimension logicalSize) {
        this.logicalSize = logicalSize;
    }

    public Dimension getLogicalSize() {
        return new Dimension(logicalSize);
    }
}