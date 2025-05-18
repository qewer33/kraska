package org.example;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.util.SystemInfo;
import org.example.app.ActionManager;
import org.example.app.color.ColorManager;
import org.example.app.tool.ToolManager;
import org.example.gui.ApplicationMenu;
import org.example.gui.screen.DashboardScreen;
import org.example.gui.canvas.Canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class Application {
    public void run() {
        instanciateManagers();
        setupTheme();

        SwingUtilities.invokeLater(() -> {
            Canvas canvas = new Canvas();
            JFrame mainWindow = createMainWindow(canvas);
            mainWindow.setVisible(true);
        });
    }

    private void instanciateManagers() {
        ActionManager.getInstance();
        ColorManager.getInstance();
        ToolManager.getInstance();
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

    private JFrame createMainWindow(Canvas canvas) {
        JFrame frame = new JFrame("Kraska");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1600, 900);
        frame.setLayout(new BorderLayout());

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.png")));
        frame.setIconImage(icon.getImage());

        frame.setJMenuBar(new ApplicationMenu(frame));

        // Show DashboardScreen first
        DashboardScreen dashboardScreen = new DashboardScreen(frame);
        frame.add(dashboardScreen, BorderLayout.CENTER);
        ((ApplicationMenu) frame.getJMenuBar()).disableMenus();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                canvas.shutdownAutosave(); // Safe shutdown
                frame.dispose();
            }
        });

        return frame;
    }
}
