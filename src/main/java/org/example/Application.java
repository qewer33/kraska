package org.example;

import org.example.gui.screen.CanvasScreen;

import javax.swing.*;
import java.awt.*;

public class Application {
    public void run() {
        JFrame mainWindow = createMainWindow();
        mainWindow.setVisible(true);
    }

    private JFrame createMainWindow() {
        JFrame frame = new JFrame("Kraska");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLayout(new BorderLayout());
        frame.add(new CanvasScreen(), BorderLayout.CENTER);
        return frame;
    }
}
