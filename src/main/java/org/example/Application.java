package org.example;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import org.example.gui.ApplicationMenu;
import org.example.gui.screen.DashboardScreen;

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

        if (SystemInfo.isLinux) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
    }

    private JFrame createMainWindow() {
        JFrame frame = new JFrame("Kraska");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1600, 900);
        frame.setLayout(new BorderLayout());

        frame.setJMenuBar(new ApplicationMenu());

        // Show DashboardScreen first
        DashboardScreen dashboardScreen = new DashboardScreen(frame);
        frame.add(dashboardScreen, BorderLayout.CENTER);

        return frame;
    }
}
