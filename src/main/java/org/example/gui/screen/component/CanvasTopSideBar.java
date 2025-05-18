package org.example.gui.screen.component;

import org.example.gui.canvas.Canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class CanvasTopSideBar extends JToolBar {

    public CanvasTopSideBar(JFrame parentFrame, Canvas canvas) {
        setFloatable(false);

        // Clear Canvas Button
        JButton clearButton = new JButton();
        clearButton.setToolTipText("Clear Canvas");
        ImageIcon clearIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/TopSideBar/vacuum1.png")));
        clearButton.setIcon(clearIcon);
        clearButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    parentFrame,
                    "Are you sure you want to clear the canvas?",
                    "Clear Canvas",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                canvas.clearCanvas();
            }
        });

        // Resize Canvas Button
        JButton resizeButton = new JButton();
        resizeButton.setToolTipText("Resize Canvas");
        ImageIcon resizeIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/TopSideBar/resize.png")));
        resizeButton.setIcon(resizeIcon);
        resizeButton.addActionListener(e -> openResizeDialog(parentFrame, canvas));


        add(clearButton);
        add(resizeButton);
    }




    private void openResizeDialog(JFrame parentFrame, Canvas canvas) {
        JSpinner widthField = new JSpinner(new SpinnerNumberModel(canvas.getCanvasSize().getWidth(), 1, Integer.MAX_VALUE, 1));
        JSpinner heightField = new JSpinner(new SpinnerNumberModel(canvas.getCanvasSize().getHeight(), 1, Integer.MAX_VALUE, 1));

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Width:"));
        panel.add(widthField);
        panel.add(new JLabel("Height:"));
        panel.add(heightField);

        int result = JOptionPane.showConfirmDialog(
                parentFrame, panel, "Resize Canvas",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int newWidth = ((Number) widthField.getValue()).intValue();
                int newHeight = ((Number) heightField.getValue()).intValue();

                BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resized.createGraphics();

                g2d.setColor(canvas.getBackground()); // kalan kısımlar transparant background oluyordu
                g2d.fillRect(0, 0, newWidth, newHeight);

                g2d.drawImage(canvas.getCanvasImage(), 0, 0, null);
                g2d.dispose();

                canvas.setCanvasImage(resized);
                canvas.revalidate();
                canvas.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentFrame, "Please enter valid dimensions.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
