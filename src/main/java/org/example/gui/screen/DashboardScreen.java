package org.example.gui.screen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.example.db.Project;
import org.example.db.ProjectDatabase;

public class DashboardScreen extends AbstractScreen {
    private final JFrame parentFrame;
    private final JTable projectTable;
    private final DefaultTableModel tableModel;
    private ProjectDatabase projectDatabase = ProjectDatabase.getInstance();
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DashboardScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Table to display projects
        String[] columnNames = {"Name", "Created", "Last Updated", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
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
        JButton createCanvasButton = new JButton("Create New Project");
        createCanvasButton.addActionListener(this::openCanvasSettingsWindow);

        // Add button to the bottom of the layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createCanvasButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadProjects() {
        // Fetch projects from the local database
        List<Project> projects = projectDatabase.getProjects();

        // Populate the table
        for (Project project : projects) {
            String createdFormatted;
            String lastOpenedFormatted;
            try {
                createdFormatted = LocalDateTime.parse(project.getCreated()).format(DISPLAY_FORMATTER);
            } catch (Exception ex) {
                createdFormatted = project.getCreated();
            }
            try {
                lastOpenedFormatted = LocalDateTime.parse(project.getLastOpened()).format(DISPLAY_FORMATTER);
            } catch (Exception ex) {
                lastOpenedFormatted = project.getLastOpened();
            }
            tableModel.addRow(new Object[]{
                    project.getName(),
                    createdFormatted,
                    lastOpenedFormatted,
                    "Load" // Button label
            });
        }
    }

    private void openCanvasSettingsWindow(ActionEvent e) {
        // Create a floating settings window
        JDialog settingsDialog = new JDialog(parentFrame, "Project Settings", true);
        settingsDialog.setSize(400, 350);
        settingsDialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Project name input
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        settingsDialog.add(new JLabel("Project Name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        JTextField nameField = new JTextField(20);
        settingsDialog.add(nameField, gbc);

        // Background color picker
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        settingsDialog.add(new JLabel("Canvas Background Color:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 1;
        JButton colorPickerButton = new JButton("Pick Color");
        settingsDialog.add(colorPickerButton, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 1;
        JPanel colorPreviewPanel = new JPanel();
        colorPreviewPanel.setPreferredSize(new Dimension(30, 30));
        colorPreviewPanel.setBackground(Color.WHITE);
        settingsDialog.add(colorPreviewPanel, gbc);

        colorPickerButton.addActionListener(event -> {
            Color selectedColor = JColorChooser.showDialog(settingsDialog, "Pick Canvas Background Color", colorPreviewPanel.getBackground());
            if (selectedColor != null) {
                colorPreviewPanel.setBackground(selectedColor);
            }
        });

        // Width input
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        settingsDialog.add(new JLabel("Canvas Width:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        JTextField widthField = new JTextField("800", 10);
        settingsDialog.add(widthField, gbc);

        // Height input
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        settingsDialog.add(new JLabel("Canvas Height:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        JTextField heightField = new JTextField("600", 10);
        settingsDialog.add(heightField, gbc);

        // Create Canvas button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton createCanvasButton = new JButton("Create Canvas");
        settingsDialog.add(createCanvasButton, gbc);

        createCanvasButton.addActionListener(event -> {
            try {
                String projectName = nameField.getText().trim();
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                Color backgroundColor = colorPreviewPanel.getBackground();

                if (projectName.isEmpty()) {
                    JOptionPane.showMessageDialog(settingsDialog, "Please enter a project name.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                for (Project project : projectDatabase.getProjects()) {
                    if (project.getName().equalsIgnoreCase(projectName)) {
                        JOptionPane.showMessageDialog(settingsDialog, "Project name already exists. Please choose a different name.", "Duplicate Project Name", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                projectDatabase.addProject(new Project(projectName, LocalDateTime.now().toString(), LocalDateTime.now().toString()));

                parentFrame.getContentPane().removeAll();
                CanvasScreen canvasScreen = new CanvasScreen(width, height, backgroundColor, projectName);
                parentFrame.getContentPane().add(canvasScreen);
                parentFrame.revalidate();
                parentFrame.repaint();

                settingsDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(settingsDialog, "Please enter valid numbers for width and height.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        settingsDialog.setLocationRelativeTo(parentFrame);
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
                String projectName = (String) tableModel.getValueAt(row, 0);
                JOptionPane.showMessageDialog(parentFrame, "Loading project: " + projectName);
                // Logic to load the project can be added here
            }
            isPushed = false;
            return label;
        }
    }
}
