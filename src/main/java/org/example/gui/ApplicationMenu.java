package org.example.gui;

import javax.swing.*;

public class ApplicationMenu extends JMenuBar {

    public ApplicationMenu() {
        // File menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);

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

        JMenuItem brush = new JMenuItem("Brush");
        JMenuItem eraser = new JMenuItem("Eraser");
        JMenuItem colorPicker = new JMenuItem("Color Picker");

        toolsMenu.add(brush);
        toolsMenu.add(eraser);
        toolsMenu.add(colorPicker);

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
            System.out.println("Congrats! You've just pushed the fucking new button!");
        });

        openFile.addActionListener(e -> {
            System.out.println("Ow! You've decided to open an already exist file. Dumb mf");
        });

        saveFile.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                    "Wow so fucking quickly! What did you draw huh, Aleksei's dick or smth?");
        });


    }

}
