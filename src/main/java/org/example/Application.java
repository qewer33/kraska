package org.example;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.gui.screen.CanvasScreen;

import javax.swing.*;
import java.awt.*;

public class Application {
    public void run() {
        setupTheme();

        SwingUtilities.invokeLater(() -> {
            JFrame mainWindow = createMainWindow();
            mainWindow.setVisible(true);
        });
    }

    private void setupTheme() {
        try {
            // Set FlatLaf theme before any Swing components are created
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
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
