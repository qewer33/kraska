package org.example.gui.canvas.selection;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Selection {
    private Rectangle bounds;
    private BufferedImage content; // Can be null if not yet copied

    public Selection(Rectangle bounds) {
        this.bounds = new Rectangle(bounds); // Defensive copy
        this.content = null;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = new Rectangle(bounds);
    }

    public int getX() {
        return bounds.x;
    }

    public int getY() {
        return bounds.y;
    }

    public int getWidth() {
        return bounds.width;
    }

    public int getHeight() {
        return bounds.height;
    }

    public BufferedImage getContent() {
        return content;
    }

    public void setContent(BufferedImage content) {
        this.content = content;
    }

    public void move(int dx, int dy) {
        bounds.translate(dx, dy);
    }

    public boolean contains(Point p) {
        return bounds.contains(p);
    }
}

