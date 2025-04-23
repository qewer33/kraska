package org.example;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.gui.screen.CanvasScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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

        CanvasScreen canvasScreen = new CanvasScreen();
        frame.add(canvasScreen, BorderLayout.CENTER);

        // Add a key listener for undo/redo
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
                    canvasScreen.getCanvas().undo(); // Trigger undo on CTRL+Z
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {
                    canvasScreen.getCanvas().redo(); // Trigger redo on CTRL+Y
                }
            }
        });

        // Request focus for the frame
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                frame.requestFocusInWindow();
            }
        });

        return frame;
    }
}
