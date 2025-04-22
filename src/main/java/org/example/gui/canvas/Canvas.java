package org.example.gui.canvas;

import org.example.app.tool.AbstractTool;
import org.example.app.tool.ToolManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.Stack;

public class Canvas extends JPanel {
    // Drawing properties
    private Dimension logicalSize = new Dimension(800, 600);
    private BufferedImage canvas;
    private BufferedImage temporaryDrawing;
    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    private Color backgroundColor = Color.WHITE;
    private double zoomFactor = 1.0;

    // Tool system
    private ToolManager toolManager;
    private AbstractTool currentTool;

    // State management
    private MouseEvent lastEvent;
    private boolean isDrawing = false;
    private Stack<BufferedImage> undoStack = new Stack<>();
    private Stack<BufferedImage> redoStack = new Stack<>();

    public Canvas() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(backgroundColor);
        initializeToolSystem();
        initializeCanvas();
        setupMouseListeners();
    }

    private void initializeToolSystem() {
        toolManager = new ToolManager(this);

        // Set default tool
        toolManager.activateTool("Brush");
    }

    private void initializeCanvas() {
        canvas = new BufferedImage(logicalSize.width, logicalSize.height, BufferedImage.TYPE_INT_ARGB);
        clearCanvas();
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

    // ===================
    // Tool System Methods
    // ===================

    public void setCurrentTool(AbstractTool tool) {
        if (currentTool != null) {
            currentTool.onDeactivate(this);
        }

        currentTool = tool;

        if (currentTool != null) {
            currentTool.onActivate(this);
            updateCursor();
        }
    }

    private void updateCursor() {
        if (currentTool instanceof CanvasPainter) {
            // Set appropriate cursor for painting tools
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    // ===================
    // Drawing Operations
    // ===================

    public void startDrawing(MouseEvent e) {
        // saveToUndoStack();
        isDrawing = true;
        lastEvent = e;

        if (currentTool instanceof CanvasPainter) {
            ((CanvasPainter) currentTool).onMousePress(this, e);
        }
    }

    public void continueDrawing(MouseEvent e) {
        if (isDrawing && currentTool instanceof CanvasPainter) {
            ((CanvasPainter) currentTool).onMouseDrag(this, e);
        }
    }

    public void finishDrawing() {
        if (isDrawing && currentTool instanceof CanvasPainter) {
            ((CanvasPainter) currentTool).onMouseRelease(this, lastEvent);
        }
        isDrawing = false;
        lastEvent = null;
        temporaryDrawing = null;
    }

    public void applyImageOperation(BufferedImageOp op) {
        saveToUndoStack();
        BufferedImage result = op.filter(canvas, null);
        Graphics2D g2d = canvas.createGraphics();
        g2d.drawImage(result, 0, 0, null);
        g2d.dispose();
        repaint();
    }

    // ===================
    // Canvas State Management
    // ===================

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
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2d.dispose();
        repaint();
    }

    // ===================
    // Mouse Listeners
    // ===================

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

    // ===================
    // Getters and Setters
    // ===================

    public BufferedImage getCanvasImage() {
        return canvas;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setBrushSize(int size) {
        this.brushSize = size;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public ToolManager getToolManager() {
        return toolManager;
    }

    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
        repaint();
    }

    public void setLogicalSize(Dimension logicalSize) {
        this.logicalSize = logicalSize;
    }

    public Dimension getLogicalSize() {
        return new Dimension(logicalSize);
    }

    // ===================
    // Utility Methods for Tools
    // ===================

    public Graphics2D getCanvasGraphics() {
        return canvas.createGraphics();
    }

    public void setTemporaryDrawing(BufferedImage tempImage) {
        this.temporaryDrawing = tempImage;
        repaint();
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
}