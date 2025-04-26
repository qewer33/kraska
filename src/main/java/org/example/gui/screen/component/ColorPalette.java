package org.example.gui.screen.component;

import org.example.app.color.ColorManager;

import javax.swing.*;
import java.awt.*;

public class ColorPalette extends JPanel {

    private final ColorManager colorManager = ColorManager.getInstance();

    private static final Color[] DEFAULT_COLORS = {
            Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE,
            Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN,
            Color.BLUE, Color.MAGENTA, new Color(128, 0, 128), new Color(0, 128, 128), new Color(165, 42, 42),
            new Color(255, 192, 203), new Color(255, 228, 181), new Color(144, 238, 144), new Color(173, 216, 230), new Color(255, 182, 193)
    };

    public ColorPalette() {
        setLayout(new GridLayout(4, 5, 5, 5));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setOpaque(false);

        for (Color color : DEFAULT_COLORS) {
            JButton colorButton = new JButton();
            colorButton.setPreferredSize(new Dimension(30, 30));
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
