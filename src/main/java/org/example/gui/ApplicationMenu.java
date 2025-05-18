package org.example.gui;

import org.example.app.ActionManager;
import org.example.app.tool.AbstractTool;
import org.example.app.tool.ToolManager;
import org.example.gui.screen.DashboardScreen;
import org.example.app.file.AppFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class ApplicationMenu extends JMenuBar {
    private final ToolManager toolManager = ToolManager.getInstance();
    private final ActionManager actionManager = ActionManager.getInstance();

    public ApplicationMenu(JFrame parentFrame) {
        // File menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem saveAsFile = new JMenuItem("Save As..");
        JMenuItem returnToDashboard = new JMenuItem("Return to Dashboard");

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(saveAsFile);
        fileMenu.addSeparator();
        fileMenu.add(returnToDashboard);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");

        JMenuItem clearCanvas = new JMenuItem("Clear Canvas");
        JMenuItem resizeCanvas = new JMenuItem("Resize Canvas");

        editMenu.add(clearCanvas);
        editMenu.add(resizeCanvas);

        // View menu
        JMenu viewMenu = new JMenu("View");

        JMenuItem zoomIn = new JMenuItem("Zoom In");
        JMenuItem zoomOut = new JMenuItem("Zoom Out");
        JMenuItem zoomReset = new JMenuItem("Reset Zoom");

        viewMenu.add(zoomIn);
        viewMenu.add(zoomOut);
        viewMenu.add(zoomReset);

        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");

        for (AbstractTool tool : toolManager.getTools()) {
            JMenuItem item = new JMenuItem();
            // System.out.println(actionManager.getAction(tool.getName()));
            item.setAction(actionManager.getAction(tool.getName()));
            toolsMenu.add(item);
        }

        // Help menu
        JMenu helpMenu = new JMenu("Help");

        JMenuItem aboutKraska = new JMenuItem("About Kraska");

        aboutKraska.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                showAboutDialog((JFrame) window);
            }
        });

        helpMenu.add(aboutKraska);

        // Add the menus to the menu bar
        this.add(fileMenu);
        this.add(editMenu);
        this.add(viewMenu);
        this.add(toolsMenu);
        this.add(helpMenu);

        // The things happening when you click the things
        newFile.addActionListener(e -> {
            DashboardScreen.getInstance().openCanvasSettingsWindow(null);
        });

        openFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            // Her şey de gözükmesin
            javax.swing.filechooser.FileNameExtensionFilter imageFilter =
                    new javax.swing.filechooser.FileNameExtensionFilter(
                            "Image Files (*.png, *.jpg, *.jpeg, *.bmp, *.gif)",
                            "png", "jpg", "jpeg", "bmp", "gif");

            chooser.setFileFilter(imageFilter);
            chooser.setAcceptAllFileFilterUsed(false);

            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                AppFile.getInstance().open(chooser.getSelectedFile());
            }
        });

        saveFile.addActionListener(e -> {
            AppFile.getInstance().save();
        });


        saveAsFile.addActionListener(e -> {
           AppFile.getInstance().saveAs();
        });

        // Add action for returning to dashboard
        returnToDashboard.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new DashboardScreen(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
    }

    private void showAboutDialog(JFrame parent) {
        JDialog dialog = new JDialog(parent, "About", true);
        dialog.setLayout(new BorderLayout());

        JLabel bannerLabel = new JLabel();
        ImageIcon bannerIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/banner.png")));
        bannerLabel.setIcon(bannerIcon);
        dialog.add(bannerLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        infoPanel.add(centeredLabel("Yunus Erdem ERGÜL"));
        infoPanel.add(centeredLabel("Mehmet Emircan KÜLLÜCEK"));
        infoPanel.add(centeredLabel("Aleksei KHLOPKOV"));
        infoPanel.add(centeredLabel("Fariz Berke TUTANÇ"));
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(centeredLabel("Thanks to Muhammed Arif DAYI for beta testing"));
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(centeredLabel("Licensed under the GNU General Public License v3.0"));

        dialog.add(infoPanel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack(); // Resize to fit all contents
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private JLabel centeredLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
}
