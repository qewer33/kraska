package org.example.app.tool;

import org.example.gui.canvas.Canvas;
import org.example.app.color.ColorManager;
import org.example.gui.canvas.CanvasPainter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ColorPickerTool extends AbstractTool implements CanvasPainter {
    private final ColorManager colorManager;
    private ColorUpdateCallback colorUpdateCallback;

    @FunctionalInterface
    public interface ColorUpdateCallback {
        void onColorUpdate(boolean isPrimary, Color color);
    }

    public ColorPickerTool() {
        super("Color Picker");
        this.colorManager = ColorManager.getInstance();
    }

    @Override
    public void onMousePress(Canvas canvas, MouseEvent e) {
        Point point = canvas.getUnzoomedPoint(e.getPoint());
        Color pickedColor = canvas.getColorAt(canvas, point);

        if (e.getButton() == MouseEvent.BUTTON1) {
            colorManager.setPrimary(pickedColor);
            if (colorUpdateCallback != null) {
                colorUpdateCallback.onColorUpdate(true, pickedColor);
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            colorManager.setSecondary(pickedColor);
            if (colorUpdateCallback != null) {
                colorUpdateCallback.onColorUpdate(false, pickedColor);
            }
        }
    }

    @Override
    public void onMouseDrag(Canvas canvas, MouseEvent e) {}

    @Override
    public void onMouseRelease(Canvas canvas, MouseEvent e) {}

    public void setColorUpdateCallback(ColorUpdateCallback callback) {
        this.colorUpdateCallback = callback;
    }
}
