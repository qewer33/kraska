package org.example.app.color;

import java.awt.*;

public class ColorManager {
    private Color primary;
    private Color secondary;

    // Singleton design pattern for manager classes
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
    }

    public Color getPrimary() {
        return primary;
    }

    public void setPrimary(Color primary) {
        this.primary = primary;
    }

    public Color getSecondary() {
        return secondary;
    }

    public void setSecondary(Color secondary) {
        this.secondary = secondary;
    }
}
