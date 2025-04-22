package org.example.app.color;

import java.awt.*;

public class ColorManager {
    private Color primary;
    private Color secondary;

    public ColorManager(Color primary, Color secondary) {
        this.primary = primary;
        this.secondary = secondary;
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
