package org.example;

import javax.swing.*;

public class Application {
    public void run() {
        JFrame mainWindow = createMainWindow();
        mainWindow.setVisible(true);
    }

    private JFrame createMainWindow() {
        JFrame frame = new JFrame("Kraska");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        return frame;
    }
}
