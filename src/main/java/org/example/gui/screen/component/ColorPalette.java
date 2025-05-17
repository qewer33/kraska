package org.example.gui.screen.component;

import org.example.app.color.ColorManager;

import javax.swing.*;
import java.awt.*;

public class ColorPalette extends JPanel {

    private final ColorManager colorManager = ColorManager.getInstance();

    private static final Color[] DEFAULT_COLORS = {
            // Row 1: Grayscale
            Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, new Color(224, 224, 224), Color.WHITE,

            // Row 2: Reds -> Oranges
            new Color(139, 0, 0), Color.RED, new Color(255, 69, 0), new Color(255, 140, 0), new Color(255, 165, 0), Color.ORANGE,

            // Row 3: Yellows -> Greens
            new Color(255, 215, 0), Color.YELLOW, new Color(173, 255, 47), new Color(127, 255, 0), new Color(0, 128, 0), new Color(34, 139, 34),

            // Row 4: Light Greens -> Teals -> Light Blues
            new Color(144, 238, 144), new Color(0, 255, 127), new Color(0, 255, 255), new Color(0, 191, 255), new Color(135, 206, 250), new Color(173, 216, 230),

            // Row 5: Blues -> Purples
            Color.BLUE, new Color(65, 105, 225), new Color(75, 0, 130), new Color(138, 43, 226), new Color(148, 0, 211), new Color(186, 85, 211),

            // Row 6: Pinks -> Browns
            new Color(255, 20, 147), new Color(255, 105, 180), new Color(210, 105, 30), new Color(160, 82, 45), new Color(205, 133, 63), new Color(222, 184, 135)
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
