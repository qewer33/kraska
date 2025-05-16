package org.example.gui.screen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import org.example.db.Project;
import org.example.db.ProjectDatabase;

public class DashboardScreen extends AbstractScreen {
    private final JFrame parentFrame;
    private final JTable projectTable;
    private final DefaultTableModel tableModel;

    public DashboardScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Table to display projects
        String[] columnNames = {"Name", "File Address", "Created", "Last Updated", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        projectTable = new JTable(tableModel);
        projectTable.getTableHeader().setReorderingAllowed(false);

        // Add a custom renderer and editor for the "Actions" column
        TableColumn actionColumn = projectTable.getColumn("Actions");
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));


        JScrollPane scrollPane = new JScrollPane(projectTable);

        // Load existing projects into the table
        loadProjects();

        // Add table to the center of the layout
        add(scrollPane, BorderLayout.CENTER);

        // Create Canvas button
        JButton createCanvasButton = new JButton("Create New Canvas");
        createCanvasButton.addActionListener(this::openCanvasSettingsWindow);

        // Add button to the bottom of the layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createCanvasButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadProjects() {
        // Fetch projects from the local database
        List<Project> projects = ProjectDatabase.getInstance().getProjects();

        // Populate the table
        for (Project project : projects) {
            tableModel.addRow(new Object[]{
                    project.getName(),
                    project.getFileAddress(),
                    project.getCreated(),
                    project.getLastOpened(),
                    "Load" // Button label
            });
        }
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

    // Custom renderer for the "Actions" column
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Custom editor for the "Actions" column
    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int row = projectTable.getSelectedRow();
                String fileAddress = (String) tableModel.getValueAt(row, 1);
                JOptionPane.showMessageDialog(parentFrame, "Loading project: " + fileAddress);
                // Logic to load the project can be added here
            }
            isPushed = false;
            return label;
        }
    }
}
