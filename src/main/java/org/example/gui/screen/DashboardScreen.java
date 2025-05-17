package org.example.gui.screen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.example.db.Project;
import org.example.db.ProjectDatabase;
import org.example.gui.screen.component.DashboardBannerPanel;

public class DashboardScreen extends AbstractScreen {
    private final JFrame parentFrame;
    private final JTable projectTable;
    private final DefaultTableModel tableModel;
    private ProjectDatabase projectDatabase = ProjectDatabase.getInstance();
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DashboardScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Setup banner
        DashboardBannerPanel bannerPanel = new DashboardBannerPanel();
        bannerPanel.newProjectButton.addActionListener(this::openCanvasSettingsWindow);
        add(bannerPanel, BorderLayout.NORTH);

        // Table to display projects
        String[] columnNames = {"Name", "Created", "Last Updated", "Load", "Delete"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4; // Only "Load" and "Delete" columns are editable (buttons)
            }
        };
        projectTable = new JTable(tableModel);
        projectTable.getTableHeader().setReorderingAllowed(false);

        // Add custom renderers and editors for both action columns
        TableColumn loadColumn = projectTable.getColumn("Load");
        loadColumn.setCellRenderer(new ButtonRenderer());
        loadColumn.setCellEditor(new LoadButtonEditor(new JCheckBox()));

        TableColumn deleteColumn = projectTable.getColumn("Delete");
        deleteColumn.setCellRenderer(new ButtonRenderer());
        deleteColumn.setCellEditor(new DeleteButtonEditor(new JCheckBox()));


        JScrollPane scrollPane = new JScrollPane(projectTable);

        // Load existing projects into the table
        loadProjects();

        // Add table to the center of the layout
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadProjects() {
        // Fetch projects from the local database
        List<Project> projects = projectDatabase.getProjects();
        tableModel.setRowCount(0); // Clear table before loading
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
                    "Load", // Button label
                    "Delete" // Button label
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

    // Custom editor for the "Load" button in the "Actions" column
    private class LoadButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;

        public LoadButtonEditor(JCheckBox checkBox) {
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

                // Update last opened time in the database
                projectDatabase.updateLastOpened(projectName, java.time.LocalDateTime.now().toString());

                // Remove dashboard and show CanvasScreen for this project
                parentFrame.getContentPane().removeAll();
                CanvasScreen canvasScreen = new CanvasScreen(800, 600, Color.WHITE, projectName);
                canvasScreen.getCanvas().loadLatestAutoSave(projectName);
                parentFrame.getContentPane().add(canvasScreen);
                parentFrame.revalidate();
                parentFrame.repaint();
            }
            isPushed = false;
            return label;
        }

        private void deleteDirectoryRecursively(File dir) {
            if (dir.exists()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            deleteDirectoryRecursively(f);
                        } else {
                            f.delete();
                        }
                    }
                }
                dir.delete();
            }
        }
    }

    // Custom editor for the "Delete" button in the "Actions" column
    private class DeleteButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;

        public DeleteButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped()); // This is correct!
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
                // Get project name BEFORE modifying the table
                int row = projectTable.getSelectedRow();
                String projectName = (String) tableModel.getValueAt(row, 0);

                int confirm = JOptionPane.showConfirmDialog(parentFrame,
                        "Are you sure you want to delete project \"" + projectName + "\"?",
                        "Delete Project", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Remove from database and memory
                    projectDatabase.removeProject(projectName);

                    // Remove autosave folder
                    String savesRoot = System.getProperty("user.home") + File.separator + "kraska_saves";
                    String safeProjectName = projectName.replaceAll("[^a-zA-Z0-9\\-_]", "_");
                    File projectDir = new File(savesRoot, safeProjectName);
                    deleteDirectoryRecursively(projectDir);

                    // Refresh the table after deletion, but only after editing is fully stopped
                    SwingUtilities.invokeLater(() -> loadProjects());
                }
            }
            isPushed = false;
            return label;
        }

        private void deleteDirectoryRecursively(File dir) {
            if (dir.exists()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            deleteDirectoryRecursively(f);
                        } else {
                            f.delete();
                        }
                    }
                }
                dir.delete();
            }
        }
    }
}
