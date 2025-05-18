package org.example.gui.screen;

import org.example.gui.ApplicationMenu;

import javax.swing.*;
import java.awt.*;

public class ScreenManager {
    private static ScreenManager instance;

    private final JFrame frame;
    private DashboardScreen dashboardScreen;
    private CanvasScreen canvasScreen;

    private ScreenManager(JFrame frame, DashboardScreen dashboardScreen) {
        this.frame = frame;
        this.dashboardScreen = dashboardScreen;
    }

    public static ScreenManager getInstance(JFrame frame, DashboardScreen dashboardScreen) {
        if (instance == null) {
            instance = new ScreenManager(frame, dashboardScreen);
        }
        return instance;
    }

    public static ScreenManager getInstance() {
        return instance;
    }

    public DashboardScreen getDashboardScreen() {
        return dashboardScreen;
    }

    public CanvasScreen getCanvasScreen() {
        return canvasScreen;
    }

    public void setDashboardScreen(DashboardScreen dashboardScreen) {
        this.dashboardScreen = dashboardScreen;
    }

    public void setCanvasScreen(CanvasScreen canvasScreen) {
        this.canvasScreen = canvasScreen;
    }

    public void switchScreen(AbstractScreen screen) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(screen, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();

        if (screen instanceof DashboardScreen) {
            ((ApplicationMenu) frame.getJMenuBar()).disableMenus();
            setDashboardScreen((DashboardScreen) screen);
        } else if (screen instanceof CanvasScreen) {
            ((ApplicationMenu) frame.getJMenuBar()).enableMenus();
            setCanvasScreen((CanvasScreen) screen);
        }
    }
}
