package org.example.gui.screen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.example.db.Project;
import org.example.db.ProjectDatabase;
import org.example.gui.screen.component.DashboardBannerPanel;

public class DashboardScreen extends AbstractScreen {
    private static DashboardScreen instance;

    private final JFrame parentFrame;
    private final JPanel projectsPanel;
    private ProjectDatabase projectDatabase = ProjectDatabase.getInstance();
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DashboardScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        DashboardScreen.instance = this;
        setLayout(new BorderLayout());

        // Setup banner
        DashboardBannerPanel bannerPanel = new DashboardBannerPanel();
        bannerPanel.newProjectButton.addActionListener(this::openCanvasSettingsWindow);
        add(bannerPanel, BorderLayout.NORTH);

        // Panel to display project cards
        projectsPanel = new JPanel();
        projectsPanel.setLayout(new BoxLayout(projectsPanel, BoxLayout.Y_AXIS));
        projectsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(projectsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Load existing projects into the panel
        loadProjects();

        // Add projects panel to the center of the layout
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadProjects() {
        projectsPanel.removeAll();
        List<Project> projects = projectDatabase.getProjects();

        for (Project project : projects) {
            projectsPanel.add(createProjectCard(project));
            projectsPanel.add(Box.createVerticalStrut(12));
        }

        projectsPanel.revalidate();
        projectsPanel.repaint();
    }

    private JPanel createProjectCard(Project project) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 180, 180), 1, true),
                new EmptyBorder(18, 24, 18, 24) // increased top/bottom padding
        ));
        card.setBackground(new Color(250, 250, 250, 230));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110)); // increased height

        // Left: Project info
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(project.getName());
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        JLabel createdLabel = new JLabel("Created: " + formatDate(project.getCreated()));
        JLabel updatedLabel = new JLabel("Last Opened: " + formatDate(project.getLastOpened()));
        createdLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        updatedLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(createdLabel);
        infoPanel.add(updatedLabel);
        infoPanel.add(Box.createVerticalStrut(2)); // add a small gap after the last label

        // Right: Action buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        JButton loadButton = new JButton("Load");
        JButton deleteButton = new JButton("Delete");

        // Make buttons bigger
        Dimension buttonSize = new Dimension(110, 36);
        Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 15);
        loadButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        loadButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);

        // Center buttons vertically and stack them with a gap
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0); // 20px gap between buttons
        gbc.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(loadButton, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        buttonPanel.add(deleteButton, gbc);

        loadButton.addActionListener(e -> {
            projectDatabase.updateLastOpened(project.getName(), java.time.LocalDateTime.now().toString());
            parentFrame.getContentPane().removeAll();
            CanvasScreen canvasScreen = new CanvasScreen(parentFrame, 800, 600, Color.WHITE, project.getName());
            canvasScreen.getCanvas().loadLatestAutoSave(project.getName());
            parentFrame.getContentPane().add(canvasScreen);
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                    "Are you sure you want to delete project \"" + project.getName() + "\"?",
                    "Delete Project", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                projectDatabase.removeProject(project.getName());
                // Remove autosave folder
                String savesRoot = System.getProperty("user.home") + File.separator + "kraska_saves";
                String safeProjectName = project.getName().replaceAll("[^a-zA-Z0-9\\-_]", "_");
                File projectDir = new File(savesRoot, safeProjectName);
                deleteDirectoryRecursively(projectDir);
                loadProjects();
            }
        });

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private String formatDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr).format(DISPLAY_FORMATTER);
        } catch (Exception ex) {
            return dateStr;
        }
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

    public void openCanvasSettingsWindow(ActionEvent e) {
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
        JSpinner widthField = new JSpinner(new SpinnerNumberModel(800, 1, Integer.MAX_VALUE, 1));
        settingsDialog.add(widthField, gbc);

        // Height input
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        settingsDialog.add(new JLabel("Canvas Height:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        JSpinner heightField = new JSpinner(new SpinnerNumberModel(600, 1, Integer.MAX_VALUE, 1));
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
                int width = (int) widthField.getValue();
                int height = (int)heightField.getValue();
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
                CanvasScreen canvasScreen = new CanvasScreen(parentFrame, width, height, backgroundColor, projectName);
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

    public static DashboardScreen getInstance() {return instance;}

    public static void setInstance(DashboardScreen screen) {instance = screen;}
}
