package org.example.gui.screen.component;

import org.example.app.color.ColorManager;

import javax.swing.*;
import java.awt.*;

public class ColorPalette extends JPanel {

    private final ColorManager colorManager = ColorManager.getInstance();

    private static final Color[] DEFAULT_COLORS = {
            Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE, Color.RED,
            Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA,
            new Color(128, 0, 128), new Color(0, 128, 128), new Color(165, 42, 42), new Color(255, 192, 203), new Color(255, 228, 181), new Color(144, 238, 144),
            new Color(173, 216, 230), new Color(255, 182, 193), new Color(255, 105, 180), new Color(0, 255, 127), new Color(70, 130, 180), new Color(138, 43, 226),
            new Color(127, 255, 212), new Color(72, 61, 139), new Color(240, 128, 128), new Color(255, 250, 205), new Color(95, 158, 160), new Color(218, 112, 214),
            new Color(176, 224, 230), new Color(154, 205, 50), new Color(255, 140, 0), new Color(0, 100, 0), new Color(255, 20, 147), new Color(0, 191, 255)


    };

    public ColorPalette() {
        setLayout(new GridLayout(6, 6, 5, 5));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setOpaque(false);

        for (Color color : DEFAULT_COLORS) {
            JButton colorButton = new JButton();
            colorButton.setPreferredSize(new Dimension(25, 25));
            colorButton.setBackground(color);
            colorButton.setOpaque(true);
            colorButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            colorButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        colorManager.setPrimary(color);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        colorManager.setSecondary(color);
                    }
                }
            });
            
            add(colorButton);
        }
    }
}
