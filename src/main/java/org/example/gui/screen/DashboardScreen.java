package org.example.gui.screen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.example.db.Project;
import org.example.db.ProjectDatabase;
import org.example.gui.ApplicationMenu;
import org.example.gui.screen.component.DashboardBannerPanel;
import org.example.gui.screen.component.WrapLayout;

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
        bannerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        add(bannerPanel, BorderLayout.NORTH);

        // Panel to display project cards
        projectsPanel = new JPanel();
        projectsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        projectsPanel.setLayout(new WrapLayout(FlowLayout.CENTER, 6, 6));
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
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // 20px arc radius
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.LIGHT_GRAY); // border color
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        card.setPreferredSize(new Dimension(300, 285));
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setBackground(new Color(255, 255, 255));

        // Thumbnail on top (centered and square, not stretched)
        try {
            ImageIcon originalIcon = new ImageIcon(project.getLatestAutosave().getAbsolutePath());
            Image originalImage = originalIcon.getImage();
            int imgWidth = originalImage.getWidth(null);
            int imgHeight = originalImage.getHeight(null);
            double targetAspect = 16.0 / 9.0;

            int cropWidth = imgWidth;
            int cropHeight = (int) (imgWidth / targetAspect);

            if (cropHeight > imgHeight) {
                cropHeight = imgHeight;
                cropWidth = (int) (imgHeight * targetAspect);
            }

            int cropX = (imgWidth - cropWidth) / 2;
            int cropY = (imgHeight - cropHeight) / 2;

            BufferedImage cropped = new BufferedImage(cropWidth, cropHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = cropped.createGraphics();
            g.drawImage(originalImage,
                    0, 0, cropWidth, cropHeight,
                    cropX, cropY, cropX + cropWidth, cropY + cropHeight,
                    null);
            g.dispose();

            int finalWidth = 280; // card width - 2 * 10 padding
            int finalHeight = (int) (finalWidth * 9.0 / 16.0);
            Image scaled = cropped.getScaledInstance(finalWidth, finalHeight, Image.SCALE_SMOOTH);

            JLabel thumbnail = new JLabel(new ImageIcon(scaled));
            thumbnail.setHorizontalAlignment(SwingConstants.CENTER);
            thumbnail.setPreferredSize(new Dimension(finalWidth, finalHeight));

            card.add(thumbnail, BorderLayout.NORTH);

        } catch (Exception e) {
            JLabel fallback = new JLabel("No Preview", SwingConstants.CENTER);
            fallback.setPreferredSize(new Dimension(280, 158));
            fallback.setOpaque(true);
            fallback.setBackground(Color.LIGHT_GRAY);
            card.add(fallback, BorderLayout.NORTH);
        }

        // Info section
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(project.getName());
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel createdLabel = new JLabel("Created: " + formatDate(project.getCreated()));
        createdLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        createdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel updatedLabel = new JLabel("Last Opened: " + formatDate(project.getLastOpened()));
        updatedLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        updatedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(createdLabel);
        infoPanel.add(updatedLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Buttons section
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        ImageIcon loadIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/dashboard/load.png")));
        JButton loadButton = new JButton("Load");
        loadButton.setIcon(loadIcon);
        ImageIcon deleteIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/dashboard/delete.png")));
        JButton deleteButton = new JButton("Delete");
        deleteButton.setIcon(deleteIcon);
        buttons.add(loadButton);
        buttons.add(deleteButton);

        card.add(buttons, BorderLayout.SOUTH);

        // Load button logic
        loadButton.addActionListener(e -> {
            projectDatabase.updateLastOpened(project.getName(), java.time.LocalDateTime.now().toString());
            parentFrame.getContentPane().removeAll();
            ImageIcon image = new ImageIcon(project.getLatestAutosave().getAbsolutePath());
            CanvasScreen canvasScreen = new CanvasScreen(parentFrame, image.getIconWidth(), image.getIconHeight(), Color.WHITE, project);
            canvasScreen.getCanvas().loadLatestAutoSave(project);
            parentFrame.getContentPane().add(canvasScreen);
            parentFrame.revalidate();
            parentFrame.repaint();
            ((ApplicationMenu) parentFrame.getJMenuBar()).enableMenus();
        });

        // Delete button logic
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                    "Are you sure you want to delete project \"" + project.getName() + "\"?",
                    "Delete Project", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                projectDatabase.removeProject(project.getName());
                String savesRoot = System.getProperty("user.home") + File.separator + "kraska_saves";
                String safeProjectName = project.getName().replaceAll("[^a-zA-Z0-9\\-_]", "_");
                File projectDir = new File(savesRoot, safeProjectName);
                deleteDirectoryRecursively(projectDir);
                loadProjects();
            }
        });

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

                Project project = new Project(projectName, LocalDateTime.now().toString(), LocalDateTime.now().toString());
                projectDatabase.addProject(project);

                parentFrame.getContentPane().removeAll();
                CanvasScreen canvasScreen = new CanvasScreen(parentFrame, width, height, backgroundColor, project);
                parentFrame.getContentPane().add(canvasScreen);
                parentFrame.revalidate();
                parentFrame.repaint();
                ((ApplicationMenu) parentFrame.getJMenuBar()).enableMenus();

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
