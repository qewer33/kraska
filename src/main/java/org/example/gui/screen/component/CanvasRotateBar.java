package org.example.gui.screen.component;

import org.example.gui.canvas.Canvas;
import org.example.gui.canvas.CanvasViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class CanvasRotateBar extends JToolBar {
    public CanvasRotateBar(Canvas canvas, CanvasViewer viewer) {
        setFloatable(false);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOrientation(JToolBar.HORIZONTAL);

        // Get images from resource
        ImageIcon flipHorizontalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/rotate/flipHorizontal.png")));
        ImageIcon flipVerticalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/rotate/flipVertical.png")));
        ImageIcon rotateRightIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/rotate/rotateRight.png")));
        ImageIcon rotateLeftIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/rotate/rotateLeft.png")));

        // === Tool Buttons ===
        JButton rotateRightButton = new JButton(rotateRightIcon);
        JButton rotateLeftButton = new JButton(rotateLeftIcon);
        JButton flipVerticalButton = new JButton(flipVerticalIcon);
        JButton flipHorizontalButton = new JButton(flipHorizontalIcon);

        // Add and configure buttons
        addSeparator(new Dimension(5, 0));
        add(rotateRightButton);
        rotateRightButton.addActionListener(e -> {
            RotateRight(canvas,viewer);});

        addSeparator(new Dimension(5, 0));
        add(rotateLeftButton);
        rotateLeftButton.addActionListener(e -> {
            RotateLeft(canvas,viewer);
        });

        addSeparator(new Dimension(5, 0));
        add(flipVerticalButton);
        flipVerticalButton.addActionListener(e -> {
            FlipVertical(canvas,viewer);
        });

        addSeparator(new Dimension(5, 0));
        add(flipHorizontalButton);
        flipHorizontalButton.addActionListener(e -> {
            FlipHorizontal(canvas,viewer);
        });

    }

    // Rotate an image by the specified angle in degrees
    public BufferedImage rotateImage(BufferedImage image, double angleDegrees) {
        double radians = Math.toRadians(angleDegrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));

        int w = image.getWidth();
        int h = image.getHeight();

        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g2d = rotated.createGraphics();

        // Center the image and apply rotation
        g2d.translate((newWidth - w) / 2, (newHeight - h) / 2);
        g2d.rotate(radians, w / 2.0, h / 2.0);
        g2d.drawRenderedImage(image, null);
        g2d.dispose();

        return rotated;
    }

    // Rotate canvas image 90 degrees to the right
    public void RotateRight(Canvas canvas, CanvasViewer viewer) {
        BufferedImage originalImage = canvas.getCanvasImage();
        BufferedImage rotatedImage = rotateImage(originalImage, 90);
        canvas.setCanvasImage(rotatedImage);
        viewer.updateLayerBounds();
    }

    // Rotate canvas image 90 degrees to the left
    public void RotateLeft(Canvas canvas, CanvasViewer viewer) {
        BufferedImage originalImage = canvas.getCanvasImage();
        BufferedImage rotatedImage = rotateImage(originalImage, -90);
        canvas.setCanvasImage(rotatedImage);
        viewer.updateLayerBounds();
    }

    // Flip canvas image horizontally
    public void FlipVertical(Canvas canvas, CanvasViewer viewer) {
        BufferedImage originalImage = canvas.getCanvasImage();
        BufferedImage flippedImage = flipImage(originalImage, true);
        canvas.setCanvasImage(flippedImage);
        viewer.updateLayerBounds();
    }

    // Flip canvas image vertically
    public void FlipHorizontal(Canvas canvas, CanvasViewer viewer) {
        BufferedImage originalImage = canvas.getCanvasImage();
        BufferedImage flippedImage = flipImage(originalImage, false);
        canvas.setCanvasImage(flippedImage);
        viewer.updateLayerBounds();
    }

    // Helper method to flip an image horizontally or vertically
    public BufferedImage flipImage(BufferedImage image, boolean horizontal) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage flipped = new BufferedImage(w, h, image.getType());
        Graphics2D g2d = flipped.createGraphics();

        if (horizontal) {
            g2d.drawImage(image, 0, 0, w, h, w, 0, 0, h, null); // X yönü ters
        } else {
            g2d.drawImage(image, 0, 0, w, h, 0, h, w, 0, null); // Y yönü ters
        }

        g2d.dispose();
        return flipped;
    }
}
