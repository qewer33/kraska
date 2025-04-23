package org.example.gui;

import javax.swing.*;

public class ApplicationMenu extends JMenuBar {

    public ApplicationMenu() {
    //The file section
        JMenu fileMenu = new JMenu("File");

        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
    //Help menu
        JMenu helpMenu = new JMenu("Help");

        JMenuItem aboutKraska = new JMenuItem("About Kraska");

        aboutKraska.addActionListener(e ->
                    JOptionPane.showMessageDialog(null,
                            "Kraska - We love to help you paint your dream waifu <3 \n Every Waifu Is Special -Farhandir",
                            "About Kraska",
                            JOptionPane.INFORMATION_MESSAGE));

        helpMenu.add(aboutKraska);

        //Add the menus to the menu bar
        this.add(fileMenu);
        this.add(helpMenu);

        //The fucking things happening when you click the fucking things

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
