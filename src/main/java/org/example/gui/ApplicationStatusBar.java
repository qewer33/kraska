package org.example.gui;

import org.example.gui.canvas.CanvasViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ApplicationStatusBar extends JPanel {
    private CanvasViewer canvasViewer;

    public ApplicationStatusBar(CanvasViewer canvasViewer) {
        this.canvasViewer = canvasViewer;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JLabel zoom = new JLabel("Zoom: %100");

        JSlider zoomSlider = new JSlider(10, 500, 100);
        zoomSlider.setMaximumSize(new Dimension(125, 20));

        // Update canvas zoom factor based on zoom slider
        zoomSlider.addChangeListener(e -> {
            canvasViewer.setZoomFactor(zoomSlider.getValue()/100.0);
            zoom.setText("Zoom: %" + zoomSlider.getValue());
        });

        JButton decreaseButton = new JButton("-");
        decreaseButton.setContentAreaFilled(false); // Remove button background

        // Decrease zoom factor with decrease button
        decreaseButton.addActionListener(e -> {
            int currentValue = zoomSlider.getValue();
            if (currentValue > zoomSlider.getMinimum()) { // Minimum kontrolü
                zoomSlider.setValue(currentValue - 10); // Değeri azalt
            }
        });

        JButton increaseButton = new JButton("+");
        increaseButton.setContentAreaFilled(false); // Remove button background

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

        add(mousePos);
        add(Box.createHorizontalGlue());
        add(zoom);
        add(decreaseButton);
        add(zoomSlider);
        add(increaseButton);
    }
}
