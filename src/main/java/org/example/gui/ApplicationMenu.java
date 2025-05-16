package org.example.gui;

import org.example.app.ActionManager;
import org.example.app.tool.AbstractTool;
import org.example.app.tool.ToolManager;

import org.example.app.file.AppFile;

import javax.swing.*;
import java.awt.*;

public class ApplicationMenu extends JMenuBar {
    private final ToolManager toolManager = ToolManager.getInstance();
    private final ActionManager actionManager = ActionManager.getInstance();

    public ApplicationMenu() {
        // File menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem saveAsFile = new JMenuItem("Save As..");

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(saveAsFile);

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

        aboutKraska.addActionListener(e ->
                    JOptionPane.showMessageDialog(null,
                            "Kraska - We love to help you paint your dream waifu <3 \n Every Waifu Is Special -Farhandir",
                            "About Kraska",
                            JOptionPane.INFORMATION_MESSAGE));

        helpMenu.add(aboutKraska);

        // Add the menus to the menu bar
        this.add(fileMenu);
        this.add(editMenu);
        this.add(viewMenu);
        this.add(toolsMenu);
        this.add(helpMenu);

        // The things happening when you click the things
        newFile.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "We didn't code it yet");
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


    }

}
