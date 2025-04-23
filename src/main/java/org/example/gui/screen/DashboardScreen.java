package org.example.gui.screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardScreen extends AbstractScreen {
    private final JFrame parentFrame;
    private Color canvasBackgroundColor = Color.WHITE; // Default canvas background color
    private final JPanel colorPreviewPanel; // Panel to visualize the selected color

    public DashboardScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout()); // Center the components

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Background color picker
        JLabel colorLabel = new JLabel("Canvas Background Color:");
        add(colorLabel, gbc);

        gbc.gridx = 1;
        JButton colorPickerButton = new JButton("Pick Color");
        colorPickerButton.addActionListener(this::pickBackgroundColor);
        add(colorPickerButton, gbc);

        // Color preview panel
        gbc.gridx = 2;
        colorPreviewPanel = new JPanel();
        colorPreviewPanel.setPreferredSize(new Dimension(30, 30));
        colorPreviewPanel.setBackground(canvasBackgroundColor); // Set initial color
        colorPreviewPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(colorPreviewPanel, gbc);

        // Width input
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel widthLabel = new JLabel("Canvas Width:");
        add(widthLabel, gbc);

        gbc.gridx = 1;
        JTextField widthField = new JTextField("800", 10);
        add(widthField, gbc);

        // Height input
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel heightLabel = new JLabel("Canvas Height:");
        add(heightLabel, gbc);

        gbc.gridx = 1;
        JTextField heightField = new JTextField("600", 10);
        add(heightField, gbc);

        // Open Canvas button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton openCanvasButton = new JButton("Open Canvas");
        openCanvasButton.addActionListener(e -> openCanvasScreen(e, widthField, heightField));
        add(openCanvasButton, gbc);
    }

    private void pickBackgroundColor(ActionEvent e) {
        Color selectedColor = JColorChooser.showDialog(this, "Pick Canvas Background Color", canvasBackgroundColor);
        if (selectedColor != null) {
            canvasBackgroundColor = selectedColor; // Store the selected color
            colorPreviewPanel.setBackground(canvasBackgroundColor); // Update the preview panel
        }
    }

    private void openCanvasScreen(ActionEvent e, JTextField widthField, JTextField heightField) {
        try {
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());

            parentFrame.getContentPane().removeAll(); // Clear the frame
            CanvasScreen canvasScreen = new CanvasScreen(width, height, canvasBackgroundColor); // Pass dimensions and background color
            parentFrame.getContentPane().add(canvasScreen); // Add CanvasScreen
            parentFrame.revalidate();
            parentFrame.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for width and height.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}
