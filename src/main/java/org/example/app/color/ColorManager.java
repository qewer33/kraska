package org.example.app.color;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ColorManager {
    private Color primary;
    private Color secondary;

    private final List<ColorChangeListener> listeners = new ArrayList<>();

    // Singleton design pattern
    private static ColorManager instance;

    public static ColorManager getInstance() {
        if (instance == null) {
            instance = new ColorManager(Color.BLACK, Color.WHITE);
        }
        return instance;
    }

    private ColorManager(Color primary, Color secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public void swap() {
        Color temp = primary;
        primary = secondary;
        secondary = temp;
        notifyColorChanged();
    }

    public Color getPrimary() {
        return primary;
    }

    public void setPrimary(Color primary) {
        if (!this.primary.equals(primary)) {
            this.primary = primary;
            notifyColorChanged();
        }
    }

    public Color getSecondary() {
        return secondary;
    }

    public void setSecondary(Color secondary) {
        if (!this.secondary.equals(secondary)) {
            this.secondary = secondary;
            notifyColorChanged();
        }
    }

    // === Listener Support ===

    public interface ColorChangeListener {
        void onColorChanged(Color primary, Color secondary);
    }

    public void addColorChangeListener(ColorChangeListener listener) {
        listeners.add(listener);
    }

    public void removeColorChangeListener(ColorChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyColorChanged() {
        for (ColorChangeListener listener : listeners) {
            listener.onColorChanged(primary, secondary);
        }
    }
}
