package org.example.gui;

import org.example.gui.canvas.CanvasViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class ApplicationStatusBar extends JPanel {
    private CanvasViewer canvasViewer;

    public ApplicationStatusBar(CanvasViewer canvasViewer) {
        this.canvasViewer = canvasViewer;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JLabel zoom = new JLabel("%100");

        JSlider zoomSlider = new JSlider(10, 500, 100);
        zoomSlider.setMaximumSize(new Dimension(125, 20));

        // Get icons from resource
        ImageIcon zoomInIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/status/zoom_in.png")));
        ImageIcon zoomOutIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/status/zoom_out.png")));
        ImageIcon cursor = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/status/cursor.png")));
        JLabel cursorLabel = new JLabel(cursor);

        // Update canvas zoom factor based on zoom slider
        zoomSlider.addChangeListener(e -> {
            canvasViewer.setZoomFactor(zoomSlider.getValue()/100.0);
            zoom.setText("%" + zoomSlider.getValue() + " ");
        });

        // Update slider when canvas zoom changes
        canvasViewer.addPropertyChangeListener("zoomFactor", evt -> {
            int zoomPercentage = (int) (canvasViewer.getZoomFactor() * 100);
            zoomSlider.setValue(zoomPercentage);
            zoom.setText("%" + zoomSlider.getValue());
        });


        JButton decreaseButton = new JButton(zoomOutIcon);
        decreaseButton.setContentAreaFilled(false); // Remove button background
        decreaseButton.setPreferredSize(new Dimension(zoomOutIcon.getIconWidth(), zoomOutIcon.getIconHeight())); // Set button size as icon size

        // Decrease zoom factor with decrease button
        decreaseButton.addActionListener(e -> {
            int currentValue = zoomSlider.getValue();
            if (currentValue > zoomSlider.getMinimum()) {
                zoomSlider.setValue(currentValue - 10);
            }
        });

        JButton increaseButton = new JButton(zoomInIcon);
        increaseButton.setContentAreaFilled(false); // Remove button background
        increaseButton.setPreferredSize(new Dimension(zoomInIcon.getIconWidth(), zoomInIcon.getIconHeight())); // Set button size as icon size

        // Increase zoom factor with increase button
        increaseButton.addActionListener(e -> {
            int currentValue = zoomSlider.getValue();
            if (currentValue < zoomSlider.getMaximum()) {
                zoomSlider.setValue(currentValue + 10);
            }
        });

        JLabel mousePos = new JLabel("Mouse Position: -");

        // Get mouse positions on canvas
        canvasViewer.getCanvas().addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                mousePos.setText( x + ", " + y + "px");
            }
        });

        // Print nothing outside of canvas
        canvasViewer.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                mousePos.setText("");
            }
        });

        add(cursorLabel);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(mousePos);
        add(Box.createHorizontalGlue());
        add(zoom);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(decreaseButton);
        add(zoomSlider);
        add(increaseButton);
        add(Box.createRigidArea(new Dimension(5, 0)));
    }
}
