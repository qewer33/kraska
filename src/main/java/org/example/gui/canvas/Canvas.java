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

    public Canvas(int width, int height, Color backgroundColor) {
        this.logicalSize = new Dimension(width, height);
        autosaveExecutor.scheduleAtFixedRate(this::autoSave, 5, 5, TimeUnit.SECONDS);
        setPreferredSize(logicalSize);
        setBackground(backgroundColor); // Set the background color
        initializeCanvas(backgroundColor);
        setupMouseListeners();
        setOpaque(false);
    }

    private void initializeCanvas(Color backgroundColor) {
        buffer = new BufferedImage(logicalSize.width, logicalSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffer.createGraphics();
        g2d.setColor(backgroundColor); // Fill the canvas with the background color
        g2d.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        g2d.dispose();

        tempBuffer = new BufferedImage(logicalSize.width, logicalSize.height, BufferedImage.TYPE_INT_ARGB);
        loadLatestAutoSave();
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
    private void autoSave() {
        if (!canvasChangedSinceLastSave || buffer==null) return; // If there are no changes or buffer is null, skip autosaving

        try {
            // Create the autosave directory if it doesn't exist
            File autosaveDir = new File(".autosave");
            if (!autosaveDir.exists()) autosaveDir.mkdirs();

            // Generate a filename based on the current timestamp
            String filename = "autosave_" + System.currentTimeMillis() + ".png";
            File file = new File(autosaveDir, filename);

            // Create a safe copy of the canvas to save
            BufferedImage safeCopy = copyCanvas();

            // Save the canvas copy as a PNG file
            ImageIO.write(safeCopy, "PNG", file);

            // Update the timestamp and reset the change flag
            lastSavedTimestamp = System.currentTimeMillis();
            canvasChangedSinceLastSave = false;

            // Get all autosave files in the directory and clean up old ones if more than 3
            File[] files = autosaveDir.listFiles((dir, name) -> name.endsWith(".png"));
            if (files != null && files.length > 3) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified));

                for (int i = 0; i < files.length - 3; i++) {
                    files[i].delete();
                }
            }

            // Log the location where the autosave occurred
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
    public void loadLatestAutoSave() {
        File autosaveDir = new File(".autosave");
        if (!autosaveDir.exists()) return;

        // Get all .png files in the autosave directory
        File[] files = autosaveDir.listFiles((dir, name) -> name.endsWith(".png"));
        if (files == null || files.length == 0) return;

        // Find the most recently modified file
        File latest = Arrays.stream(files)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);

        // If a valid autosave file is found, prompt the user to load it
        if (latest != null) {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "Would you like to load the most recent autosave?",
                    "Autosave Found",
                    JOptionPane.YES_NO_OPTION
            );

            // If user selects "Yes", load the autosave file
            if (response != JOptionPane.YES_OPTION) return;

            try {
                // Read the latest autosave file into a BufferedImage
                BufferedImage loadedImage = ImageIO.read(latest);
                if (loadedImage != null) {
                    // Draw the loaded image onto the buffer (canvas)
                    Graphics2D g2d = buffer.createGraphics();
                    g2d.drawImage(loadedImage, 0, 0, null);
                    g2d.dispose();
                    repaint();
                    System.out.println("Auto-saved canvas loaded from: " + latest.getAbsolutePath());
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
        setSize(getPreferredSize());
        revalidate();
        repaint();
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

    public void setTempBufferAlpha(float alpha) {
        this.tempBufferAlpha = alpha;
    }

    public BufferedImage getTempBuffer() {
        return tempBuffer;
    }

    public void setTempBuffer(BufferedImage tempBuffer) {
        this.tempBuffer = tempBuffer;
    }

    public void setImage(BufferedImage buffer){this.buffer = buffer;}

    public BufferedImage getImage() {return buffer;}
}