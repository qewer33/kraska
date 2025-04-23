package org.example.gui.screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardScreen extends AbstractScreen {
    private final JFrame parentFrame;

    public DashboardScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new GridBagLayout()); // Center the button

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create Canvas button
        JButton createCanvasButton = new JButton("Create Canvas");
        createCanvasButton.addActionListener(this::openCanvasSettingsWindow);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(createCanvasButton, gbc);
    }

    private void openCanvasSettingsWindow(ActionEvent e) {
        // Create a floating settings window
        JDialog settingsDialog = new JDialog(parentFrame, "Canvas Settings", true);
        settingsDialog.setSize(400, 300);
        settingsDialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Background color picker
        JLabel colorLabel = new JLabel("Canvas Background Color:");
        settingsDialog.add(colorLabel, gbc);

        gbc.gridx = 1;
        JButton colorPickerButton = new JButton("Pick Color");
        JPanel colorPreviewPanel = new JPanel();
        colorPreviewPanel.setPreferredSize(new Dimension(30, 30));
        colorPreviewPanel.setBackground(Color.WHITE); // Default color
        colorPickerButton.addActionListener(event -> {
            Color selectedColor = JColorChooser.showDialog(settingsDialog, "Pick Canvas Background Color", colorPreviewPanel.getBackground());
            if (selectedColor != null) {
                colorPreviewPanel.setBackground(selectedColor);
            }
        });
        settingsDialog.add(colorPickerButton, gbc);

        gbc.gridx = 2;
        settingsDialog.add(colorPreviewPanel, gbc);

        // Width input
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel widthLabel = new JLabel("Canvas Width:");
        settingsDialog.add(widthLabel, gbc);

        gbc.gridx = 1;
        JTextField widthField = new JTextField("800", 10);
        settingsDialog.add(widthField, gbc);

        // Height input
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel heightLabel = new JLabel("Canvas Height:");
        settingsDialog.add(heightLabel, gbc);

        gbc.gridx = 1;
        JTextField heightField = new JTextField("600", 10);
        settingsDialog.add(heightField, gbc);

        // Create Canvas button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton createCanvasButton = new JButton("Create Canvas");
        createCanvasButton.addActionListener(event -> {
            try {
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                Color backgroundColor = colorPreviewPanel.getBackground();

                parentFrame.getContentPane().removeAll(); // Clear the frame
                CanvasScreen canvasScreen = new CanvasScreen(width, height, backgroundColor); // Pass dimensions and background color
                parentFrame.getContentPane().add(canvasScreen); // Add CanvasScreen
                parentFrame.revalidate();
                parentFrame.repaint();

                settingsDialog.dispose(); // Close the settings window
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(settingsDialog, "Please enter valid numbers for width and height.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        settingsDialog.add(createCanvasButton, gbc);

        settingsDialog.setLocationRelativeTo(parentFrame); // Center the dialog relative to the main frame
        settingsDialog.setVisible(true);
    }
}
