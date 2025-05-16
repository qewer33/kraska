package org.example.gui.canvas;

import org.example.app.color.ColorManager;
import org.example.app.tool.ToolManager;
import org.example.gui.canvas.selection.SelectionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.Stack;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Canvas is a JPanel that displays a canvas image and handles drawing operations.
 * It is the base class of Kraska's painting engine.
 */
public class Canvas extends JPanel {
    private Dimension logicalSize = new Dimension(800, 600);
    private BufferedImage buffer;
    private BufferedImage tempBuffer;
    private float tempBufferAlpha = 1.0f;
    private double zoomFactor = 1.0;
    private long lastSavedTimestamp = 0;
    private boolean canvasChangedSinceLastSave = false;
    private final ScheduledExecutorService autosaveExecutor = Executors.newSingleThreadScheduledExecutor();

    // Managers
    private final ColorManager colorManager = ColorManager.getInstance();
    private final ToolManager toolManager = ToolManager.getInstance();
    private final SelectionManager selectionManager = SelectionManager.getInstance();

    // State management
    private MouseEvent lastEvent;
    private boolean isDrawing = false;
    private final Stack<BufferedImage> undoStack = new Stack<>();
    private final Stack<BufferedImage> redoStack = new Stack<>();

    public Canvas() {}

    public Canvas(int width, int height, Color backgroundColor, String projectName) {
        this.logicalSize = new Dimension(width, height);
        autosaveExecutor.scheduleAtFixedRate(() -> autoSave(projectName), 5, 5, TimeUnit.SECONDS);
        setPreferredSize(logicalSize);
        setBackground(backgroundColor);
        initializeCanvas(backgroundColor, projectName);
        setupMouseListeners();
        setOpaque(false);
    }

    private void initializeCanvas(Color backgroundColor, String projectName) {
        buffer = new BufferedImage(logicalSize.width, logicalSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffer.createGraphics();
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        g2d.dispose();

        tempBuffer = new BufferedImage(logicalSize.width, logicalSize.height, BufferedImage.TYPE_INT_ARGB);
        loadLatestAutoSave(projectName);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Apply zoom (scale)
        g2d.scale(zoomFactor, zoomFactor);

        // Draw canvas image
        if (buffer != null) {
            g2d.drawImage(buffer, 0, 0, null);
        }

        if (tempBuffer != null) {
            // Draw tempBuffer with opacity (force) if currently drawing
            Composite originalComposite = g2d.getComposite();

            if (tempBufferAlpha < 1.0f) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tempBufferAlpha));
            }

            g2d.drawImage(tempBuffer, 0, 0, null);
            g2d.setComposite(originalComposite);
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

        if (selectionManager.isActive()) selectionManager.getView().onMousePress(this, e);

        if (!(selectionManager.restrictToolInput && selectionManager.isActive()) && toolManager.getActiveTool() instanceof CanvasPainter tool) {
            tool.onMousePress(this, e);
        }
    }

    public void continueDrawing(MouseEvent e) {
        if (selectionManager.isActive()) selectionManager.getView().onMouseDrag(this, e);

        if (!(selectionManager.restrictToolInput && selectionManager.isActive()) && isDrawing && toolManager.getActiveTool() instanceof CanvasPainter tool) {
            tool.onMouseDrag(this, e);
        }
    }

    public void finishDrawing() {
        if (selectionManager.isActive()) selectionManager.getView().onMouseRelease(this, lastEvent);

        if (!(selectionManager.restrictToolInput && selectionManager.isActive()) && isDrawing && toolManager.getActiveTool() instanceof CanvasPainter tool) {
            tool.onMouseRelease(this, lastEvent);
        }

        isDrawing = false;
        lastEvent = null;
    }

    public void applyImageOperation(BufferedImageOp op) {
        saveToUndoStack();
        BufferedImage result = op.filter(buffer, null);
        Graphics2D g2d = buffer.createGraphics();
        g2d.drawImage(result, 0, 0, null);
        g2d.dispose();
        repaint();
    }

    // --- CANVAS STATE MANAGEMENT ---

    private void saveToUndoStack() {
        BufferedImage copy = new BufferedImage(
                buffer.getWidth(), buffer.getHeight(), buffer.getType());
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(buffer, 0, 0, null);
        g2d.dispose();
        undoStack.push(copy);
        redoStack.clear();
        canvasChangedSinceLastSave = true;
    }

    // Automatically save the canvas if changes have been made
    private void autoSave(String projectName) {
    if (!canvasChangedSinceLastSave || buffer == null) return;

    try {
        // Main saves directory (e.g., in user home)
        String savesRoot = System.getProperty("user.home") + File.separator + "kraska_saves";
        // Sanitize project name for folder use
        String safeProjectName = projectName.replaceAll("[^a-zA-Z0-9\\-_]", "_");
        File projectDir = new File(savesRoot, safeProjectName);

        if (!projectDir.exists()) projectDir.mkdirs();

        // Generate a filename based on the current timestamp
        String filename = "autosave_" + System.currentTimeMillis() + ".png";
        File file = new File(projectDir, filename);

        // Create a safe copy of the canvas to save
        BufferedImage safeCopy = copyCanvas();
        ImageIO.write(safeCopy, "PNG", file);

        lastSavedTimestamp = System.currentTimeMillis();
        canvasChangedSinceLastSave = false;

        // Optionally, clean up old autosaves (e.g., keep only last 3)
        File[] files = projectDir.listFiles((dir, name) -> name.endsWith(".png"));
        if (files != null && files.length > 3) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            for (int i = 0; i < files.length - 3; i++) {
                files[i].delete();
            }
        }

        System.out.println("Auto-saved at: " + file.getAbsolutePath());
    } catch (IOException ex) {
        ex.printStackTrace();
    }
}

    // Shutdown the autosave functionality (stop the autosave executor)
    public void shutdownAutosave() {
        autosaveExecutor.shutdownNow();
    }

    // Load the latest autosave file if it exists
    public void loadLatestAutoSave(String projectName) {
        String savesRoot = System.getProperty("user.home") + File.separator + "kraska_saves";
        String safeProjectName = projectName.replaceAll("[^a-zA-Z0-9\\-_]", "_");
        File projectDir = new File(savesRoot, safeProjectName);

        if (!projectDir.exists()) return;

        File[] files = projectDir.listFiles((dir, name) -> name.endsWith(".png"));
        if (files == null || files.length == 0) return;

        File latest = Arrays.stream(files)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);

        if (latest != null) {
            try {
                BufferedImage loadedImage = ImageIO.read(latest);
                if (loadedImage != null) {
                    Graphics2D g2d = buffer.createGraphics();
                    g2d.drawImage(loadedImage, 0, 0, null);
                    g2d.dispose();
                    repaint();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(copyCanvas());
            buffer = undoStack.pop();
            repaint();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(copyCanvas());
            buffer = redoStack.pop();
            repaint();
        }
    }

    private BufferedImage copyCanvas() {
        BufferedImage copy = new BufferedImage(
                buffer.getWidth(), buffer.getHeight(), buffer.getType());
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(buffer, 0, 0, null);
        g2d.dispose();
        return copy;
    }

    public void clearCanvas() {
        saveToUndoStack();
        Graphics2D g2d = buffer.createGraphics();
        g2d.setColor(colorManager.getSecondary());
        g2d.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        g2d.dispose();
        repaint();
    }

    public Graphics2D getTempGraphics() {
        return tempBuffer.createGraphics();
    }

    public void clearTempBuffer() {
        Graphics2D g2d = tempBuffer.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, tempBuffer.getWidth(), tempBuffer.getHeight());
        g2d.dispose();
        repaint();
    }

    public void applyTempBuffer(float opacity) {
        Graphics2D g2d = buffer.createGraphics();
        g2d.setComposite(AlphaComposite.SrcOver.derive(opacity));
        g2d.drawImage(tempBuffer, 0, 0, null);
        g2d.dispose();

        clearTempBuffer(); // Clear temp after applying
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
        return buffer.createGraphics();
    }

    public Dimension getCanvasSize() {
        return new Dimension(buffer.getWidth(), buffer.getHeight());
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

    public Color getColorAt(Canvas canvas, Point point) {
        BufferedImage image = canvas.getCanvasImage();
        if (point.x >= 0 && point.x < image.getWidth()
                && point.y >= 0 && point.y < image.getHeight()) {
            return new Color(image.getRGB(point.x, point.y), true);
        }
        return Color.WHITE;
    }

    // --- GETTERS AND SETTERS ---

    public BufferedImage getCanvasImage() {
        return buffer;
    }
    public void setCanvasImage(BufferedImage newImage) {
        this.buffer = newImage;
        this.logicalSize = new Dimension(newImage.getWidth(), newImage.getHeight());
        this.tempBuffer = new BufferedImage(
                newImage.getWidth(), newImage.getHeight(), BufferedImage.TYPE_INT_ARGB
        );
        setPreferredSize(getPreferredSize());
        revalidate();
        repaint();
    }

    public ToolManager getToolManager() {
        return toolManager;
    }

    public void setZoomFactor(double zoomFactor) {
        double oldValue = this.zoomFactor;
        this.zoomFactor = zoomFactor;
        repaint();

        this.firePropertyChange("zoomFactor", oldValue, zoomFactor);
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

    public void setTempBufferAlpha(float alpha) {
        this.tempBufferAlpha = alpha;
    }

    public BufferedImage getTempBuffer() {
        return tempBuffer;
    }

    public void setTempBuffer(BufferedImage tempBuffer) {
        this.tempBuffer = tempBuffer;
    }

    public void setImage(BufferedImage buffer) {
        this.buffer = buffer;
        this.logicalSize = new Dimension(buffer.getWidth(), buffer.getHeight());
        this.tempBuffer = new BufferedImage(buffer.getWidth(), buffer.getHeight(), BufferedImage.TYPE_INT_ARGB);
        setPreferredSize(getPreferredSize());
        revalidate();
        repaint();
    }

    public BufferedImage getImage() {return buffer;}
}